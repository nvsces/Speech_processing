package com.nvsces.speech_processing.screens.CashList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.nvsces.mynative.APP_ACTIVITY
import com.nvsces.speech_processing.databinding.FragmentCashFileListBinding
import com.nvsces.speech_processing.models.AppMediaFile

class CashFileListFragment : Fragment() {


    private var _binding: FragmentCashFileListBinding?=null
    private val mBinding get() = _binding!!
    private lateinit var mObserverList: Observer<List<AppMediaFile>>
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: CashListAdapter
    private lateinit var mViewModel: CashListFragmentViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCashFileListBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }

    override fun onStart() {
        super.onStart()
        initialization()
    }

    private fun initialization() {
        mAdapter = CashListAdapter()
        mRecyclerView = mBinding.fileNameRecyclerView
        mRecyclerView.adapter = mAdapter
        mObserverList = Observer {
            val list = it.asReversed()
            mAdapter.setList(list)
        }
        mViewModel = ViewModelProvider(this).get(CashListFragmentViewModel::class.java)
        mViewModel.allNotes.observe(this, mObserverList)
        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


}