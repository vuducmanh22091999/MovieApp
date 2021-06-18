package com.example.movieapp.data.remote

import com.example.movieapp.data.model.detail_movie.DetailMovieModel
import com.example.movieapp.data.model.detail_movie.ListVideoMovieModel
import com.example.movieapp.data.model.movie.ListMovieModel
import retrofit2.Response
import retrofit2.http.*

interface MovieAppServices {
    @GET("movie/popular")
    suspend fun getListPopular(@Query("api_key") apiKey: String): Response<ListMovieModel>

    @GET("movie/top_rated")
    suspend fun getListTopRate(@Query("api_key") apiKey: String): Response<ListMovieModel>

    @GET("movie/{movie_id}")
    suspend fun getDetailMovie(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): Response<DetailMovieModel>

    @GET("movie/{movie_id}/videos")
    suspend fun getVideoMovie(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): Response<ListVideoMovieModel>
}