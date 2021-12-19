package com.example.movieapp.data.model.product

import java.io.Serializable

data class ProductModel(
    var type: String? = null,
    var id: Long? = null,
    var urlAvatar: String? = null,
    var name: String? = null,
    var price: Long = 0,
    var contentProduct: String = "",
    var listImage: ArrayList<String> = arrayListOf(),
    var listSize: ArrayList<SizeProductModel> = arrayListOf()
): Serializable
