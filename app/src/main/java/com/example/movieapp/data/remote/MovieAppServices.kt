package com.example.movieapp.data.remote

import com.example.movieapp.data.model.popular.ListMovieModel
import retrofit2.Response
import retrofit2.http.*

interface MovieAppServices {
    @GET("movie/popular")
    suspend fun getListPopular(@Query("api_key") apiKey: String): Response<ListMovieModel>

    @GET("movie/top_rated")
    suspend fun getListTopRate(@Query("api_key") apiKey: String): Response<ListMovieModel>
}