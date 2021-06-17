package com.example.movieapp.data.model.detail_movie

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BelongsToCollectionModel(
    val id: Int = 0,
    val name: String = "",
    @SerializedName("backdrop_path")
    val posterPath: String? = null,
    @SerializedName("poster_path")
    val backdropPath: String? = null
) : Serializable
