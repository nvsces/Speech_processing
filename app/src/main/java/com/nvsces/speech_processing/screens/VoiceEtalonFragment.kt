package com.nvsces.speech_processing.screens

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nvsces.mynative.APP_ACTIVITY
import com.nvsces.mynative.drawViewFragment
import com.nvsces.mynative.nameCurrentFile
import com.nvsces.speech_processing.R
import com.nvsces.speech_processing.databinding.FragmentVoiceEtalonBinding
import com.nvsces.speech_processing.screens.CashList.CashFileListFragment
import com.nvsces.speech_processing.utils.*
import kotlinx.android.synthetic.main.fragment_voice_etalon.*


class VoiceEtalonFragment() : Fragment() {

    private lateinit var mViewModel:VoiceEtalonFragmentViewModel
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
        mViewModel=ViewModelProvider(this).get(VoiceEtalonFragmentViewModel::class.java)
        mViewModel.initRecorder()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onResume() {
        super.onResume()
        initButton()
        initView()
        mViewModel.clearStandardList()
        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        APP_ACTIVITY.mToolbar.setNavigationOnClickListener {
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }
    }

    private fun initView() {
        mBinding.btnOpenFile.text= nameCurrentFile
        mBinding.editFilename.setText("")
        mBinding.checkboxSaveFile.isChecked=false
        mBinding.checkboxSaveFile.setOnClickListener {
            if (mBinding.checkboxSaveFile.isChecked){
                mBinding.editFilename.visibility=View.VISIBLE
            }else{
                mBinding.editFilename.visibility=View.INVISIBLE
            }
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun initButton() {

        mBinding.btnVoiceEtalon.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {

                btn_voice_etalon.setBackgroundResource(R.drawable.ic_voice_blue)
                mViewModel.startRecorder()

            } else if (event.action == MotionEvent.ACTION_UP) {

                btn_voice_etalon.setBackgroundResource(R.drawable.ic_voice)
                mViewModel.stopRecorder{
                    if (mBinding.checkboxSaveFile.isChecked){
                        val filenameSave=mBinding.editFilename.text.toString()
                        mViewModel.createFileWav(filenameSave)
                    }
                    drawViewFragment=true
                    replaceFragment(DisplayingFeaturesFragment(it))
                }
            }
            true
        }
        mBinding.btnLoadingCash.setOnClickListener {
            if (nameCurrentFile!="cash") {
            mViewModel.readWavFileData(nameCurrentFile){
                drawViewFragment=true
                replaceFragment(DisplayingFeaturesFragment(it))
            }
        }
            }
        mBinding.btnOpenFile.setOnClickListener {
            replaceFragment(CashFileListFragment())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewModel.releaseRecorder()
    }
}