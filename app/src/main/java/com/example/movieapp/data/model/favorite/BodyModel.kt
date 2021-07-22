package com.example.movieapp.data.model.favorite

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BodyModel(
    @SerializedName("media_type")
    val mediaType: String,
    @SerializedName("media_id")
    val mediaId: Int,
    val favorite: Boolean
): Serializable