package com.nvsces.speech_processing.screens.CashList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nvsces.speech_processing.R
import com.nvsces.speech_processing.models.AppMediaFile
import com.nvsces.speech_processing.screens.VoiceEtalonFragment
import com.nvsces.speech_processing.utils.AppVoicePlayer
import com.nvsces.speech_processing.utils.replaceFragment
import kotlinx.android.synthetic.main.item_file_name.view.*

class CashListAdapter: RecyclerView.Adapter<CashListHolder>() {

    private var mListMediaFiles= emptyList<AppMediaFile>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CashListHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_file_name,parent,false)
        return CashListHolder(view)
    }

    override fun onViewAttachedToWindow(holder: CashListHolder) {
        holder.onAttach(mListMediaFiles[holder.adapterPosition])
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: CashListHolder) {
        holder.onDetach()
        super.onViewDetachedFromWindow(holder)
    }
    override fun onBindViewHolder(holder: CashListHolder, position: Int) {
        holder.nameFile.text=mListMediaFiles[position].name
    }

    override fun getItemCount(): Int =mListMediaFiles.size

    fun setList(list: List<AppMediaFile>){
        mListMediaFiles=list
        notifyDataSetChanged()
    }

}