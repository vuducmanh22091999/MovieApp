package com.example.movieapp.ui.cart.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.movieapp.data.model.product.CartProductModel

class BaseDiffAdapter<T : CartProductModel> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.hashCode() == newItem.hashCode()
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.hashCode() == newItem.hashCode()
    }
}