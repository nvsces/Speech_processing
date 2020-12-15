package com.nvsces.speech_processing.screens.menu

import android.view.MenuItem
import android.view.View
import com.nvsces.mynative.APP_ACTIVITY
import com.nvsces.mynative.BASIC_TONE
import com.nvsces.mynative.MFCC
import com.nvsces.mynative.SPECTROGRAMM
import com.nvsces.speech_processing.R
import com.nvsces.speech_processing.databinding.FragmentDisplayingFeaturesBinding

var method = BASIC_TONE

fun navigationMenuClick(item: MenuItem, binding: FragmentDisplayingFeaturesBinding){
    when (item.itemId) {
        R.id.action_basic_tone -> {
            APP_ACTIVITY.title="BasicTone"
            method= BASIC_TONE
            basicToneShow(binding)
        }
        R.id.action_mfcc -> {
            APP_ACTIVITY.title="MFCC"
            method= MFCC
            mfccShow(binding)
        }
        R.id.action_spectrogramm -> {
            APP_ACTIVITY.title="Spectrogramm"
            method= SPECTROGRAMM
            spectrogrammShow(binding)
        }
    }
}

private fun mfccShow(binding: FragmentDisplayingFeaturesBinding) {
    binding.imageBasictone.visibility= View.GONE
    binding.imageMfcc.visibility= View.VISIBLE
    binding.mfccView.visibility= View.VISIBLE
    binding.frqView.visibility= View.GONE
    binding.specView.visibility= View.GONE
    binding.startSpectrogramm.visibility= View.GONE
    binding.infoStart.visibility= View.VISIBLE
    binding.infoSizeFrame.visibility= View.VISIBLE
}

private fun basicToneShow(binding: FragmentDisplayingFeaturesBinding) {
    binding.mfccView.visibility= View.GONE
    binding.imageMfcc.visibility= View.GONE
    binding.frqView.visibility= View.VISIBLE
    binding.specView.visibility= View.GONE
    binding.startSpectrogramm.visibility= View.GONE
    binding.infoStart.visibility= View.VISIBLE
    binding.infoSizeFrame.visibility= View.VISIBLE
}

private fun spectrogrammShow(binding: FragmentDisplayingFeaturesBinding) {
    binding.infoStart.visibility= View.GONE
    binding.imageMfcc.visibility= View.GONE
    binding.mfccView.visibility= View.GONE
    binding.frqView.visibility= View.GONE
    binding.specView.visibility= View.VISIBLE
    binding.startSpectrogramm.visibility= View.VISIBLE
}