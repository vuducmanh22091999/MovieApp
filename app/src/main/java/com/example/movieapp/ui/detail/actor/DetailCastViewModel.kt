package com.example.movieapp.ui.detail.actor

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.data.model.cast.DetailCastModel
import com.example.movieapp.data.repository.MovieRepo
import kotlinx.coroutines.launch

class DetailCastViewModel(private val movieRepo: MovieRepo): ViewModel() {
    var castModel = MutableLiveData<DetailCastModel>()

    fun getCastMovie(personId: Int, apiKey: String) {
        viewModelScope.launch {
            val response = movieRepo.getDetailCast(personId, apiKey)
            if (response.isSuccessful && response.body() != null) {
                castModel.postValue(response.body())
            }
        }
    }
}