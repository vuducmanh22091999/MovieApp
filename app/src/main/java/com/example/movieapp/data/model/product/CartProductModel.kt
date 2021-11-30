package com.example.movieapp.data.model.product

import java.io.Serializable

data class CartProductModel(
    val idCart: Long? = null,
    val idUser: String? = null,
    var amountUserOrder: Long = 0,
    var size: Int = 0,
    val productModel: ProductModel? = null,
    var isAddSuccess: Boolean = false,
    var isCheckOrder: Boolean = false,
    var isOrderSuccess: Boolean = false,
    var totalPrice: Long = 0
) : Serializable