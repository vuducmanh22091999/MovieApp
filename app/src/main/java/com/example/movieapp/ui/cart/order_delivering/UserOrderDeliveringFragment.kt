package com.example.movieapp.ui.cart.order_delivering

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.product.CartProductModel
import com.example.movieapp.ui.order_history.adapter.OrderHistoryAdapter
import com.example.movieapp.utils.ORDER_DELIVERING
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_new_order.*
import kotlinx.android.synthetic.main.fragment_order_delivering.*
import java.util.ArrayList

class UserOrderDeliveringFragment: BaseFragment() {
    var idUser = ""
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseOrderDelivering: DatabaseReference
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter
    val listProductOrderDelivering = ArrayList<CartProductModel>()

    override fun getLayoutID(): Int {
        return R.layout.fragment_order_delivering
    }

    override fun doViewCreated() {
        auth = FirebaseAuth.getInstance()
        idUser = auth.currentUser?.uid.toString()
        databaseOrderDelivering = FirebaseDatabase.getInstance().reference.child(ORDER_DELIVERING)

        getDataOrderDelivering()
    }

    private fun initAdapter() {
        orderHistoryAdapter =
            OrderHistoryAdapter {_, _ -> }
        orderHistoryAdapter.submitList(listProductOrderDelivering)
        val linearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        frgOrderDelivering_rcvCart.setHasFixedSize(true)
        frgOrderDelivering_rcvCart.layoutManager = linearLayoutManager
        frgOrderDelivering_rcvCart.adapter = orderHistoryAdapter
    }

    private fun getDataOrderDelivering() {
        listProductOrderDelivering.clear()
        databaseOrderDelivering.child(idUser).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                view?.apply {
                    if (snapshot.exists()) {
                        for (value in snapshot.children) {
                            val cartProductModel = value.getValue(CartProductModel::class.java)
                            if (cartProductModel != null) {
                                frgOrderDelivering_tvNotification.visibility = View.GONE
                                listProductOrderDelivering.add(cartProductModel)
                            }
                        }
                        showHideCart()
                        initAdapter()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        databaseOrderDelivering.child(idUser).get().addOnCompleteListener {

        }
    }

    private fun showHideCart() {
        if (listProductOrderDelivering.isEmpty()) {
            frgOrderDelivering_tvNotification.setText(R.string.title_notification)
        } else {
            frgOrderDelivering_tvNotification.setText(R.string.title_blank)
        }
    }
}