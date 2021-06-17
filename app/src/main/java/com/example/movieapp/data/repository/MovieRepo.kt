package com.example.movieapp.data.repository

import com.example.movieapp.data.remote.MovieAppServices

class MovieRepo(private val movieAppServices: MovieAppServices) {
    suspend fun getPopularMovie(apiKey: String) = movieAppServices.getListPopular(apiKey)

    suspend fun getTopRateMovie(apiKey: String) = movieAppServices.getListTopRate(apiKey)

    suspend fun getDetailMovie(movieId: Int, apiKey: String) = movieAppServices.getDetailMovie(movieId, apiKey)
}