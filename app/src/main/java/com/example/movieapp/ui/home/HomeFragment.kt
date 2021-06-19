package com.example.movieapp.ui.home

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.movie.ListMovieModel
import com.example.movieapp.ui.detail.movie.DetailMovieFragment
import com.example.movieapp.ui.home.adapter.PopularMovieAdapter
import com.example.movieapp.ui.home.adapter.TopRateMovieAdapter
import com.example.movieapp.utils.API_KEY
import com.example.movieapp.utils.ID_MOVIE
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment() {
    private lateinit var popularMovieAdapter: PopularMovieAdapter
    private lateinit var topRateMovieAdapter: TopRateMovieAdapter
    private val homeViewModel: HomeViewModel by viewModel()

    private var listPopularMovieModel = ListMovieModel()
    private var listTopRateModel = ListMovieModel()

    override fun getLayoutID(): Int {
        return R.layout.fragment_home
    }

    override fun doViewCreated() {
        initData()
        observerViewModel()
    }

    private fun initData() {
        homeViewModel.getPopularMovie(API_KEY)
        homeViewModel.getTopRateMovie(API_KEY)
    }

    private fun observerViewModel() {
        homeViewModel.popularMovie.observe(this@HomeFragment, {
            initRecyclerViewPopularMovie(it)
        })

        homeViewModel.topRateMovie.observe(this@HomeFragment, {
            initRecyclerViewTopRateMovie(it)
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
                addFragment(detailMovieFragment, R.id.frameLayout)
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
}