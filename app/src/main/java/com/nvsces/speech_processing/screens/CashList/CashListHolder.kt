package com.nvsces.speech_processing.screens.CashList

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nvsces.mynative.APP_ACTIVITY
import com.nvsces.mynative.REPOSITORY
import com.nvsces.mynative.nameCurrentFile
import com.nvsces.speech_processing.models.AppMediaFile
import com.nvsces.speech_processing.screens.VoiceEtalonFragment
import com.nvsces.speech_processing.utils.AppVoicePlayer
import com.nvsces.speech_processing.utils.replaceFragment
import kotlinx.android.synthetic.main.item_file_name.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class CashListHolder(view: View): RecyclerView.ViewHolder(view){

    private val mAppVoicePlayer= AppVoicePlayer()

    val nameFile: TextView =view.file_name
    val btnPlay: ImageView =view.file_btn_play
    val btnPause:ImageView=view.file_btn_pause
    val btnDelete: ImageView =view.file_btn_delete

    fun onAttach(mediaItem: AppMediaFile){
        mAppVoicePlayer.init()
        btnPlay.setOnClickListener {
            btnPause.visibility=View.VISIBLE
            btnPlay.visibility=View.INVISIBLE
            play(mediaItem){
                btnPause.visibility=View.INVISIBLE
                btnPlay.visibility=View.VISIBLE
            }
            btnPause.setOnClickListener {
                btnPause.visibility=View.INVISIBLE
                btnPlay.visibility=View.VISIBLE
                stop {
                }
            }
        }
        nameFile.setOnClickListener {
            nameCurrentFile=mediaItem.name
            replaceFragment(VoiceEtalonFragment(),false)
        }
        btnDelete.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                REPOSITORY.delete(mediaItem){
                    deleteFile(mediaItem.mediaUrl)
                }
            }
        }
    }

    private fun deleteFile(filename:String) {
        val fileDelete=File(APP_ACTIVITY.cacheDir,filename)
        fileDelete.delete()
    }

    private fun play(file: AppMediaFile, function: () -> Unit) {
        mAppVoicePlayer.play(file.mediaUrl){
            function()
        }
    }

    private fun stop(function: () -> Unit){
        mAppVoicePlayer.stop { function()  }
    }

    fun onDetach() {
        nameFile.setOnClickListener(null)
        mAppVoicePlayer.release()
    }
}