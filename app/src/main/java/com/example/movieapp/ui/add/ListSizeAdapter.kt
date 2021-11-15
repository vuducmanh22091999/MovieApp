package com.example.movieapp.ui.add

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R
import com.example.movieapp.data.model.product.SizeProductModel
import kotlinx.android.synthetic.main.item_size.view.*

class ListSizeAdapter(
    private val listSize: List<SizeProductModel>,
    private var pickSize: (Int, Int) -> Unit
) : RecyclerView.Adapter<ListSizeAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bindDataViewHolder(sizeProductModel: SizeProductModel) {
            itemView.itemSize_tvSize.text = sizeProductModel.size.toString()
            itemView.isSelected = sizeProductModel.isSelected
            if (sizeProductModel.isSelected && sizeProductModel.amountSize != 0)
                itemView.itemSize_etAmountSize.setText(sizeProductModel.amountSize.toString())
            else if (sizeProductModel.isSelected)
                itemView.itemSize_etAmountSize.hint = "Type amount product..."
            else if (!sizeProductModel.isSelected)
                itemView.itemSize_etAmountSize.hint = "Type amount product..."
            if (sizeProductModel.isSelected) {
                itemView.itemSize_etAmountSize.isFocusable = true
                itemView.itemSize_etAmountSize.isFocusableInTouchMode = true
                itemView.itemSize_etAmountSize.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {

                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        if (TextUtils.isEmpty(s))
                            Toast.makeText(itemView.context, "Don't leave blank", Toast.LENGTH_SHORT).show()
                        else if (s.toString().trim().toInt() == 0 || s.toString().trim().toInt() < 0)
                            Toast.makeText(itemView.context, "Amount bigger 0", Toast.LENGTH_SHORT).show()
                        else
                            sizeProductModel.amountSize = s.toString().toInt()
                    }

                    override fun afterTextChanged(s: Editable?) {
                    }

                })
            }
            if (!sizeProductModel.isSelected) {
                itemView.itemSize_etAmountSize.isFocusable = false
                itemView.itemSize_etAmountSize.isFocusableInTouchMode = false
            }
            itemView.setOnClickListener {
                pickSize(absoluteAdapterPosition, sizeProductModel.amountSize)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_size, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindDataViewHolder(listSize[position])
    }

    override fun getItemCount(): Int = listSize.size
}