package com.example.movieapp.ui.home

import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.popular.PopularMovieModel
import com.example.movieapp.data.model.trending.TrendingMovieModel
import com.example.movieapp.ui.home.adapter.PopularMovieAdapter
import com.example.movieapp.ui.home.adapter.TrendingMovieAdapter
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BaseFragment() {
    lateinit var popularMovieAdapter: PopularMovieAdapter
    lateinit var trendingMovieAdapter: TrendingMovieAdapter
    private var listPopular: ArrayList<PopularMovieModel> = arrayListOf()
    private var listTrending: ArrayList<TrendingMovieModel> = arrayListOf()

    override fun getLayoutID(): Int {
        return R.layout.fragment_home
    }

    override fun doViewCreated() {
        initData()
        initAdapter()
        initRecyclerView()
    }

    private fun initAdapter() {
        popularMovieAdapter = PopularMovieAdapter(listPopular.toList()) { index, _ ->
            Toast.makeText(context, listPopular[index].urlPoster, Toast.LENGTH_SHORT).show()
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
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        frgHome_rcvTrendingMovie.setHasFixedSize(true)
        frgHome_rcvTrendingMovie.layoutManager = linearLayoutManagerVertical
        frgHome_rcvTrendingMovie.adapter = trendingMovieAdapter
    }

    private fun initData() {
        listPopular.add(PopularMovieModel(R.drawable.img_deadpool))
        listPopular.add(PopularMovieModel(R.drawable.img_deadpool))
        listPopular.add(PopularMovieModel(R.drawable.img_deadpool))
        listPopular.add(PopularMovieModel(R.drawable.img_deadpool))
        listPopular.add(PopularMovieModel(R.drawable.img_deadpool))
        listPopular.add(PopularMovieModel(R.drawable.img_deadpool))
        listPopular.add(PopularMovieModel(R.drawable.img_deadpool))
        listPopular.add(PopularMovieModel(R.drawable.img_deadpool))
        listPopular.add(PopularMovieModel(R.drawable.img_deadpool))
        listPopular.add(PopularMovieModel(R.drawable.img_deadpool))

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