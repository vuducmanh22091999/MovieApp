package com.example.movieapp.ui.main

import android.view.View
import androidx.fragment.app.Fragment
import com.example.movieapp.R
import com.example.movieapp.base.BaseActivity
import com.example.movieapp.ui.account.AccountUserFragment
import com.example.movieapp.ui.cart.UserCartFragment
import com.example.movieapp.ui.home.UserHomeFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_user.*

class UserActivity : BaseActivity() {
    private var fragment = Fragment()
    private lateinit var currentFragment: Fragment
    private lateinit var userHomeFragment: UserHomeFragment
    private lateinit var accountUserFragment: AccountUserFragment
    private lateinit var userCartFragment: UserCartFragment
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
                R.id.cartFragment -> {
                        if (currentFragment === userCartFragment)
                            fragmentManager.beginTransaction().show(userCartFragment).commit()
                        else
                            fragmentManager.beginTransaction().hide(currentFragment)
                                .show(userCartFragment).commit()
                        currentFragment = userCartFragment
                        return@setOnNavigationItemSelectedListener true
                }
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
        userCartFragment = UserCartFragment()

        fragmentManager.beginTransaction()
            .add(R.id.actUser_frameLayout, userHomeFragment, fragment::class.java.simpleName)
            .commit()

        fragmentManager.beginTransaction()
            .add(R.id.actUser_frameLayout, accountUserFragment, fragment::class.java.simpleName)
            .commit()
        fragmentManager.beginTransaction().hide(accountUserFragment).commit()

        fragmentManager.beginTransaction()
            .add(R.id.actUser_frameLayout, userCartFragment, fragment::class.java.simpleName)
            .commit()
        fragmentManager.beginTransaction().hide(userCartFragment).commit()

        currentFragment = userHomeFragment
    }

    override fun onBackPressed() {
        val index = fragmentManager.backStackEntryCount - 1
        if (index >= 0) {
            super.onBackPressed()
            actUser_bottomNavigation.visibility = View.VISIBLE
        } else {
            val menuItem = actUser_bottomNavigation.menu.getItem(0)
            if (actUser_bottomNavigation.selectedItemId != menuItem.itemId) {

                if (currentFragment === userHomeFragment)
                    fragmentManager.beginTransaction().show(userHomeFragment).commit()
                else {
                    fragmentManager.beginTransaction().hide(currentFragment).show(userHomeFragment)
                        .commit()
                    currentFragment = userHomeFragment
                }
                actUser_bottomNavigation.selectedItemId = menuItem.itemId
            } else
                super.onBackPressed()
        }
    }

}