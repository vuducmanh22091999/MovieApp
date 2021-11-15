package com.example.movieapp.ui.main

import android.view.View
import androidx.fragment.app.Fragment
import com.example.movieapp.R
import com.example.movieapp.base.BaseActivity
import com.example.movieapp.ui.account.AccountAdminFragment
import com.example.movieapp.ui.cart.AdminCartFragment
import com.example.movieapp.ui.home.AdminHomeFragment
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_question_login.*


class MainActivity : BaseActivity() {
    private var fragment = Fragment()
    private lateinit var currentFragment: Fragment
    private lateinit var adminHomeFragment: AdminHomeFragment
    private lateinit var adminCartFragment: AdminCartFragment
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
                R.id.cartFragment -> {
                        if (currentFragment === adminCartFragment)
                            fragmentManager.beginTransaction().show(adminCartFragment).commit()
                        else
                            fragmentManager.beginTransaction().hide(currentFragment)
                                .show(adminCartFragment).commit()
                        currentFragment = adminCartFragment
                        return@setOnNavigationItemSelectedListener true
                }
                R.id.accountFragment -> {
                        if (currentFragment === accountAdminFragment)
                            fragmentManager.beginTransaction().show(accountAdminFragment).commit()
                        else
                            fragmentManager.beginTransaction().hide(currentFragment)
                                .show(accountAdminFragment).commit()
                        currentFragment = accountAdminFragment
                        return@setOnNavigationItemSelectedListener true
                }
            }
            true
        }
    }

    private fun setupFragment() {
        adminHomeFragment = AdminHomeFragment()
        adminCartFragment = AdminCartFragment()
        accountAdminFragment = AccountAdminFragment()

        fragmentManager.beginTransaction()
            .add(R.id.frameLayout, adminHomeFragment, fragment::class.java.simpleName)
            .commit()

        fragmentManager.beginTransaction()
            .add(R.id.frameLayout, accountAdminFragment, fragment::class.java.simpleName)
            .commit()
        fragmentManager.beginTransaction().hide(accountAdminFragment).commit()

        fragmentManager.beginTransaction()
            .add(R.id.frameLayout, adminCartFragment, fragment::class.java.simpleName)
            .commit()
        fragmentManager.beginTransaction().hide(adminCartFragment).commit()

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
            }
//            else
//                super.onBackPressed()
        }
    }
}