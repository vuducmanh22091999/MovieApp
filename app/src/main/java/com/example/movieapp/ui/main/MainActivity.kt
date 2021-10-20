package com.example.movieapp.ui.main

import android.view.View
import androidx.fragment.app.Fragment
import com.example.movieapp.R
import com.example.movieapp.base.BaseActivity
import com.example.movieapp.ui.account.AccountAdminFragment
import com.example.movieapp.ui.favorite.FavoriteFragment
import com.example.movieapp.ui.home.AdminHomeFragment
import com.example.movieapp.ui.search.SearchMovieFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_question_login.*


class MainActivity : BaseActivity() {
    private var fragment = Fragment()
    private lateinit var currentFragment: Fragment
    private lateinit var adminHomeFragment: AdminHomeFragment
    private lateinit var searchFragment: SearchMovieFragment
    private lateinit var favoriteFragment: FavoriteFragment
    private lateinit var accountAdminFragment: AccountAdminFragment
    private var fragmentManager = supportFragmentManager

    override fun getLayoutID(): Int {
        return R.layout.activity_main
    }

    override fun doViewCreated() {
        setupFragment()
        handleNavigationBottom()
    }

    private fun handleNavigationBottom() {
        bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.homeFragment -> {
                    if (currentFragment === adminHomeFragment)
                        fragmentManager.beginTransaction().show(adminHomeFragment).commit()
                    else
                        fragmentManager.beginTransaction().hide(currentFragment).show(adminHomeFragment)
                            .commit()
                    currentFragment = adminHomeFragment
                    return@setOnNavigationItemSelectedListener true
                }
//                R.id.favouritesFragment -> {
//                        if (currentFragment === favoriteFragment)
//                            fragmentManager.beginTransaction().show(favoriteFragment).commit()
//                        else
//                            fragmentManager.beginTransaction().hide(currentFragment)
//                                .show(favoriteFragment).commit()
//                        currentFragment = favoriteFragment
//                        return@setOnNavigationItemSelectedListener true
//                }
                R.id.accountFragment -> {
                        if (currentFragment === accountAdminFragment)
                            fragmentManager.beginTransaction().show(accountAdminFragment).commit()
                        else
                            fragmentManager.beginTransaction().hide(currentFragment)
                                .show(accountAdminFragment).commit()
                        currentFragment = accountAdminFragment
                        return@setOnNavigationItemSelectedListener true
                }
//                R.id.searchFragment -> {
//                    if (currentFragment === searchFragment)
//                        fragmentManager.beginTransaction().show(searchFragment).commit()
//                    else
//                        fragmentManager.beginTransaction().hide(currentFragment)
//                            .show(searchFragment).commit()
//                    currentFragment = searchFragment
//                    return@setOnNavigationItemSelectedListener true
//                }
            }
            true
        }
    }

    private fun setupFragment() {
        adminHomeFragment = AdminHomeFragment()
        searchFragment = SearchMovieFragment()
        accountAdminFragment = AccountAdminFragment()
        favoriteFragment = FavoriteFragment()

        fragmentManager.beginTransaction()
            .add(R.id.frameLayout, adminHomeFragment, fragment::class.java.simpleName)
            .commit()

        fragmentManager.beginTransaction()
            .add(R.id.frameLayout, accountAdminFragment, fragment::class.java.simpleName)
            .commit()
        fragmentManager.beginTransaction().hide(accountAdminFragment).commit()

        fragmentManager.beginTransaction()
            .add(R.id.frameLayout, searchFragment, fragment::class.java.simpleName)
            .commit()
        fragmentManager.beginTransaction().hide(searchFragment).commit()

        fragmentManager.beginTransaction()
            .add(R.id.frameLayout, favoriteFragment, fragment::class.java.simpleName)
            .commit()
        fragmentManager.beginTransaction().hide(favoriteFragment).commit()

        currentFragment = adminHomeFragment
    }

    fun hideBottom() {
        bottomNavigation.visibility = View.GONE
    }

    override fun onBackPressed() {
        val index = fragmentManager.backStackEntryCount - 1
        if (index >= 0) {
            super.onBackPressed()
            bottomNavigation.visibility = View.VISIBLE
        } else {
            val menuItem = bottomNavigation.menu.getItem(0)
            if (bottomNavigation.selectedItemId != menuItem.itemId) {

                if (currentFragment === adminHomeFragment)
                    fragmentManager.beginTransaction().show(adminHomeFragment).commit()
                else {
                    fragmentManager.beginTransaction().hide(currentFragment).show(adminHomeFragment)
                        .commit()
                    currentFragment = adminHomeFragment
                }
                bottomNavigation.selectedItemId = menuItem.itemId
            } else
                super.onBackPressed()
        }
    }
}