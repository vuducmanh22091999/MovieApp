package com.example.movieapp.ui.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.data.model.product.ProductModel
import kotlinx.android.synthetic.main.item_product_user.view.*

class UserProductAdapter(
    private val list: List<ProductModel>,
    private val onClick: (Int, String) -> Unit
) : RecyclerView.Adapter<UserProductAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bindDataViewHolder(productModel: ProductModel) {
            itemView.itemProductUser_tvTitleNameProduct.text = productModel.name
            itemView.itemProductUser_tvAmount.text = productModel.amount.toString()
            itemView.itemProductUser_tvPrice.text = productModel.price.toString() + "$"
            itemView.context?.let {
                Glide.with(itemView.context).load(productModel.urlAvatar)
                    .placeholder(R.drawable.img_placeholder).into(itemView.itemProductUser_imgAvatar)
            }
            itemView.setOnClickListener {
                onClick(adapterPosition, productModel.name.toString())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_product_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindDataViewHolder(list[position])
    }

    override fun getItemCount() = list.size
}