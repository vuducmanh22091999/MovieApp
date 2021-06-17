package com.example.movieapp.data.model.detail_movie

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SpokenLanguagesModel(
    @SerializedName("english_name")
    val englishName : String = "",
    @SerializedName("iso_639_1")
    val iso6391: String = "",
    val name : String = ""
): Serializable