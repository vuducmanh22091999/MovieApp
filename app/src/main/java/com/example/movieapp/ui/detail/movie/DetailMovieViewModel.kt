package com.example.movieapp.ui.detail.movie

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media.AudioAttributesImplApi21Parcelizer
import com.example.movieapp.data.model.cast.ListCastMovieModel
import com.example.movieapp.data.model.detail_movie.DetailMovieModel
import com.example.movieapp.data.model.detail_movie.ListVideoMovieModel
import com.example.movieapp.data.repository.MovieRepo
import kotlinx.coroutines.launch

class DetailMovieViewModel(private val movieRepo: MovieRepo) : ViewModel() {
    var detailMovie = MutableLiveData<DetailMovieModel>()
    var videoMovie = MutableLiveData<ListVideoMovieModel>()
    var castMovie = MutableLiveData<ListCastMovieModel>()

    fun getDetailMovie(movieId: Int, apiKey: String) {
        viewModelScope.launch {
            val response = movieRepo.getDetailMovie(movieId, apiKey)
            if (response.isSuccessful && response.body() != null) {
                detailMovie.postValue(response.body())
            }
        }
    }

    fun getVideoMovie(movieId: Int, apiKey: String) {
        viewModelScope.launch {
            val response = movieRepo.getVideoMovie(movieId, apiKey)
            if (response.isSuccessful && response.body() != null) {
                videoMovie.postValue(response.body())
            }
        }
    }

    fun getCastMovie(movieId: Int, apiKey: String) {
        viewModelScope.launch {
            val response = movieRepo.getCastMovie(movieId, apiKey)
            if (response.isSuccessful && response.body() != null) {
                castMovie.postValue(response.body())
            }
        }
    }
}