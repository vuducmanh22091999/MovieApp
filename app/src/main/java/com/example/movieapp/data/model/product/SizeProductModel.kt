package com.example.movieapp.data.model.product

import java.io.Serializable

data class SizeProductModel(
    val size: Int = 0,
    var amountSize: Int = 0,
    var isSelected: Boolean = false
): Serializable