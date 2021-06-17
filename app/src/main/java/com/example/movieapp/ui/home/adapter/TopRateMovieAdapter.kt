package com.example.movieapp.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.data.model.movie.MovieModel
import com.example.movieapp.utils.BASE_URL_IMG
import kotlinx.android.synthetic.main.item_top_rate_movie.view.*

class TopRateMovieAdapter(
    private val listTopRate: List<MovieModel>,
    private val onClick: (Int, String) -> Unit
) : RecyclerView.Adapter<TopRateMovieAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun dataBindHolder(movieModel: MovieModel) {
            Glide.with(itemView.context).load(BASE_URL_IMG + movieModel.posterPath)
                .placeholder(R.drawable.img_placeholder).into(itemView.itemTopRateMovie_imgPoster)
            itemView.itemTopRateMovie_tvTitleNameMovie.text = movieModel.title
            itemView.itemTopRateMovie_tvTitleScoreRateMovie.text = movieModel.voteAverage
            itemView.itemTopRateMovie_tvOverviewMovie.text = movieModel.overview
            itemView.itemTopRateMovie_tvReleasedDateMovie.text = movieModel.releaseDate
            itemView.setOnClickListener {
                onClick(absoluteAdapterPosition, movieModel.title)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_top_rate_movie, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dataBindHolder(listTopRate[position])
    }

    override fun getItemCount(): Int = listTopRate.size
}