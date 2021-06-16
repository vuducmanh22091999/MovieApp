package com.example.movieapp.ui.favorite.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.data.model.favorite.FavoriteMovieModel
import kotlinx.android.synthetic.main.item_favorite_movie.view.*

class FavoriteAdapter(
    private val listFavorite: List<FavoriteMovieModel>,
    private val onClick: (Int, String) -> Unit,
    private val onClickFavorite: (Int, String) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun dataBindHolder(favoriteMovieModel: FavoriteMovieModel) {
            Glide.with(itemView.context).load(favoriteMovieModel.urlMovie)
                .placeholder(R.drawable.img_placeholder).into(itemView.itemTrendingMovie_imgPoster)
            itemView.itemTrendingMovie_tvTitleNameMovie.text = favoriteMovieModel.nameMovie
            itemView.itemTrendingMovie_tvTitleOriginalLanguageMovie.text = favoriteMovieModel.originalLanguageMovie
            itemView.setOnClickListener {
                onClick(absoluteAdapterPosition, favoriteMovieModel.nameMovie)
            }

            itemView.itemFavoriteMovie_imgFavorite.setOnClickListener {
                listFavorite[absoluteAdapterPosition].statusFavorite = !listFavorite[absoluteAdapterPosition].statusFavorite
                itemView.itemFavoriteMovie_imgFavorite.isSelected = !listFavorite[absoluteAdapterPosition].statusFavorite
                onClickFavorite(absoluteAdapterPosition, listFavorite[absoluteAdapterPosition].statusFavorite.toString())
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