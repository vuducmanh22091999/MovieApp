package com.example.movieapp.data.model.account

import java.io.Serializable

data class AccountModel(
    var id: String = "",
    var userName: String = "",
    var phoneNumber: String = "",
    var email: String = "",
    var urlAvatar: String = ""
) : Serializable
