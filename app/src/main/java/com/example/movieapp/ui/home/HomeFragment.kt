package com.example.movieapp.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.local.AppPreferences
import com.example.movieapp.data.model.movie.ListMovieModel
import com.example.movieapp.ui.detail.movie.DetailMovieFragment
import com.example.movieapp.ui.home.adapter.PopularMovieAdapter
import com.example.movieapp.ui.home.adapter.TopRateMovieAdapter
import com.example.movieapp.ui.main.MainActivity
import com.example.movieapp.utils.API_KEY
import com.example.movieapp.utils.ID_MOVIE
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment() {
    private lateinit var popularMovieAdapter: PopularMovieAdapter
    private lateinit var topRateMovieAdapter: TopRateMovieAdapter
    private val homeViewModel: HomeViewModel by viewModel()
    private lateinit var appPreferences: AppPreferences

    private var listPopularMovieModel = ListMovieModel()
    private var listTopRateModel = ListMovieModel()

    override fun getLayoutID(): Int {
        return R.layout.fragment_home
    }

    override fun doViewCreated() {
        appPreferences = context?.let { AppPreferences(it) }!!
        initData()
        observerViewModel()
    }

    private fun setInfo() {
        frgHome_tvTitleUserName.text = appPreferences.getLoginUserName()
        if (getIDUserFacebook().isNotEmpty()) {
            context?.let {
                Glide.with(it).load(urlAvatar()).placeholder(R.drawable.ic_account)
                    .into(frgHome_imgAvatar)
            }
        } else {
            context?.let {
                Glide.with(it).load(appPreferences.getLoginAvatar())
                    .placeholder(R.drawable.ic_account).into(frgHome_imgAvatar)
            }
        }
    }

    private fun getIDUserFacebook(): String {
        var facebookUserId = ""
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            for (profile in user.providerData) {
                if (FacebookAuthProvider.PROVIDER_ID == profile.providerId) {
                    facebookUserId = profile.uid
                }
            }
        }
        return facebookUserId
    }

    private fun urlAvatar(): String {
        return "https://graph.facebook.com/${getIDUserFacebook()}/picture?type=large"
    }

    private fun initData() {
        showLoading()
        homeViewModel.getPopularMovie(API_KEY)
        homeViewModel.getTopRateMovie(API_KEY)
    }

    private fun observerViewModel() {
        homeViewModel.popularMovie.observe(this@HomeFragment, {
            initRecyclerViewPopularMovie(it)
        })

        homeViewModel.topRateMovie.observe(this@HomeFragment, {
            initRecyclerViewTopRateMovie(it)
            hideLoading()
        })
    }

    private fun initRecyclerViewPopularMovie(listMoviePopularModel: ListMovieModel) {
        this.listPopularMovieModel.results.addAll(listMoviePopularModel.results)
        popularMovieAdapter =
            PopularMovieAdapter(this.listPopularMovieModel.results.toList()) { index, _ ->
                val detailMovieFragment = DetailMovieFragment()
                val bundle = Bundle()
                bundle.putSerializable(ID_MOVIE, this.listPopularMovieModel.results[index].id)
                detailMovieFragment.arguments = bundle
                addFragment(
                    detailMovieFragment,
                    R.id.frameLayout,
                    DetailMovieFragment::class.java.simpleName
                )
            }
        val linearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        frgHome_rcvPosterMovie.setHasFixedSize(true)
        frgHome_rcvPosterMovie.layoutManager = linearLayoutManager
        frgHome_rcvPosterMovie.adapter = popularMovieAdapter
    }

    private fun initRecyclerViewTopRateMovie(listMovieTopRateModel: ListMovieModel) {
        this.listTopRateModel.results.addAll(listMovieTopRateModel.results)
        topRateMovieAdapter =
            TopRateMovieAdapter(this.listTopRateModel.results.toList()) { index, _ ->
                val detailMovieFragment = DetailMovieFragment()
                val bundle = Bundle()
                bundle.putSerializable(ID_MOVIE, this.listTopRateModel.results[index].id)
                detailMovieFragment.arguments = bundle
                addFragment(detailMovieFragment, R.id.frameLayout)
            }
        val linearLayoutManagerVertical =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        frgHome_rcvTopRateMovie.setHasFixedSize(true)
        frgHome_rcvTopRateMovie.layoutManager = linearLayoutManagerVertical
        frgHome_rcvTopRateMovie.adapter = topRateMovieAdapter
    }

    override fun onResume() {
        super.onResume()
        setInfo()
    }
}