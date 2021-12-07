package com.example.movieapp.data.model.product

import java.io.Serializable

data class CartProductModel(
    var idCart: Long? = null,
    val idUser: String? = null,
    var userName: String? = null,
    var amountUserOrder: Long = 0,
    var size: Int = 0,
    var orderStatus: String? = null,
    val productModel: ProductModel? = null,
    var orderDateCompleted: String = "",
    var isAddSuccess: Boolean = false,
    var isNewOrder: Boolean = false,
    var isOrderConfirm: Boolean = false,
    var isOrderDelivering: Boolean = false,
    var isOrderCompleted: Boolean = false,
    var isOrderCanceled: Boolean = false,
    var isOrderSuccess: Boolean = false,
    var totalPrice: Long = 0
) : Serializable