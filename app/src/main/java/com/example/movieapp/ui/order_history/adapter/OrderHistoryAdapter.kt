package com.example.movieapp.ui.order_history.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.data.model.product.CartProductModel
import com.example.movieapp.utils.formatStringLong
import kotlinx.android.synthetic.main.item_order_history.view.*

class OrderHistoryAdapter(private val listProduct: List<CartProductModel>) :
    RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bindDataViewHolder(cartProductModel: CartProductModel) {
            itemView.context?.let {
                Glide.with(itemView.context).load(cartProductModel.productModel?.urlAvatar)
                    .placeholder(R.drawable.img_placeholder)
                    .into(itemView.itemOrderHistory_imgAvatar)
            }
            itemView.itemOrderHistory_tvTitleNameProduct.text =
                cartProductModel.productModel?.name
            itemView.itemOrderHistory_tvAmountUserOrder.text =
                "Amount user order: ${cartProductModel.amountUserOrder}"
            itemView.itemOrderHistory_tvPrice.text =
                "Total: ${formatStringLong(cartProductModel.totalPrice)}$"
            itemView.itemOrderHistory_tvSize.text =
                "Size: ${cartProductModel.productModel?.listSize?.get(0)?.size.toString()}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_order_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindDataViewHolder(listProduct[position])
    }

    override fun getItemCount(): Int = listProduct.size
}