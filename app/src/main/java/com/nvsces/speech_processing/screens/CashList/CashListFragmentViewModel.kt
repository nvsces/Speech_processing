package com.nvsces.speech_processing.screens.CashList

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.nvsces.mynative.REPOSITORY

class CashListFragmentViewModel:ViewModel() {
    val allNotes= REPOSITORY.allMediaFile
    fun signOut(){
        REPOSITORY.signOut()
    }
}