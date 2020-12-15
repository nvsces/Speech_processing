package com.nvsces.speech_processing.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nvsces.mynative.*
import com.nvsces.speech_processing.databinding.FragmentDisplayingFeaturesBinding
import com.nvsces.speech_processing.screens.menu.method
import com.nvsces.speech_processing.screens.menu.navigationMenuClick

class DisplayingFeaturesFragment (val listBuffer: ArrayList<DoubleArray>) : Fragment() {

    private var _binding: FragmentDisplayingFeaturesBinding?=null
    private val mBinding get() = _binding!!
    private lateinit var mNavView: BottomNavigationView
    private lateinit var mViewModel: FeatureFragmentViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDisplayingFeaturesBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }

    override fun onStart() {
        super.onStart()
        initialisation()
    }

    override fun onResume() {
        super.onResume()
        firstStart()
        initBtn()
        initNavigation()
    }
    private fun initialisation() {
        mViewModel = ViewModelProvider(this).get(FeatureFragmentViewModel::class.java)
        mViewModel.setListBuffer(listBuffer)
        mBinding.specView.setFFTResolution(2048)
        mBinding.specView.setSamplingRate(16000)
        mViewModel.initVoiceRecord(16000)
        mNavView=mBinding.featureNavigation
    }

    private fun firstStart() {
        if (drawViewFragment) {
            mViewModel.initBasicTone( {
                mBinding.frqView.setWaveFrequency(it)
                mBinding.frqView.invalidate()
            },{ APP_ACTIVITY.runOnUiThread{ mBinding.infoStart.visibility= View.VISIBLE
                mBinding.infoSizeFrame.visibility= View.VISIBLE}})
            drawViewFragment =false
            mViewModel.initMFCC {
                mBinding.mfccView.setWave(it)
                mBinding.mfccView.invalidate()
            }
        }
    }

    private fun initNavigation() {
        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        APP_ACTIVITY.mToolbar.setNavigationOnClickListener {
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }
        mNavView.setOnNavigationItemSelectedListener { item ->
            item.isChecked=true
            navigationMenuClick(item,mBinding)
            false
        }
    }

    private fun initBtn() {
        mBinding.infoStart.setOnClickListener { it ->
            frameSizeEdit =mBinding.infoSizeFrame.text.toString().toFloat()
            (it as Button).text = "Стоп"
            mBinding.imageBasictone.visibility= View.VISIBLE
            mBinding.infoSizeFrame.visibility= View.VISIBLE
            when(method){
                BASIC_TONE ->{
                    mViewModel.startRecordBasicTone( {
                        mBinding.frqView.setRealTimeWave(it)
                        mBinding.frqView.invalidate()
                    },{APP_ACTIVITY.runOnUiThread {mBinding.infoStart.text = "Start" }})
                }
                MFCC ->{
                    mViewModel.startRecordMFCC( {
                        mBinding.mfccView.setRealTimeWave(it)
                        mBinding.mfccView.invalidate()
                    },{APP_ACTIVITY.runOnUiThread {mBinding.infoStart.text = "Start" }})
                }
            }
        }


        mBinding.startSpectrogramm.setOnClickListener {
            spectrogrammRecord =!spectrogrammRecord
            if (spectrogrammRecord){
                (it as Button).text="Запись идёт"
                mViewModel.startRecordSpectrogram {dataView->
                    mBinding.specView.setMagnitudes(dataView)
                    mBinding.specView.invalidate()
                }
            }else{
                mViewModel.stopRecorder()
                (it as Button).text="Включить запись с микрофона"
            }
        }
    }

}