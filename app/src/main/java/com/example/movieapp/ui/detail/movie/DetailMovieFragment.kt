package com.example.movieapp.ui.detail.movie

import android.widget.Toast
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.utils.ID_POPULAR_MOVIE
import kotlinx.android.synthetic.main.fragment_detail_movie.*

class DetailMovieFragment: BaseFragment() {
    override fun getLayoutID(): Int {
        return R.layout.fragment_detail_movie
    }

    override fun doViewCreated() {
        getData()
    }

    private fun getData() {
        val idPopular = arguments?.getSerializable(ID_POPULAR_MOVIE)
        Toast.makeText(context, idPopular.toString(), Toast.LENGTH_SHORT).show()
    }

    private fun urlVideo() {
        val string = "fNQawiJ6FR8"
        frgDetailMovie_videoView.setVideoPath("https://www.youtube.com/watch?v=$string")
        frgDetailMovie_videoView.start()
    }
}