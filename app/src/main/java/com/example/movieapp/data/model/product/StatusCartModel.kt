package com.example.movieapp.data.model.product

import com.example.movieapp.data.model.BaseModel

data class StatusCartModel(
    var idOrder: Long? = null,
    var userName: String = "",
    var status: Int = 0,
    var valueStatus: String = "New Order",
    var completedDate: String = "",
    val listProduct: ArrayList<CartProductModel> = arrayListOf()
): BaseModel()