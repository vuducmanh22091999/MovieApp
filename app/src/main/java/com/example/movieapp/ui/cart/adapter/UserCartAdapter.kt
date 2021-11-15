package com.example.movieapp.ui.cart.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.data.model.product.CartProductModel
import com.example.movieapp.utils.formatString
import kotlinx.android.synthetic.main.item_user_cart_product.view.*

class UserCartAdapter(
    private val list: List<CartProductModel>,
    private var minus: (Int, Int) -> Unit,
    private var plus: (Int, Int) -> Unit
) : RecyclerView.Adapter<UserCartAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bindDataViewHolder(cartProductModel: CartProductModel) {
            itemView.context?.let {
                Glide.with(itemView.context).load(cartProductModel.productModel?.urlAvatar)
                    .placeholder(R.drawable.img_placeholder)
                    .into(itemView.itemCartProductUser_imgAvatar)
            }
            itemView.itemCartProductUser_tvSize.text =
                "Size: ${cartProductModel.productModel?.listSize?.get(0)?.size.toString()}"
            itemView.itemCartProductUser_tvTitleNameProduct.text =
                cartProductModel.productModel?.name
            itemView.itemCartProductUser_tvPrice.text =
                "${formatString(cartProductModel.productModel?.price!!)}$"
            itemView.itemCartProductUser_tvAmountOrder.text = cartProductModel.amountUserOrder.toString()
            itemView.itemCartProductUser_imgMinus.setOnClickListener {
                minus(absoluteAdapterPosition, cartProductModel.amountUserOrder)
                itemView.itemCartProductUser_tvAmountOrder.text = cartProductModel.amountUserOrder.toString()
            }
            if (cartProductModel.amountUserOrder == 0) {
                itemView.itemCartProductUser_imgMinus.isClickable = false
                itemView.itemCartProductUser_imgMinus.isFocusable = false
            }
            if (cartProductModel.amountUserOrder < 0)
                itemView.itemCartProductUser_tvAmountOrder.text = "0"
            itemView.itemCartProductUser_imgPlus.setOnClickListener {
                plus(absoluteAdapterPosition, cartProductModel.amountUserOrder)
                itemView.itemCartProductUser_tvAmountOrder.text = cartProductModel.amountUserOrder.toString()
            }
            if (cartProductModel.amountUserOrder == cartProductModel.productModel?.listSize?.get(0)?.amountSize!!) {
                itemView.itemCartProductUser_imgPlus.isClickable = false
                itemView.itemCartProductUser_imgPlus.isFocusable = false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_user_cart_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindDataViewHolder(list[position])
    }

    override fun getItemCount() = list.size
}