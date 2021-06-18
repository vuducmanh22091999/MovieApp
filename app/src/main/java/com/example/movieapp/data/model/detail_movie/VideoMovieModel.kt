package com.example.movieapp.data.model.detail_movie

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class VideoMovieModel(
    val id : String = "",
    @SerializedName("iso_639_1")
    val iso6391: String = "",
    @SerializedName("iso_3166_1")
    val iso31661: String = "",
    val key: String = "",
    val name: String = "",
    val site: String = "",
    val size: Int = 0,
    val type: String = ""
):Serializable
