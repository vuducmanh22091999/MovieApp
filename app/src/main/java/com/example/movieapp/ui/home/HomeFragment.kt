package com.example.movieapp.ui.home

import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.popular.PopularMovieModel
import com.example.movieapp.data.model.trending.TrendingMovieModel
import com.example.movieapp.ui.detail.movie.DetailMovieFragment
import com.example.movieapp.ui.home.adapter.PopularMovieAdapter
import com.example.movieapp.ui.home.adapter.TrendingMovieAdapter
import com.example.movieapp.utils.API_KEY
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment() {
    lateinit var popularMovieAdapter: PopularMovieAdapter
    lateinit var trendingMovieAdapter: TrendingMovieAdapter
    private val homeViewModel: HomeViewModel by viewModel()
    private var listPopular: ArrayList<PopularMovieModel> = arrayListOf()
    private var listTrending: ArrayList<TrendingMovieModel> = arrayListOf()

    override fun getLayoutID(): Int {
        return R.layout.fragment_home
    }

    override fun doViewCreated() {
        initData()
        initAdapter()
        initRecyclerView()
        observerViewModel()
    }

    private fun observerViewModel() {
        homeViewModel.popularMovie.observe(this@HomeFragment, {
            Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
        })
    }

    private fun initAdapter() {
        popularMovieAdapter = PopularMovieAdapter(listPopular.toList()) { _, _ ->
            addFragment(DetailMovieFragment(), R.id.frameLayout)
        }

        trendingMovieAdapter = TrendingMovieAdapter(listTrending.toList()) { index, _ ->
            Toast.makeText(context, listTrending[index].nameMovie, Toast.LENGTH_SHORT).show()
        }
    }

    private fun initRecyclerView() {
        val linearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        frgHome_rcvPosterMovie.setHasFixedSize(true)
        frgHome_rcvPosterMovie.layoutManager = linearLayoutManager
        frgHome_rcvPosterMovie.adapter = popularMovieAdapter

        val linearLayoutManagerVertical =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        frgHome_rcvTrendingMovie.setHasFixedSize(true)
        frgHome_rcvTrendingMovie.layoutManager = linearLayoutManagerVertical
        frgHome_rcvTrendingMovie.adapter = trendingMovieAdapter
    }

    private fun initData() {
        homeViewModel.getPopularMovie(API_KEY)

        listTrending.add(TrendingMovieModel(R.drawable.img_deadpool, "ABC", "1h90p"))
        listTrending.add(TrendingMovieModel(R.drawable.img_deadpool, "ABC", "1h90p"))
        listTrending.add(TrendingMovieModel(R.drawable.img_deadpool, "ABC", "1h90p"))
        listTrending.add(TrendingMovieModel(R.drawable.img_deadpool, "ABC", "1h90p"))
        listTrending.add(TrendingMovieModel(R.drawable.img_deadpool, "ABC", "1h90p"))
        listTrending.add(TrendingMovieModel(R.drawable.img_deadpool, "ABC", "1h90p"))
        listTrending.add(TrendingMovieModel(R.drawable.img_deadpool, "ABC", "1h90p"))
        listTrending.add(TrendingMovieModel(R.drawable.img_deadpool, "ABC", "1h90p"))
        listTrending.add(TrendingMovieModel(R.drawable.img_deadpool, "ABC", "1h90p"))
        listTrending.add(TrendingMovieModel(R.drawable.img_deadpool, "ABC", "1h90p"))
    }
}