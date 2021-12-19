package com.example.movieapp.data.model.product

import com.example.movieapp.data.model.BaseModel

data class CartProductModel(
    var idCart: Long? = null,
    val idUser: String? = null,
    var userName: String? = null,
    var amountUserOrder: Long = 0,
    var size: Int = 0,
    val productModel: ProductModel? = null,
    var orderDateCompleted: String = "",
    var totalPrice: Long = 0
) : BaseModel()