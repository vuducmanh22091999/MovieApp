package com.example.movieapp.ui.cart.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.data.model.product.CartProductModel
import com.example.movieapp.utils.formatStringLong
import kotlinx.android.synthetic.main.item_admin_cart_product.view.*

class AdminCartAdapter(
    private val listProduct: List<CartProductModel>,
    private val userName: String
) :
    RecyclerView.Adapter<AdminCartAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bindDataViewHolder(cartProductModel: CartProductModel) {
            itemView.context?.let {
                Glide.with(itemView.context).load(cartProductModel.productModel?.urlAvatar)
                    .placeholder(R.drawable.img_placeholder)
                    .into(itemView.itemCartProductAdmin_imgAvatar)
            }
            itemView.itemCartProductAdmin_tvAmountUserOrder.text =
                "Amount user order: ${cartProductModel.amountUserOrder}"
            itemView.itemCartProductAdmin_tvPrice.text =
                "Total: ${formatStringLong(cartProductModel.totalPrice)}$"
            itemView.itemCartProductAdmin_tvSize.text =
                "Size: ${cartProductModel.productModel?.listSize?.get(0)?.size.toString()}"
            itemView.itemCartProductAdmin_tvTitleNameProduct.text =
                cartProductModel.productModel?.name
            itemView.itemCartProductAdmin_tvOrderUserName.text = userName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_admin_cart_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindDataViewHolder(listProduct[position])
    }

    override fun getItemCount(): Int = listProduct.size
}