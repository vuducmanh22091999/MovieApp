package com.example.movieapp.ui.main

import android.view.View
import androidx.fragment.app.Fragment
import com.example.movieapp.R
import com.example.movieapp.base.BaseActivity
import com.example.movieapp.ui.account.AccountUserFragment
import com.example.movieapp.ui.home.HomeFragment
import com.example.movieapp.ui.home.UserHomeFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_user.*

class UserActivity : BaseActivity() {
    private var fragment = Fragment()
    private lateinit var currentFragment: Fragment
    private lateinit var userHomeFragment: UserHomeFragment
    private lateinit var accountUserFragment: AccountUserFragment
    private var fragmentManager = supportFragmentManager

    override fun getLayoutID(): Int {
        return R.layout.activity_user
    }

    override fun doViewCreated() {
        setupFragment()
        handleNavigationBottom()
    }

    fun hideBottom() {
        actUser_bottomNavigation.visibility = View.GONE
    }

    private fun handleNavigationBottom() {
        actUser_bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.homeFragment -> {
                    if (currentFragment === userHomeFragment)
                        fragmentManager.beginTransaction().show(userHomeFragment).commit()
                    else
                        fragmentManager.beginTransaction().hide(currentFragment).show(userHomeFragment)
                            .commit()
                    currentFragment = userHomeFragment
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
                    if (currentFragment === accountUserFragment)
                        fragmentManager.beginTransaction().show(accountUserFragment).commit()
                    else
                        fragmentManager.beginTransaction().hide(currentFragment)
                            .show(accountUserFragment).commit()
                    currentFragment = accountUserFragment
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
        userHomeFragment = UserHomeFragment()
        accountUserFragment = AccountUserFragment()

        fragmentManager.beginTransaction()
            .add(R.id.actUser_frameLayout, userHomeFragment, fragment::class.java.simpleName)
            .commit()

        fragmentManager.beginTransaction()
            .add(R.id.actUser_frameLayout, accountUserFragment, fragment::class.java.simpleName)
            .commit()
        fragmentManager.beginTransaction().hide(accountUserFragment).commit()

        currentFragment = userHomeFragment
    }

}