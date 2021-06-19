package com.example.movieapp.ui.detail.movie

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.cast.ListCastMovieModel
import com.example.movieapp.data.model.detail_movie.DetailMovieModel
import com.example.movieapp.ui.detail.cast.DetailCastFragment
import com.example.movieapp.ui.detail.adapter.CastAdapter
import com.example.movieapp.utils.API_KEY
import com.example.movieapp.utils.BASE_URL_IMG
import com.example.movieapp.utils.ID_CAST
import com.example.movieapp.utils.ID_MOVIE
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import kotlinx.android.synthetic.main.dialog_video.*
import kotlinx.android.synthetic.main.fragment_detail_movie.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.item_poster_movie.*
import kotlinx.android.synthetic.main.item_poster_movie.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class DetailMovieFragment : BaseFragment() {
    private val detailMovieViewModel: DetailMovieViewModel by viewModel()
    private var idPopular = 0
    private var detailMovieModel = DetailMovieModel()
    private var videoMovie = ""
    private lateinit var castAdapter: CastAdapter
    private lateinit var dialog: Dialog
    private var listCastMovieModel = ListCastMovieModel()

    override fun getLayoutID(): Int {
        return R.layout.fragment_detail_movie
    }

    override fun doViewCreated() {
        initData()
        initDialog()
        lifecycle.addObserver(dialog.dialogVideo_youtubePlayerView)
        observerViewModel()
        showDetailVideo()
    }

    private fun initData() {
        idPopular = arguments?.getSerializable(ID_MOVIE).toString().toInt()
        detailMovieViewModel.getDetailMovie(idPopular, API_KEY)
        detailMovieViewModel.getVideoMovie(idPopular, API_KEY)
        detailMovieViewModel.getCastMovie(idPopular, API_KEY)
    }

    private fun initDialog() {
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_video)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun observerViewModel() {
        detailMovieViewModel.detailMovie.observe(this@DetailMovieFragment, {
            detailMovieModel = it
            setData()
        })

        detailMovieViewModel.videoMovie.observe(this@DetailMovieFragment, {
            if (it.results.isEmpty()) {
                frgDetailMovie_tvTrailer.visibility = View.INVISIBLE
            } else
                videoMovie = it.results[0].key
        })

        detailMovieViewModel.castMovie.observe(this@DetailMovieFragment, {
            initRecyclerViewCastMovie(it)
        })
    }

    private fun initRecyclerViewCastMovie(listCastMovieModel: ListCastMovieModel) {
        this.listCastMovieModel.cast.addAll(listCastMovieModel.cast)
        castAdapter = CastAdapter(listCastMovieModel.cast.toList()) { index, string ->
            Toast.makeText(context, this.listCastMovieModel.cast[index].id.toString(), Toast.LENGTH_SHORT).show()
            val detailCastFragment = DetailCastFragment()
            val bundle = Bundle()
            bundle.putSerializable(ID_CAST, this.listCastMovieModel.cast[index].id)
            detailCastFragment.arguments = bundle
            addFragment(detailCastFragment, R.id.frameLayout)
        }
        val linearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        frgDetailMovie_rcvCast.setHasFixedSize(true)
        frgDetailMovie_rcvCast.layoutManager = linearLayoutManager
        frgDetailMovie_rcvCast.adapter = castAdapter
    }

    private fun setData() {
        var genres = ""
        var productionCountries = ""
        var spokenLanguages = ""
        detailMovieModel.let {
            context?.let { context ->
                Glide.with(context).load(BASE_URL_IMG + it.backdropPath)
                    .placeholder(R.drawable.img_placeholder).into(frgDetailMovie_imgPosterMovie)

                Glide.with(context).load(BASE_URL_IMG + it.posterPath)
                    .placeholder(R.drawable.img_placeholder).into(frgDetailMovie_img)
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
            Log.d("TEST", it.id.toString())
        }
    }

    private fun openDialog() {
        dialog.dialogVideo_youtubePlayerView.addYouTubePlayerListener(object :
            AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                if (videoMovie.isNotEmpty()) {
                    val videoId = videoMovie
                    youTubePlayer.loadVideo(videoId, 0f)
                }
            }
        })
    }

    private fun showDetailVideo() {
        frgDetailMovie_tvTrailer.setOnClickListener {
            openDialog()
            dialog.show()
        }
    }
}