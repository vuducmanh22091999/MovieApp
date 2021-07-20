package com.example.movieapp.ui.favorite

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.data.model.favorite.BodyModel
import com.example.movieapp.data.model.favorite.ResponsesModel
import com.example.movieapp.data.model.movie.ListMovieModel
import com.example.movieapp.data.repository.MovieRepo
import kotlinx.coroutines.launch

class FavoriteViewModel(private val movieRepo: MovieRepo): ViewModel() {
    var createFavoriteMovie = MutableLiveData<ResponsesModel>()
    var favoriteMovie = MutableLiveData<ListMovieModel>()

    fun createFavoriteMovie(apiKey: String, sessionId: String, bodyModel: BodyModel) {
        viewModelScope.launch {
            val response = movieRepo.createFavoriteMovie(apiKey, sessionId, bodyModel)
            if (response.isSuccessful && response.body() != null) {
                createFavoriteMovie.postValue(response.body())
            }
        }
    }

    fun getFavoriteMovie(apiKey: String, sessionId: String) {
        viewModelScope.launch {
            val response = movieRepo.getFavoriteMovie(apiKey, sessionId)
            if (response.isSuccessful && response.body() != null) {
                favoriteMovie.postValue(response.body())
            }
        }
    }
}