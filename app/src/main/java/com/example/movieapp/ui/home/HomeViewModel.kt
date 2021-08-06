package com.example.movieapp.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.data.model.movie.ListMovieModel
import com.example.movieapp.data.repository.MovieRepo
import kotlinx.coroutines.launch

class HomeViewModel(private val movieRepo: MovieRepo): ViewModel() {
    var popularMovie = MutableLiveData<ListMovieModel>()
    var topRateMovie = MutableLiveData<ListMovieModel>()

    fun getPopularMovie(apiKey: String) {
        viewModelScope.launch {
            val response = movieRepo.getPopularMovie(apiKey)
            if (response.isSuccessful && response.body() != null) {
                popularMovie.postValue(response.body())
            }
        }
    }
   
    fun getTopRateMovie(apiKey: String) {
        viewModelScope.launch {
            val response = movieRepo.getTopRateMovie(apiKey)
            if (response.isSuccessful && response.body() != null) {
                topRateMovie.postValue(response.body())
            }
        }
    }
}