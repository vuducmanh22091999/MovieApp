package com.example.movieapp.ui.favorite

import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.favorite.FavoriteMovieModel
import com.example.movieapp.ui.favorite.adapter.FavoriteAdapter
import kotlinx.android.synthetic.main.fragment_favorites.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.frgHome_rcvPosterMovie
import kotlinx.android.synthetic.main.item_favorite_movie.*
import kotlinx.android.synthetic.main.item_favorite_movie.view.*

class FavoriteFragment : BaseFragment() {
    lateinit var favoriteAdapter: FavoriteAdapter
    private val listFavorite = ArrayList<FavoriteMovieModel>()

    override fun getLayoutID(): Int {
        return R.layout.fragment_favorites
    }

    override fun doViewCreated() {
        initData()
        initAdapter()
        initRecyclerView()
    }

    private fun initData() {
        listFavorite.add(FavoriteMovieModel(R.drawable.img_deadpool, "Deadpool", "en"))
        listFavorite.add(FavoriteMovieModel(R.drawable.img_deadpool, "Deadpool1", "en1"))
        listFavorite.add(FavoriteMovieModel(R.drawable.img_deadpool, "Deadpool2", "en2"))
        listFavorite.add(FavoriteMovieModel(R.drawable.img_deadpool, "Deadpool3", "en3"))
        listFavorite.add(FavoriteMovieModel(R.drawable.img_deadpool, "Deadpool4", "en4"))
        listFavorite.add(FavoriteMovieModel(R.drawable.img_deadpool, "Deadpool5", "en5"))
        listFavorite.add(FavoriteMovieModel(R.drawable.img_deadpool, "Deadpool6", "en6"))
        listFavorite.add(FavoriteMovieModel(R.drawable.img_deadpool, "Deadpool7", "en7"))
        listFavorite.add(FavoriteMovieModel(R.drawable.img_deadpool, "Deadpool8", "en8"))
        listFavorite.add(FavoriteMovieModel(R.drawable.img_deadpool, "Deadpool9", "en9"))
    }

    private fun initAdapter() {
        favoriteAdapter = FavoriteAdapter(listFavorite.toList(), { _, clickShowName ->
            Toast.makeText(context, clickShowName, Toast.LENGTH_SHORT).show()
        }, { _, clickStatus ->
            Toast.makeText(context, clickStatus, Toast.LENGTH_SHORT).show()
        })
    }

    private fun initRecyclerView() {
        val linearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        frgFavorite_rcvFavorite.setHasFixedSize(true)
        frgFavorite_rcvFavorite.layoutManager = linearLayoutManager
        frgFavorite_rcvFavorite.adapter = favoriteAdapter
    }
}