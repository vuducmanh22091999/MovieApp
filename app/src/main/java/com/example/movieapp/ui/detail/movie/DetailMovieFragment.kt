package com.example.movieapp.ui.detail.movie

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.detail_movie.DetailMovieModel
import com.example.movieapp.utils.API_KEY
import com.example.movieapp.utils.BASE_URL_IMG
import com.example.movieapp.utils.ID_POPULAR_MOVIE
import kotlinx.android.synthetic.main.fragment_detail_movie.*
import kotlinx.android.synthetic.main.item_poster_movie.*
import kotlinx.android.synthetic.main.item_poster_movie.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailMovieFragment : BaseFragment() {
    private val detailMovieViewModel: DetailMovieViewModel by viewModel()
    var idPopular = 0
    var test = MutableLiveData<DetailMovieModel>()
    var detailMovieModel = DetailMovieModel()

    override fun getLayoutID(): Int {
        return R.layout.fragment_detail_movie
    }

    override fun doViewCreated() {
        initData()
        observerViewModel()
    }

    private fun initData() {
        idPopular = arguments?.getSerializable(ID_POPULAR_MOVIE).toString().toInt()
        detailMovieViewModel.getDetailMovie(idPopular, API_KEY)
    }

    private fun observerViewModel() {
        detailMovieViewModel.detailMovie.observe(this@DetailMovieFragment, {
            detailMovieModel = it
            setData()
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setData() {
        var genres = ""
        var productionCountries = ""
        var spokenLanguages = ""
        detailMovieModel.let {
            context?.let { context ->
                Glide.with(context).load(BASE_URL_IMG + it.posterPath)
                    .placeholder(R.drawable.img_placeholder).into(frgDetailMovie_imgPosterMovie)
            }
            frgDetailMovie_tvTitleNameMovie.text = it.title
            frgDetailMovie_tvTitleReleaseDateMovie.text = it.releaseDate
            frgDetailMovie_tvTitleVoteAverageMovie.text = it.voteAverage
            frgDetailMovie_tvTitleVoteCountMovie.text = it.voteCount
            frgDetailMovie_tvTitlePopularityMovie.text = it.popularity.toString()
            frgDetailMovie_tvTitleOverviewMovie.text = it.overview
            for (i in 0 until it.genres.size) {
                genres += "${it.genres[i].name}\n"
            }
            frgDetailMovie_tvTitleGenresMovie.text = genres
            for (i in 0 until it.productionCountries.size) {
                productionCountries += "${it.productionCountries[i].name}\n"
            }
            frgDetailMovie_tvTitleProductionCountriesMovie.text = productionCountries

            for (i in 0 until it.spokenLanguages.size) {
                spokenLanguages += "${it.spokenLanguages[i].name}\n"
            }
            frgDetailMovie_tvTitleSpokenLanguagesMovie.text = spokenLanguages
        }
    }

    private fun urlVideo() {
        val string = "fNQawiJ6FR8"
        frgDetailMovie_videoView.setVideoPath("https://www.youtube.com/watch?v=$string")
        frgDetailMovie_videoView.start()
    }
}