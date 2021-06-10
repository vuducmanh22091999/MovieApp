package com.example.movieapp.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.data.model.trending.TrendingMovieModel
import kotlinx.android.synthetic.main.item_trending_movie.view.*

class TrendingMovieAdapter(private val listTrending: List<TrendingMovieModel>, private val onClick: (Int, String) -> Unit) : RecyclerView.Adapter<TrendingMovieAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun dataBindHolder(trendingMovieModel: TrendingMovieModel) {
            Glide.with(itemView.context).load(trendingMovieModel.imgMovie).placeholder(R.drawable.img_placeholder).into(itemView.itemTrendingMovie_imgPoster)
            itemView.itemTrendingMovie_tvTitleNameMovie.text = trendingMovieModel.nameMovie
            itemView.itemTrendingMovie_tvTitleHourMovie.text = trendingMovieModel.hourMovie
            itemView.setOnClickListener {
                onClick(adapterPosition, trendingMovieModel.nameMovie)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_trending_movie, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dataBindHolder(listTrending[position])
    }

    override fun getItemCount(): Int = listTrending.size
}