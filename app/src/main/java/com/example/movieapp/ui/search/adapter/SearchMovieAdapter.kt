package com.example.movieapp.ui.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.data.model.movie.MovieModel
import com.example.movieapp.utils.BASE_URL_IMG
import kotlinx.android.synthetic.main.item_search_movie.view.*

class SearchMovieAdapter(
    private var listResultSearch: List<MovieModel>,
    private val onClick: (Int, String) -> Unit
) : RecyclerView.Adapter<SearchMovieAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindDataViewHolder(movieModel: MovieModel) {
            Glide.with(itemView.context).load(BASE_URL_IMG + movieModel.posterPath)
                .placeholder(R.drawable.img_placeholder).into(itemView.itemSearch_imgPosterMovie)

            itemView.itemSearch_tvTitleMovie.text = movieModel.title
            itemView.setOnClickListener {
                onClick(adapterPosition, movieModel.title)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_search_movie, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindDataViewHolder(listResultSearch[position])
    }

    override fun getItemCount() = listResultSearch.size

    fun filterMovie(filteredMovie: List<MovieModel>) {
        listResultSearch = filteredMovie
        notifyDataSetChanged()
    }
}