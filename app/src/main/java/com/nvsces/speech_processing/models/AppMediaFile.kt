package com.nvsces.speech_processing.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "media_file_tables")
data class AppMediaFile (
    @PrimaryKey(autoGenerate = true) val id:Int=0,
    @ColumnInfo val name:String="",
    @ColumnInfo val mediaUrl:String=""
    ): Serializable
