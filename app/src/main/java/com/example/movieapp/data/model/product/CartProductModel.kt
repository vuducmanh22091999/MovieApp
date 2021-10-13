package com.example.movieapp.data.model.product

import java.io.Serializable

data class CartProductModel(
    val idCart: Int? = null,
    val productModel: ProductModel? = null
): Serializable