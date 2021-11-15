package com.example.movieapp.ui.detail.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R
import com.example.movieapp.data.model.product.SizeProductModel
import kotlinx.android.synthetic.main.item_size.view.*

class ListSizeUserAdapter(
    private val listSize: List<SizeProductModel>,
    private var pickSize: (Int, Int) -> Unit
) : RecyclerView.Adapter<ListSizeUserAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindDataViewHolder(sizeProductModel: SizeProductModel) {
            itemView.itemSize_tvSize.text = sizeProductModel.size.toString()
            itemView.isSelected = sizeProductModel.isSelected
            itemView.setOnClickListener {
                pickSize(absoluteAdapterPosition, sizeProductModel.amountSize)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_size_product_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindDataViewHolder(listSize[position])
    }

    override fun getItemCount(): Int = listSize.size
}