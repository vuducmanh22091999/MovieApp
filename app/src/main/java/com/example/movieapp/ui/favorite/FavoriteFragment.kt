package com.example.movieapp.ui.favorite

import android.content.Intent
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.local.AppPreferences
import com.example.movieapp.data.model.favorite.FavoriteMovieModel
import com.example.movieapp.ui.favorite.adapter.FavoriteAdapter
import com.example.movieapp.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_favorites.*

class FavoriteFragment : BaseFragment() {
    lateinit var favoriteAdapter: FavoriteAdapter
    private val listFavorite = ArrayList<FavoriteMovieModel>()

    private lateinit var auth: FirebaseAuth
    private lateinit var appPreferences: AppPreferences

    override fun getLayoutID(): Int {
        return R.layout.fragment_favorites
    }

    override fun doViewCreated() {
        auth = Firebase.auth
        appPreferences = context?.let { AppPreferences(it) }!!

        checkLogin()
        initData()
        initAdapter()
        initRecyclerView()
    }

    private fun checkLogin() {
        val user = auth.currentUser
        if (user == null) {
            val intentNewScreen = Intent(context, LoginActivity::class.java)
            startActivity(intentNewScreen)
        }
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