package com.example.movieapp.ui.favorite

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.local.AppPreferences
import com.example.movieapp.data.model.favorite.FavoriteMovieModel
import com.example.movieapp.data.model.movie.ListMovieModel
import com.example.movieapp.ui.detail.movie.DetailMovieFragment
import com.example.movieapp.ui.favorite.adapter.FavoriteAdapter
import com.example.movieapp.ui.home.adapter.PopularMovieAdapter
import com.example.movieapp.ui.home.adapter.TopRateMovieAdapter
import com.example.movieapp.ui.login.LoginActivity
import com.example.movieapp.utils.API_KEY
import com.example.movieapp.utils.ID_MOVIE
import com.example.movieapp.utils.SESSION_ID
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_favorites.*
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteFragment : BaseFragment() {
    private val favoriteViewModel: FavoriteViewModel by viewModel()
    private var listFavorite = ListMovieModel()
    private lateinit var topRateMovieAdapter: TopRateMovieAdapter

    private lateinit var auth: FirebaseAuth
    private lateinit var appPreferences: AppPreferences

    override fun getLayoutID(): Int {
        return R.layout.fragment_favorites
    }

    override fun doViewCreated() {
        appPreferences = context?.let { AppPreferences(it) }!!

//        checkLogin()
        observerViewModel()
    }

    private fun checkLogin() {
        val user = auth.currentUser
        if (user == null) {
            val intentNewScreen = Intent(context, LoginActivity::class.java)
            startActivity(intentNewScreen)
        }
    }

    private fun observerViewModel() {
        favoriteViewModel.favoriteMovie.observe(this@FavoriteFragment, {
            initRecyclerViewTopRateMovie(it)
            hideLoading()
        })
    }

    private fun initData() {
        favoriteViewModel.getFavoriteMovie(API_KEY, SESSION_ID)
    }

    private fun initRecyclerViewTopRateMovie(listFavoriteMovieModel: ListMovieModel) {
        this.listFavorite.results.addAll(listFavoriteMovieModel.results)
        topRateMovieAdapter =
            TopRateMovieAdapter(this.listFavorite.results.toList()) { index, _ ->
                val detailMovieFragment = DetailMovieFragment()
                val bundle = Bundle()
                bundle.putSerializable(ID_MOVIE, this.listFavorite.results[index].id)
                detailMovieFragment.arguments = bundle
                addFragment(detailMovieFragment, R.id.frameLayout)
            }
        val linearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        frgFavorite_rcvFavorite.setHasFixedSize(true)
        frgFavorite_rcvFavorite.layoutManager = linearLayoutManager
        frgFavorite_rcvFavorite.adapter = topRateMovieAdapter
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            showLoading()
            listFavorite.results.clear()
            initData()
        }

    }
}