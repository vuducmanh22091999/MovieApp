package com.example.movieapp.data.model.cast

import com.google.gson.annotations.SerializedName


data class CastMovieModel(
    val adult: Boolean = false,
    val gender: Int = 0,
    val id: Int = 0,
    @SerializedName("known_for_department")
    val knownForDepartment: String = "",
    val name: String = "",
    @SerializedName("original_name")
    val originalName: String = "",
    val popularity: Double = 0.0,
    @SerializedName("profile_path")
    val profilePath: String = "",
    @SerializedName("cast_id")
    val castId: Int = 0,
    val character: String = "",
    @SerializedName("credit_id")
    val creditId: String = "",
    val order: Int = 0
)
