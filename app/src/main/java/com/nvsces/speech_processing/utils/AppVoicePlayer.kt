package com.nvsces.speech_processing.utils

import android.media.MediaPlayer
import android.util.Log
import com.nvsces.mynative.APP_ACTIVITY
import java.io.File
import java.lang.Exception

class AppVoicePlayer {

    private lateinit var mMediaPlayer: MediaPlayer
    private lateinit var mFile:File

    fun play(messageKey:String,function: () -> Unit) {
        mFile= File(APP_ACTIVITY.cacheDir,messageKey)
        if (mFile.exists() && mFile.length()>0 && mFile.isFile) {
            startPlay() {
                function()
            }
        }
    }


    private fun startPlay(function: () -> Unit) {
        try {
            mMediaPlayer.setDataSource(mFile.absolutePath)
            mMediaPlayer.prepare()
            mMediaPlayer.start()
            Log.d("TEST","mediaStart")
            mMediaPlayer.setOnCompletionListener {
                stop{
                    function()
                }
            }
        }catch (e:Exception){
            showToast(e.message.toString())
        }
    }

fun stop(function: () -> Unit) {
        try {
            mMediaPlayer.stop()
            mMediaPlayer.reset()
            function()
        }catch (e:Exception){
            showToast(e.message.toString())
        }
    }

    fun release(){
        mMediaPlayer.release()
    }
    fun init(){
        mMediaPlayer=MediaPlayer()
    }
}