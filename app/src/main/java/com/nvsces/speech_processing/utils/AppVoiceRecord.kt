package com.nvsces.speech_processing.utils

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import com.nvsces.mynative.currentRun
import com.nvsces.mynative.etalonRun
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppVoiceRecord(private val fs:Int) {

    private var audioRecord: AudioRecord? = null
    private var recordLength = 0
    private var isRecorder=false

    fun prepare(multiple: Int) {
        val BYTES_PER_SHORT = 2
        recordLength = AudioRecord.getMinBufferSize(fs, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT) / BYTES_PER_SHORT
        val r = recordLength % multiple
        if (r > 0) recordLength += (multiple - r)
        audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                fs,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                recordLength * BYTES_PER_SHORT
        )
    }

    fun start(function:(audioData:ShortArray)->Unit)= CoroutineScope(Dispatchers.IO).launch {
        audioRecord?.startRecording()
        isRecorder=true
        while (isRecorder){
            val recordBuffer=ShortArray(recordLength)
            audioRecord!!.read(recordBuffer,0,recordLength)
            function(recordBuffer)
        }
    }

    fun stop() {
        isRecorder=false
        audioRecord!!.stop()
    }

    fun release() { if (!isRecorder && audioRecord != null) audioRecord!!.release()   }
}