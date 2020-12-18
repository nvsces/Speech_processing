package com.nvsces.speech_processing.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nvsces.speech_processing.models.AppMediaFile

@Dao
interface AppRoomDao {
    @Query("SELECT * from media_file_tables")
    fun getAllmediaFiles():LiveData<List<AppMediaFile>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(fileMedia: AppMediaFile)

    @Delete
    suspend fun delete(fileMedia: AppMediaFile)
}