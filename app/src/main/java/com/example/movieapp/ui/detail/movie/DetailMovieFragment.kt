package com.example.movieapp.ui.detail.movie

import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_detail_movie.*

class DetailMovieFragment: BaseFragment() {
    override fun getLayoutID(): Int {
        return R.layout.fragment_detail_movie
    }

    override fun doViewCreated() {
        val string = "fNQawiJ6FR8"
        frgDetailMovie_videoView.setVideoPath("https://www.youtube.com/watch?v=$string")
        frgDetailMovie_videoView.start()
    }
}