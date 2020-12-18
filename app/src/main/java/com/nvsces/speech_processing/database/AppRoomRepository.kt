package com.nvsces.speech_processing.database

import androidx.lifecycle.LiveData
import com.nvsces.speech_processing.models.AppMediaFile

class AppRoomRepository(private val appRoomDao:AppRoomDao):DatabaseRepository {
    override val allMediaFile: LiveData<List<AppMediaFile>>
        get() = appRoomDao.getAllmediaFiles()

    override suspend fun insert(fileMedia: AppMediaFile, onSuccess: () -> Unit) {
        appRoomDao.insert(fileMedia)
        onSuccess()
    }

    override suspend fun delete(fileMedia: AppMediaFile, onSuccess: () -> Unit) {
        appRoomDao.delete(fileMedia)
        onSuccess()
    }

    override fun signOut() {
        super.signOut()
    }
}