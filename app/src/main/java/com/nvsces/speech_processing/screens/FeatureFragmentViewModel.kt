package com.nvsces.speech_processing.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nvsces.mynative.*
import com.nvsces.speech_processing.createComplex
import com.nvsces.speech_processing.feature.FFTnew
import com.nvsces.speech_processing.feature.sonopy.Sonopy
import com.nvsces.speech_processing.pef
import com.nvsces.speech_processing.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FeatureFragmentViewModel: ViewModel()  {
    private lateinit var listBuffer: ArrayList<DoubleArray>
    var bufferBasicToneRealTime = ArrayList<Float>()
    private var appRecorder:AppVoiceRecord?=null
    private var bufferMFCCRealTime = ArrayList<FloatArray>()
    private var listMFCC = ArrayList<Array<FloatArray>>()
    private var listMFCCout = ArrayList<FloatArray>()
    private lateinit var sonopy: Sonopy
    private var numberFrame = 0
    private val bufferStack = Array<ShortArray>(3) { ShortArray(2048) }


    fun setListBuffer(list: ArrayList<DoubleArray>) {
        listBuffer = list
        sonopy = Sonopy(samplingRate, listBuffer[0].size,
                0, fftResolution / 4, numFilters)
    }

    fun initVoiceRecord(fs: Int) {
        appRecorder = AppVoiceRecord(fs)
        appRecorder?.prepare(2048)
    }

    fun stopRecorder() {
        appRecorder?.stop()
    }

    fun startRecordBasicTone(onSuccess: (ArrayList<Float>) -> Unit,onFinished: () -> Unit) {
        bufferBasicToneRealTime.clear()
        appRecorder?.start {recordBuffer ->
            if (bufferBasicToneRealTime.size==listBuffer.size) {
                appRecorder?.stop()
                onFinished()
            }
            realTimeBasicToneProcessing(recordBuffer) {
                onSuccess(it)
            }
        }
    }
    fun startRecordMFCC(onSuccess: (ArrayList<FloatArray>) -> Unit,onFinished: () -> Unit) {
        mfcc_realtime=true
        bufferMFCCRealTime.clear()
        appRecorder?.start { recordBuffer ->
            if (bufferMFCCRealTime.size==listBuffer.size) {
                appRecorder?.stop()
                onFinished()
            }
            realTimeMFCCProcessing(recordBuffer) {
                onSuccess(it)
            }
        }
    }
    fun startRecordSpectrogram(onSuccess: (FloatArray) -> Unit){
        appRecorder?.start { recordBuffer ->
            realTimeSpectrogramProcessing(recordBuffer) {
                onSuccess(it)
            }
        }
    }

    private fun realTimeMFCCProcessing(recordBuffer: ShortArray, onSuccess: (ArrayList<FloatArray>) -> Unit)=
            viewModelScope.launch(Dispatchers.IO){
        val mfcc = sonopy.mfccSpec(short2FloatArray(recordBuffer), numFilters)
        bufferMFCCRealTime.add(deleteFrameIsMFCC(mfcc))
        onSuccess(bufferMFCCRealTime)
    }
    private fun realTimeBasicToneProcessing(recordBuffer: ShortArray, onSuccess: (ArrayList<Float>) -> Unit) =
            viewModelScope.launch(Dispatchers.IO) {
                val tonTemp = pef(short2DoubleArray(recordBuffer), 16000, true)
                bufferBasicToneRealTime.add(tonTemp[0])
                onSuccess(bufferBasicToneRealTime)
            }
    private fun realTimeSpectrogramProcessing(recordBuffer: ShortArray, onSuccess: (FloatArray) -> Unit) =
            viewModelScope.launch(Dispatchers.IO) {
                val n = recordBuffer.size
                val fftBuffer = ShortArray(n)

                for (i in 0 until bufferStack.size - 1)
                    System.arraycopy(recordBuffer, n / 2 * i, bufferStack[i + 1], 0, n / 2)

                for (i in 0 until bufferStack.size - 1) {
                    System.arraycopy(bufferStack[i], 0, fftBuffer, 0, n / 2)
                    System.arraycopy(bufferStack[i + 1], 0, fftBuffer, n / 2, n / 2)
                    val complexBuffer = createComplex(short2FloatArray(fftBuffer), 2048)
                    val magnitude = FFTnew.fft(complexBuffer).map { it.abs() }
                    val maxmag = maxValueAbs(magnitude)
                    val listOnsuccess = doubleArrayToFloatArray(magnitude.map { it / maxmag })
                    onSuccess(listOnsuccess)
                }
            }

    fun initBasicTone(onSuccess: (ArrayList<Float>) -> Unit, onFinished: () -> Unit) =
            viewModelScope.launch(Dispatchers.IO) {
                val arrayTon = ArrayList<Float>()
                for (i in 0 until listBuffer.size step 1) {
                    val tonTemp = pef(listBuffer[i], 16000, true)
                    arrayTon.add(tonTemp[0])
                    onSuccess(arrayTon)
                }
                onFinished()
            }
    fun initMFCC(onSuccess: (ArrayList<FloatArray>) -> Unit) =
            viewModelScope.launch(Dispatchers.IO) {
                for (i in 0 until listBuffer.size) {
                    val mfcc = sonopy.mfccSpec(doubleArrayToFloatArray(listBuffer[i]), numFilters)
                    listMFCC.add(mfcc)
                }
                listMFCCout.addAll(deleteFrameListIsMFCC(listMFCC))
                onSuccess(listMFCCout)
            }
}