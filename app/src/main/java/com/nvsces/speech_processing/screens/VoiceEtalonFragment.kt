package com.nvsces.speech_processing.screens

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.FileUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.annotation.RequiresApi
import com.nvsces.mynative.*
import com.nvsces.speech_processing.R
import com.nvsces.speech_processing.WavFile.WavFile
import com.nvsces.speech_processing.databinding.FragmentVoiceEtalonBinding
import com.nvsces.speech_processing.utils.*
import kotlinx.android.synthetic.main.fragment_voice_etalon.*
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.ceil
import kotlin.math.roundToInt

class VoiceEtalonFragment : Fragment() {

    private var mEtalonList = ArrayList<DoubleArray>()
    private lateinit var dataWavOne:DoubleArray

    private var Voicerecorder: VoiceRecord? = null
    private var _binding: FragmentVoiceEtalonBinding? = null
    private val mBinding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVoiceEtalonBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Voicerecorder = VoiceRecord(16000)
        Voicerecorder?.prepare(2048)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onResume() {
        super.onResume()
        initButton()
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("ClickableViewAccessibility")
    private fun initButton() {

        mBinding.btnVoiceEtalon.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                btn_voice_etalon.setBackgroundResource(R.drawable.ic_voice_blue)
                etalonRun = true
                mEtalonList.clear()
                Voicerecorder?.start() { }
            } else if (event.action == MotionEvent.ACTION_UP) {
                btn_voice_etalon.setBackgroundResource(R.drawable.ic_voice)
                etalonRun = false
                Voicerecorder?.stop()
                mEtalonList.addAll(shortArrayListToDoubleArrayList(Voicerecorder!!.etalonList))
                Voicerecorder!!.etalonList.clear()
                drawViewFragment=true
               // replaceFragment(DisplayingFeaturesFragment(mEtalonList))
                replaceFragment(DisplayingFeaturesFragment(mEtalonList))
            }
            true
        }


        mBinding.voiceEtalonBtnCashEtalon.setOnClickListener {
            val inputStream: InputStream = APP_ACTIVITY.getResources()
                .openRawResource(R.raw.mifasol)
            mFile = File(APP_ACTIVITY.filesDir, "pid.wav")
            mFile.createNewFile()
            val outputStream = FileOutputStream(mFile)
            FileUtils.copy(inputStream, outputStream)
            val wavFile = WavFile.openWavFile(mFile)

            val numFrames = wavFile.numFrames.toInt()
            val sampleBuffer = DoubleArray(numFrames * wavFile.numChannels)
            wavFile.readFrames(sampleBuffer, numFrames)

            if (wavFile.numChannels==2) {
                dataWavOne = splitDataWavChannelsToFloat(sampleBuffer)[0]
            } else {
                dataWavOne=sampleBuffer
            }
           // mEtalonList = floatArraySplitSample(dataWavOne, fftResolution)

            val sizeBufferList:Int= ceil((dataWavOne.size/2048).toDouble()).roundToInt()
            for (i in 0 until sizeBufferList){
                val temp=DoubleArray(2048)
                System.arraycopy(dataWavOne,i*2048,temp,0,2048)
                mEtalonList.add(temp)
            }
            drawViewFragment=true
           replaceFragment(DisplayingFeaturesFragment(mEtalonList))
        }
    }
}