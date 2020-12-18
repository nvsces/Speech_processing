package com.nvsces.speech_processing.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nvsces.mynative.APP_ACTIVITY
import com.nvsces.speech_processing.MainActivity
import com.nvsces.speech_processing.R
import com.nvsces.speech_processing.databinding.FragmentNoPermissionBinding
import com.nvsces.speech_processing.databinding.FragmentVoiceEtalonBinding
import com.nvsces.speech_processing.utils.replaceFragment
import com.nvsces.speech_processing.utils.restartActivity

class NoPermissionFragment : Fragment() {

    private var _binding: FragmentNoPermissionBinding? = null
    private val mBinding get() = _binding!!


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoPermissionBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        mBinding.btnBack.setOnClickListener {
            restartActivity()
        }
    }
}