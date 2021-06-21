package com.example.movieapp.ui.detail.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.data.model.cast.CastMovieModel
import com.example.movieapp.utils.BASE_URL_IMG
import kotlinx.android.synthetic.main.item_cast.view.*

class CastAdapter(
    private val listCast: List<CastMovieModel>,
    private val onClick: (Int, String) -> Unit
) : RecyclerView.Adapter<CastAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun dataBindHolder(castMovieModel: CastMovieModel) {
            itemView.context?.let {
                Glide.with(itemView.context).load(BASE_URL_IMG + castMovieModel.profilePath)
                    .placeholder(R.drawable.img_user)
                    .into(itemView.itemCast_imgAvatar)
            }

            itemView.itemCast_tvName.text = castMovieModel.name

            itemView.setOnClickListener {
                onClick(adapterPosition, castMovieModel.id.toString())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_cast, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dataBindHolder(listCast[position])
    }

    override fun getItemCount() = listCast.size
}