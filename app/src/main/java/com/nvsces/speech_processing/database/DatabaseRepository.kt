package com.nvsces.speech_processing.database

import androidx.lifecycle.LiveData
import com.nvsces.speech_processing.models.AppMediaFile

interface DatabaseRepository {
    val allMediaFile:LiveData<List<AppMediaFile>>
    suspend fun insert(fileMedia: AppMediaFile,onSuccess:()->Unit)
    suspend fun delete(fileMedia: AppMediaFile,onSuccess:()->Unit)

    fun connectToDatabase(onSuccess: () -> Unit,onFail:(String)->Unit){}

    fun signOut(){}

}