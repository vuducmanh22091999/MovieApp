package com.example.movieapp.ui.order_history.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.data.model.product.CartProductModel
import com.example.movieapp.data.model.product.StatusCartModel
import com.example.movieapp.ui.cart.adapter.AdminCartAdapter
import com.example.movieapp.ui.cart.adapter.BaseDiffAdapter
import com.example.movieapp.utils.formatStringLong
import kotlinx.android.synthetic.main.item_order_history.view.*
import java.util.concurrent.Executors

class OrderHistoryAdapter :
    ListAdapter<CartProductModel, OrderHistoryAdapter.DataViewHolder>(
        AsyncDifferConfig.Builder<CartProductModel>(BaseDiffAdapter())
        .setBackgroundThreadExecutor(Executors.newSingleThreadExecutor()).build()) {
    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_order_history, parent, false)
        return DataViewHolder(view)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bindDataViewHolder(currentList[position])
    }
}