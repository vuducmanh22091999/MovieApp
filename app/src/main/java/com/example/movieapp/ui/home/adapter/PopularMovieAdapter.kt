package com.example.movieapp.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.data.model.movie.MovieModel
import com.example.movieapp.utils.BASE_URL_IMG
import kotlinx.android.synthetic.main.item_poster_movie.view.*

class PopularMovieAdapter(
    private val listPoster: List<MovieModel>,
    private val onClick: (Int, String) -> Unit
) : RecyclerView.Adapter<PopularMovieAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun dataBindHolder(movieModel: MovieModel) {
            Glide.with(itemView.context).load(BASE_URL_IMG + movieModel.posterPath)
                .placeholder(R.drawable.img_placeholder).into(itemView.itemPosterMovie_imgPoster)
            itemView.itemPosterMovie_imgPoster.setOnClickListener {
                onClick(adapterPosition, movieModel.posterPath)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_poster_movie, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dataBindHolder(listPoster[position])
    }

    override fun getItemCount(): Int = listPoster.size
}