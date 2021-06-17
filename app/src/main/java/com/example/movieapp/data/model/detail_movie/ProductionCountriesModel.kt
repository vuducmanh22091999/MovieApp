package com.example.movieapp.data.model.detail_movie

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProductionCountriesModel(
    @SerializedName("iso_3166_1")
    val iso31161: String = "",
    val name: String = ""
):Serializable
