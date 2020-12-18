package com.nvsces.speech_processing

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.nvsces.mynative.APP_ACTIVITY
import com.nvsces.mynative.REPOSITORY
import com.nvsces.mynative.checkPermissions
import com.nvsces.speech_processing.database.AppRoomDatabase
import com.nvsces.speech_processing.database.AppRoomRepository
import com.nvsces.speech_processing.databinding.ActivityMainBinding
import com.nvsces.speech_processing.screens.NoPermissionFragment
import com.nvsces.speech_processing.screens.VoiceEtalonFragment
import com.nvsces.speech_processing.utils.replaceFragment

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    lateinit var mToolbar: Toolbar
    val mBinding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        APP_ACTIVITY = this
        if (checkPermissions(Manifest.permission.RECORD_AUDIO)){
            replaceFragment(VoiceEtalonFragment(),false)
        }
    }

    override fun onStart() {
        super.onStart()
        mToolbar = mBinding.toolbarMain
        setSupportActionBar(mToolbar)
        initRoom()
    }

    private fun initRoom() {
            val dao= AppRoomDatabase.getInstance(APP_ACTIVITY).getAppRoomDao()
            REPOSITORY = AppRoomRepository(dao)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(APP_ACTIVITY,
                Manifest.permission.RECORD_AUDIO
            )== PackageManager.PERMISSION_GRANTED){
            replaceFragment(VoiceEtalonFragment(),false)
        }else {
            replaceFragment(NoPermissionFragment(),false)
        }
    }

}