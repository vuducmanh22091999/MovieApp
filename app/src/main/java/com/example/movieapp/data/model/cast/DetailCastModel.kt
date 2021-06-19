package com.example.movieapp.data.model.cast

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DetailCastModel(
    val adult: Boolean = false,
    @SerializedName("also_known_as")
    val alsoKnowAs: ArrayList<String> = arrayListOf(),
    val biography: String = "",
    val birthday: String = "",
    val deathday: String? = null,
    val gender: Int = 0,
    val homepage: String? = null,
    val id: Int = 0,
    @SerializedName("imdb_id")
    val imdbId: String = "",
    @SerializedName("known_for_department")
    val knownForDepartment: String = "",
    val name: String = "",
    @SerializedName("place_of_birth")
    val placeOfBirth: String = "",
    val popularity: Double = 0.0,
    @SerializedName("profile_path")
    val profilePath: String = ""
) : Serializable