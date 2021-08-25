package com.example.movieapp.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.data.model.product.ProductModel
import kotlinx.android.synthetic.main.item_product.view.*

class ListProductAdapter(
    private val list: List<ProductModel>,
    private val onClick: (Int, String) -> Unit,
    private val onClickDelete: (Int, String) -> Unit
) : RecyclerView.Adapter<ListProductAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindDataViewHolder(productModel: ProductModel) {
            itemView.itemProduct_tvTitleName.text = productModel.name
            itemView.itemProduct_tvAmount.text = productModel.number
            itemView.context?.let {
                Glide.with(itemView.context).load(productModel.urlAvatar)
                    .placeholder(R.drawable.img_placeholder).into(itemView.itemProduct_imgPoster)
            }

            itemView.setOnClickListener {
                onClick(adapterPosition, productModel.toString())
            }

            itemView.itemProduct_imgDelete.setOnClickListener {
                onClickDelete(adapterPosition, productModel.toString())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindDataViewHolder(list[position])
    }

    override fun getItemCount() = list.size
}