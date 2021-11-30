package com.example.movieapp.ui.confirm_order.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.data.model.product.CartProductModel
import com.example.movieapp.utils.formatStringInt
import com.example.movieapp.utils.formatStringLong
import kotlinx.android.synthetic.main.item_confirm_order_cart.view.*

class ConfirmOderAdapter(private val list: List<CartProductModel>)
    : RecyclerView.Adapter<ConfirmOderAdapter.ViewHolder>(){
    inner class ViewHolder(itemView: View) :  RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bindDataViewHolder(cartProductModel: CartProductModel) {
            itemView.context?.let {
                Glide.with(itemView.context).load(cartProductModel.productModel?.urlAvatar)
                    .placeholder(R.drawable.img_placeholder)
                    .into(itemView.itemConfirmOrder_imgAvatar)
            }
            itemView.itemConfirmOrder_tvSize.text =
                "Size: ${cartProductModel.productModel?.listSize?.get(0)?.size.toString()}"
            itemView.itemConfirmOrder_tvTitleNameProduct.text =
                cartProductModel.productModel?.name
            itemView.itemConfirmOrder_tvPrice.text =
                "${formatStringLong(cartProductModel.productModel?.price!!)}$"
            itemView.itemConfirmOrder_tvAmountUserOrder.text = cartProductModel.amountUserOrder.toString()

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_confirm_order_cart, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindDataViewHolder(list[position])
    }

    override fun getItemCount(): Int = list.size
}