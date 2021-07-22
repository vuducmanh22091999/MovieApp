package com.example.movieapp.data.remote

import com.example.movieapp.data.model.cast.DetailCastModel
import com.example.movieapp.data.model.cast.ListCastMovieModel
import com.example.movieapp.data.model.detail_movie.DetailMovieModel
import com.example.movieapp.data.model.detail_movie.ListVideoMovieModel
import com.example.movieapp.data.model.favorite.BodyModel
import com.example.movieapp.data.model.favorite.ResponsesModel
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

    @GET("movie/{movie_id}/credits")
    suspend fun getCastMovie(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): Response<ListCastMovieModel>

    @GET("person/{person_id}")
    suspend fun getDetailCast(
        @Path("person_id") personId: Int,
        @Query("api_key") apiKey: String
    ): Response<DetailCastModel>

    @GET("search/movie")
    suspend fun searchMovie(
        @Query("query") query: String,
        @Query("api_key") apiKey: String
    ): Response<ListMovieModel>

    @POST("account/{account_id}/favorite")
    suspend fun createFavoriteMovie(
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String,
        @Body body: BodyModel
    ): Response<ResponsesModel>

    @GET("account/{account_id}/favorite/movies")
    suspend fun getFavoriteMovie(
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String,
    ): Response<ListMovieModel>
}