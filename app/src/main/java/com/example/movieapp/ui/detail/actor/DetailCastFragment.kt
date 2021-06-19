package com.example.movieapp.ui.detail.actor

import android.widget.Toast
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.utils.API_KEY
import com.example.movieapp.utils.ID_CAST
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailCastFragment: BaseFragment() {
    private val detailCastViewModel: DetailCastViewModel by viewModel()
    private var personId = 0

    override fun getLayoutID(): Int {
        return R.layout.fragment_detail_actor
    }

    override fun doViewCreated() {
        initData()
        observerViewModel()
    }

    private fun initData() {
        personId = arguments?.getSerializable(ID_CAST).toString().toInt()
        detailCastViewModel.getCastMovie(personId, API_KEY)
    }

    private fun observerViewModel() {
        detailCastViewModel.castModel.observe(this@DetailCastFragment, {
            Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
        })
    }
}