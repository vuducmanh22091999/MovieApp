package com.example.movieapp.ui.favorite.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.data.model.favorite.FavoriteMovieModel
import com.example.movieapp.data.model.movie.MovieModel
import com.example.movieapp.utils.BASE_URL_IMG
import kotlinx.android.synthetic.main.item_favorite_movie.view.*

class FavoriteAdapter(
    private val listFavorite: List<MovieModel>,
    private val onClick: (Int, String) -> Unit,
) : RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun dataBindHolder(movieModel: MovieModel) {
            Glide.with(itemView.context).load(BASE_URL_IMG + movieModel.posterPath)
                .placeholder(R.drawable.img_placeholder).into(itemView.itemTrendingMovie_imgPoster)
            itemView.itemTrendingMovie_tvTitleNameMovie.text = movieModel.title
            itemView.itemTrendingMovie_tvTitleOriginalLanguageMovie.text = movieModel.originalLanguage
            itemView.setOnClickListener {
                onClick(absoluteAdapterPosition, movieModel.title)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_favorite_movie, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dataBindHolder(listFavorite[position])
    }

    override fun getItemCount(): Int = listFavorite.size
}