package com.example.movieapp.ui.detail.movie

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.data.model.detail_movie.DetailMovieModel
import com.example.movieapp.data.repository.MovieRepo
import kotlinx.coroutines.launch

class DetailMovieViewModel(private val movieRepo: MovieRepo): ViewModel() {
    var detailMovie = MutableLiveData<DetailMovieModel>()

    fun getDetailMovie(movieId: Int, apiKey: String) {
        viewModelScope.launch {
            val response = movieRepo.getDetailMovie(movieId, apiKey)
            if (response.isSuccessful && response.body() != null) {
                detailMovie.postValue(response.body())
            }
        }
    }
}