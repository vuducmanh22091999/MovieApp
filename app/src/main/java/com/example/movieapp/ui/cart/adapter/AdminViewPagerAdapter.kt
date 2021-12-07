package com.example.movieapp.ui.cart.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.movieapp.ui.cart.new_order.AdminNewOrderFragment
import com.example.movieapp.ui.cart.order_canceled.AdminOrderCanceledFragment
import com.example.movieapp.ui.cart.order_completed.AdminOrderCompletedFragment
import com.example.movieapp.ui.cart.order_confirm.AdminOrderConfirmFragment
import com.example.movieapp.ui.cart.order_delivering.AdminOrderDeliveringFragment

class AdminViewPagerAdapter(fm: FragmentManager, behavior: Int) :
    FragmentStatePagerAdapter(fm, behavior) {
    override fun getCount(): Int = 5

    override fun getItem(position: Int): Fragment {
        when(position) {
            0 -> return AdminNewOrderFragment()
            1 -> return AdminOrderConfirmFragment()
            2 -> return AdminOrderDeliveringFragment()
            3 -> return AdminOrderCompletedFragment()
            4 -> return AdminOrderCanceledFragment()
        }
        return AdminNewOrderFragment()
    }

    override fun getPageTitle(position: Int): CharSequence {
        var title = ""
        when(position) {
            0 -> title = "New Order"
            1 -> title = "Order Confirm"
            2 -> title = "Order Delivering"
            3 -> title = "Order Completed"
            4 -> title = "Order Canceled"
        }
        return title
    }
}