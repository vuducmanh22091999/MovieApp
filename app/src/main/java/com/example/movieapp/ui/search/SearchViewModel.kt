package com.example.movieapp.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.data.model.movie.ListMovieModel
import com.example.movieapp.data.repository.MovieRepo
import kotlinx.coroutines.launch

class SearchViewModel(private val movieRepo: MovieRepo) : ViewModel() {
    var resultSearch = MutableLiveData<ListMovieModel>()

    fun searchMovie(query: String, apiKey: String) {
        viewModelScope.launch {
            val response = movieRepo.searchMovie(query, apiKey)
            if (response.isSuccessful && response.body() != null) {
                resultSearch.postValue(response.body())
            }
        }
    }
}