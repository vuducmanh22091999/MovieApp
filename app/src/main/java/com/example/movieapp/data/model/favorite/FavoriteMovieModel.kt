package com.example.movieapp.data.model.favorite

import java.io.Serializable

data class FavoriteMovieModel(
//    val urlMovie: String,
    val urlMovie: Int,
    val nameMovie: String,
    val originalLanguageMovie: String,
    var statusFavorite : Boolean = true
) : Serializable
