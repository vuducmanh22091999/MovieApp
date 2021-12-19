package com.example.movieapp.data.model.product

import java.io.Serializable

data class SizeProductModel(
    val size: Int = 0,
    var amountSize: Long = 0L,
    var isSelected: Boolean = false
): Serializable