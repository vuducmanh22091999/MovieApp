package com.example.movieapp.ui.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.data.model.movie.MovieModel
import com.example.movieapp.utils.BASE_URL_IMG
import kotlinx.android.synthetic.main.item_product.view.*

class SearchMovieAdapter(
    private var listResultSearch: List<MovieModel>,
    private val onClick: (Int, String) -> Unit
) : RecyclerView.Adapter<SearchMovieAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindDataViewHolder(movieModel: MovieModel) {
            Glide.with(itemView.context).load(BASE_URL_IMG + movieModel.posterPath)
                .placeholder(R.drawable.img_placeholder).into(itemView.itemProduct_imgPoster)

//            itemView.itemProduct_tvTitleNameMovie.text = movieModel.title
//            itemView.itemProduct_tvTitleScoreRateMovie.text = movieModel.voteAverage
//            itemView.itemProduct_tvOverviewMovie.text = movieModel.overview
//            itemView.itemProduct_tvReleasedDateMovie.text = movieModel.releaseDate
            itemView.setOnClickListener {
                onClick(adapterPosition, movieModel.title)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_product, parent, false)
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