package com.example.movieapp.ui.detail.cast

import android.view.View
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.cast.DetailCastModel
import com.example.movieapp.ui.main.MainActivity
import com.example.movieapp.utils.API_KEY
import com.example.movieapp.utils.BASE_URL_IMG
import com.example.movieapp.utils.ID_CAST
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_detail_cast.*
import kotlinx.android.synthetic.main.item_cast.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailCastFragment: BaseFragment() {
    private val detailCastViewModel: DetailCastViewModel by viewModel()
    private var personId = 0

    override fun getLayoutID(): Int {
        return R.layout.fragment_detail_cast
    }

    override fun doViewCreated() {
        handleBottom()
        initData()
        observerViewModel()
    }

    private fun handleBottom() {
        (activity as MainActivity).hideBottom()
    }

    private fun initData() {
        personId = arguments?.getSerializable(ID_CAST).toString().toInt()
        detailCastViewModel.getCastMovie(personId, API_KEY)
    }

    private fun observerViewModel() {
        detailCastViewModel.castModel.observe(this@DetailCastFragment, {
            setData(it)
        })
    }

    private fun setData(detailCastModel: DetailCastModel) {
        detailCastModel.let {
            context?.let { it1 ->
                Glide.with(it1).load(BASE_URL_IMG + detailCastModel.profilePath)
                    .placeholder(R.drawable.img_placeholder)
                    .into(frgDetailCast_imgAvatar)
            }
            frgDetailCast_tvNameCast.text = detailCastModel.name
            frgDetailCast_tvBirthdayCast.text = detailCastModel.birthday
            frgDetailCast_tvPlaceOfBirthCast.text = detailCastModel.placeOfBirth
            frgDetailCast_tvBiography.text = detailCastModel.biography
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).bottomNavigation.visibility = View.GONE
    }
}