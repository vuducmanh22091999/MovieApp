package com.example.movieapp.data.repository

import com.example.movieapp.data.model.favorite.BodyModel
import com.example.movieapp.data.remote.MovieAppServices

class MovieRepo(private val movieAppServices: MovieAppServices) {
    suspend fun getPopularMovie(apiKey: String) = movieAppServices.getListPopular(apiKey)

    suspend fun getTopRateMovie(apiKey: String) = movieAppServices.getListTopRate(apiKey)

    suspend fun getDetailMovie(movieId: Int, apiKey: String) =
        movieAppServices.getDetailMovie(movieId, apiKey)

    suspend fun getVideoMovie(movieId: Int, apiKey: String) =
        movieAppServices.getVideoMovie(movieId, apiKey)

    suspend fun getCastMovie(movieId: Int, apiKey: String) =
        movieAppServices.getCastMovie(movieId, apiKey)

    suspend fun getDetailCast(personId: Int, apiKey: String) =
        movieAppServices.getDetailCast(personId, apiKey)

    suspend fun searchMovie(query: String, apiKey: String) =
        movieAppServices.searchMovie(query, apiKey)

    suspend fun createFavoriteMovie(apiKey: String, sessionId: String, bodyModel: BodyModel) =
        movieAppServices.createFavoriteMovie(apiKey, sessionId, bodyModel)

    suspend fun getFavoriteMovie(apiKey: String, sessionId: String) =
        movieAppServices.getFavoriteMovie(apiKey, sessionId)
}