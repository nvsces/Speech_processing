package com.nvsces.mynative

import com.nvsces.speech_processing.MainActivity
import com.nvsces.speech_processing.database.DatabaseRepository
import java.io.File

val MFCC="mfcc"
val BASIC_TONE="tone"
val SPECTROGRAMM="spectrogramm"

//lateinit var mFile:File
lateinit var REPOSITORY: DatabaseRepository
lateinit var APP_ACTIVITY: MainActivity
var nameCurrentFile="cash"
var samplingRate=16000
var fftResolution=2048
var numFilters=13

var drawViewFragment=false

var mfcc_realtime=false
var frameSizeEdit=1f

var spectrogrammRecord=false
