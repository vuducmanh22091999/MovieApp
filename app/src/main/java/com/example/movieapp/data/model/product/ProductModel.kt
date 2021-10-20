package com.example.movieapp.data.model.product

import java.io.Serializable

data class ProductModel(
    var type: String? = null,
    var id: String? = null,
    var urlAvatar: String? = null,
    var name: String? = null,
    var amount: Int? = null,
    var price: Int? = null,
    var listImage: ArrayList<String> = arrayListOf()
): Serializable
