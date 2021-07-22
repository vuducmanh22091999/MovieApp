package com.example.movieapp.data.model.favorite

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ResponsesModel(
    @SerializedName("status_code")
    val statusCode: Int,
    @SerializedName("status_message")
    val statusMessage: String
):Serializable