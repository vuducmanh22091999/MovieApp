package com.example.movieapp.ui.main

import com.example.movieapp.R
import com.example.movieapp.base.BaseActivity
import com.example.movieapp.ui.account.AccountFragment
import com.example.movieapp.ui.favorite.FavoriteFragment
import com.example.movieapp.ui.home.HomeFragment
import com.example.movieapp.ui.search.SearchFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {
    override fun getLayoutID() : Int {
        return R.layout.activity_main
    }

    override fun doViewCreated() {
        addFragment(HomeFragment(), R.id.frameLayout)
        handleNavigationBottom()
    }

    private fun handleNavigationBottom() {
        bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.homeFragment -> addFragment(HomeFragment(), R.id.frameLayout)
                R.id.favouritesFragment -> addFragment(FavoriteFragment(), R.id.frameLayout)
                R.id.accountFragment -> addFragment(AccountFragment(), R.id.frameLayout)
                R.id.searchFragment -> addFragment(SearchFragment(), R.id.frameLayout)
            }
            true
        }
    }
}