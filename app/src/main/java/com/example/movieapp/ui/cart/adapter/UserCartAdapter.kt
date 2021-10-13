package com.example.movieapp.ui.cart.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.data.model.product.CartProductModel
import kotlinx.android.synthetic.main.item_cart_product.view.*

class UserCartAdapter(private val list: List<CartProductModel>): RecyclerView.Adapter<UserCartAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindDataViewHolder(cartProductModel: CartProductModel) {
            itemView.context?.let {
                Glide.with(itemView.context).load(cartProductModel.productModel?.urlAvatar)
                    .placeholder(R.drawable.img_placeholder).into(itemView.itemCartProductUser_imgAvatar)
            }
            itemView.itemCartProductUser_tvTitleNameProduct.text = cartProductModel.productModel?.name
            itemView.itemCartProductUser_tvAmount.text = cartProductModel.productModel?.amount.toString()
            itemView.itemCartProductUser_tvPrice.text = cartProductModel.productModel?.price.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_cart_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindDataViewHolder(list[position])
    }

    override fun getItemCount() = list.size
}