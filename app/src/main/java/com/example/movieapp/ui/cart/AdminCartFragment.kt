package com.example.movieapp.ui.cart

import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.ui.cart.adapter.AdminViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_admin_cart.*

class AdminCartFragment : BaseFragment() {
    private lateinit var adminViewPagerAdapter : AdminViewPagerAdapter

    override fun getLayoutID(): Int {
        return R.layout.fragment_admin_cart
    }

    override fun doViewCreated() {
        adminViewPagerAdapter = AdminViewPagerAdapter(childFragmentManager,
            FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        frgAdminCart_viewPager.adapter = adminViewPagerAdapter
        frgAdminCart_tabLayout.setupWithViewPager(frgAdminCart_viewPager)
    }

}