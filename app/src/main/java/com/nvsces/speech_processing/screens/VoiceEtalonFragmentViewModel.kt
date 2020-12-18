package com.nvsces.speech_processing.screens

import android.os.Build
import android.os.FileUtils
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nvsces.mynative.APP_ACTIVITY
import com.nvsces.mynative.REPOSITORY
import com.nvsces.speech_processing.R
import com.nvsces.speech_processing.WavFile.WavFile
import com.nvsces.speech_processing.models.AppMediaFile
import com.nvsces.speech_processing.utils.AppVoiceRecord
import com.nvsces.speech_processing.utils.short2DoubleArrayNorm
import com.nvsces.speech_processing.utils.splitDataWavChannelsToFloat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.ceil
import kotlin.math.roundToInt

class VoiceEtalonFragmentViewModel:ViewModel() {

    private lateinit var mFile:File
    private var mStandardList = ArrayList<DoubleArray>()
    private lateinit var dataWavOne:DoubleArray
    private var voiceRecorder: AppVoiceRecord? = null

    fun initRecorder(){
        voiceRecorder = AppVoiceRecord(16000)
        voiceRecorder?.prepare(2048)
    }

    fun startRecorder(){
        voiceRecorder?.start{
            mStandardList.add(short2DoubleArrayNorm(it))
        }
    }

    fun stopRecorder(onSuccess: (standard: ArrayList<DoubleArray>) -> Unit){
        voiceRecorder?.stop()
        onSuccess(mStandardList)
    }

    fun releaseRecorder(){
        voiceRecorder?.stop()
        voiceRecorder?.release()
    }

    fun createFileWav(filenameSave: String)=
            viewModelScope.launch(Dispatchers.IO) {
                val mediaFile=filenameSave+"_media"
                mFile = File(APP_ACTIVITY.cacheDir, mediaFile)
                mFile.createNewFile()
                val n= mStandardList[0].size*mStandardList.size
                val ff=WavFile.newWavFile(mFile, 1, n.toLong(), 24, 16000)
                val writeData=DoubleArray(n)
                for (i in 0 until mStandardList.size){
                    System.arraycopy(mStandardList[i], 0, writeData,
                            i * mStandardList[i].size, mStandardList[i].size)
                }
                ff.writeFrames(writeData, writeData.size)
                ff.close()
                REPOSITORY.insert(AppMediaFile(name = filenameSave,mediaUrl = mediaFile)) {}
            }

    fun addStandardList(element:DoubleArray){
        mStandardList.add(element)
    }

    fun clearStandardList(){
        mStandardList.clear()
    }

    fun readWavFileData(filename:String, onSuccess: (standard:ArrayList<DoubleArray>) -> Unit)=
            viewModelScope.launch(Dispatchers.IO){

                    mFile = File(APP_ACTIVITY.cacheDir, filename + "_media")
                    val wavFile = WavFile.openWavFile(mFile)
                    val numFrames = wavFile.numFrames.toInt()
                    val sampleBuffer = DoubleArray(numFrames * wavFile.numChannels)
                    wavFile.readFrames(sampleBuffer, numFrames)

                    if (wavFile.numChannels == 2) {
                        dataWavOne = splitDataWavChannelsToFloat(sampleBuffer)[0]
                    } else {
                        dataWavOne = sampleBuffer
                    }
                    val sizeBufferList: Int = ceil((dataWavOne.size / 2048).toDouble()).roundToInt()
                    for (i in 0 until sizeBufferList) {
                        val temp = DoubleArray(2048)
                        System.arraycopy(dataWavOne, i * 2048, temp, 0, 2048)
                        mStandardList.add(temp)
                    }
                    onSuccess(mStandardList)

            }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun readRawFile(){
        val inputStream: InputStream = APP_ACTIVITY.getResources()
                .openRawResource(R.raw.mifasol)
        mFile = File(APP_ACTIVITY.filesDir, "pid.wav")
        mFile.createNewFile()
        val outputStream = FileOutputStream(mFile)
        FileUtils.copy(inputStream, outputStream)
    }
}