package com.example.movieapp.data.model.account

import java.io.Serializable

data class AccountLogin(
    val id: String = "",
    val userName: String = "",
    val phone: String = "",
    val email: String = "",
    val urlAvatar: String = ""
): Serializable
