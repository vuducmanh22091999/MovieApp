package com.example.movieapp.data.model.detail_movie

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProductionCompaniesModel(
    val id: Int = 0,
    @SerializedName("logo_path")
    val logoPath: String = "",
    val name : String = "",
    @SerializedName("origin_country")
    val originCountry: String = ""
):Serializable
