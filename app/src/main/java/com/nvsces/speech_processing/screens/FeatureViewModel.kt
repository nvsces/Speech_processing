package com.nvsces.speech_processing.screens

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

class FeatureViewModel : ViewModel() {
    private lateinit var listBuffer: ArrayList<DoubleArray>
    var bufferBasicToneRealTime = ArrayList<Float>()
    private var voiceRecord: VoiceRecord? = null
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
        voiceRecord = VoiceRecord(fs)
        voiceRecord?.prepare(2048)
    }

    fun checkRecording(method: String, onSuccess: () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        when (method) {
            BASIC_TONE -> numberFrame = bufferBasicToneRealTime.size
            MFCC -> numberFrame = bufferMFCCRealTime.size
        }
        while (streamRealTime) {
            if (numberFrame == listBuffer.size) {
                stopRecord()
                onSuccess()
            }
        }
    }

    fun stopRecord() {
        currentRun = false
        streamRealTime = false
        voiceRecord?.stop()
    }

    fun startRecordBasicTone(onSuccess: (ArrayList<Float>) -> Unit) {
        currentRun = true
        streamRealTime = true
        bufferBasicToneRealTime.clear()
        voiceRecord?.startTime() { recordBuffer ->
            basicToneProcessing(recordBuffer) {
                onSuccess(it)
            }
        }
    }

    fun startRecordMFCC(onSuccess: (ArrayList<FloatArray>) -> Unit) {
        currentRun = true
        streamRealTime = true
        bufferMFCCRealTime.clear()
        voiceRecord?.startTime() { recordBuffer ->
            mfccRealTimeProcessing(recordBuffer) {
                onSuccess(it)
            }
        }
    }

    private fun mfccRealTimeProcessing(recordBuffer: ShortArray, onSuccess: (ArrayList<FloatArray>) -> Unit) {
        val mfcc = sonopy.mfccSpec(short2FloatArray(recordBuffer), numFilters)
        bufferMFCCRealTime.add(deleteFrameIsMFCC(mfcc))
        mfcc_realtime = true
        numberFrame = bufferMFCCRealTime.size
        onSuccess(bufferMFCCRealTime)
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

    private fun basicToneProcessing(recordBuffer: ShortArray, onSuccess: (ArrayList<Float>) -> Unit) =
            viewModelScope.launch(Dispatchers.IO) {
                val tonTemp = pef(short2DoubleArray(recordBuffer), 16000, true)
                bufferBasicToneRealTime.add(tonTemp[0])
                numberFrame = bufferBasicToneRealTime.size
                onSuccess(bufferBasicToneRealTime)
            }

    fun initBasicTone(onSuccess: (ArrayList<Float>) -> Unit, onFinished: () -> Unit) =
            viewModelScope.launch(Dispatchers.IO) {
                val arrayTon = ArrayList<Float>()
                for (i in 0 until listBuffer.size step 1) {
                    val tonTemp = pef(listBuffer[i], 44100, true)
                    arrayTon.add(tonTemp[0])
                    onSuccess(arrayTon)
                }
                onFinished()
            }


    private fun spectrogrammProcessing(recordBuffer: ShortArray, onSuccess: (FloatArray) -> Unit) =
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


    fun startRecordSpectrogramm(onSuccess: (FloatArray) -> Unit) {
        currentRun = true
        streamRealTime = true
        voiceRecord?.startTime() { recordBuffer ->
            spectrogrammProcessing(recordBuffer) {
                onSuccess(it)
            }
        }
    }
}