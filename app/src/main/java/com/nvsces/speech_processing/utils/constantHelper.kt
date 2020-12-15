package com.nvsces.mynative

import com.nvsces.speech_processing.MainActivity
import java.io.File

val MFCC="mfcc"
val BASIC_TONE="tone"
val SPECTROGRAMM="spectrogramm"

lateinit var mFile:File

lateinit var APP_ACTIVITY: MainActivity
var samplingRate=16000
var fftResolution=2048
var numFilters=13

var drawViewFragment=false

var mfcc_realtime=false
var frameSizeEdit=1f


var currentRun=false
var etalonRun=false

var streamRealTime=true

var realtimebolean=false

var spectrogrammRecord=false

const val STATE_DEFAULT=0
const val STATE_ENERGY=1