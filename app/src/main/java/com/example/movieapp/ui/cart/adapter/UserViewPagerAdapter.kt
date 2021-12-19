package com.example.movieapp.ui.cart.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.movieapp.ui.cart.new_order.UserNewOrderFragment
import com.example.movieapp.ui.cart.order_canceled.UserOrderCanceledFragment
import com.example.movieapp.ui.cart.order_completed.UserOrderCompletedFragment
import com.example.movieapp.ui.cart.order_confirm.UserOrderConfirmFragment
import com.example.movieapp.ui.cart.order_delivering.UserOrderDeliveringFragment

class UserViewPagerAdapter(fm: FragmentManager, behavior: Int) :
    FragmentStatePagerAdapter(fm, behavior) {
    override fun getCount(): Int = 5

    override fun getItem(position: Int): Fragment {
        when(position) {
            0 -> return UserNewOrderFragment()
            1 -> return UserOrderConfirmFragment()
            2 -> return UserOrderDeliveringFragment()
            3 -> return UserOrderCompletedFragment()
            4 -> return UserOrderCanceledFragment()
        }
        return UserNewOrderFragment()
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