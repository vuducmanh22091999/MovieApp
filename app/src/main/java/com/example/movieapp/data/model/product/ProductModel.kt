package com.example.movieapp.data.model.product

import java.io.Serializable

data class ProductModel(
    val type: String? = null,
    val id: String? = null,
    val urlAvatar: String? = null,
    val name: String? = null,
    val amount: Int? = null,
    val price: Int? = null
): Serializable
