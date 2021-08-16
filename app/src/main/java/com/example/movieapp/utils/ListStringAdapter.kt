package com.example.movieapp.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R
import kotlinx.android.synthetic.main.item_top_rate_movie.view.*

class ListStringAdapter(
    private val list: List<StringModel>,
    private val onClick: (Int, String) -> Unit
) : RecyclerView.Adapter<ListStringAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindDataViewHolder(stringModel: StringModel) {
            itemView.itemTopRateMovie_tvTitleNameMovie.text = stringModel.name
            itemView.itemTopRateMovie_tvOverviewMovie.text = stringModel.number
            itemView.itemTopRateMovie_tvReleasedDateMovie.text = stringModel.id.toString()

            itemView.setOnClickListener {
                onClick(adapterPosition, stringModel.toString())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_top_rate_movie, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindDataViewHolder(list[position])
    }

    override fun getItemCount() = list.size
}