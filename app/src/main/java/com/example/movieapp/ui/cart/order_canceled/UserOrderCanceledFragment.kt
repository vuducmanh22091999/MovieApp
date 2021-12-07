package com.example.movieapp.ui.cart.order_canceled

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.product.CartProductModel
import com.example.movieapp.ui.order_history.adapter.OrderHistoryAdapter
import com.example.movieapp.utils.ORDER_CANCELED
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_new_order.*
import kotlinx.android.synthetic.main.fragment_order_canceled.*
import java.util.ArrayList

class UserOrderCanceledFragment: BaseFragment() {
    var idUser = ""
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseNewOrder: DatabaseReference
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter
    val listProductOrderCanceled = ArrayList<CartProductModel>()

    override fun getLayoutID(): Int {
        return R.layout.fragment_order_canceled
    }

    override fun doViewCreated() {
        auth = FirebaseAuth.getInstance()
        idUser = auth.currentUser?.uid.toString()
        databaseNewOrder = FirebaseDatabase.getInstance().reference.child(ORDER_CANCELED)

        getDataOrderCanceled()
    }

    private fun initAdapter() {
        orderHistoryAdapter =
            OrderHistoryAdapter { _, _ ->}
        orderHistoryAdapter.submitList(listProductOrderCanceled)
        val linearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        frgOrderCanceled_rcvCart.setHasFixedSize(true)
        frgOrderCanceled_rcvCart.layoutManager = linearLayoutManager
        frgOrderCanceled_rcvCart.adapter = orderHistoryAdapter
    }

    private fun getDataOrderCanceled() {
        listProductOrderCanceled.clear()
        databaseNewOrder.child(idUser).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                view?.apply {
                    if (snapshot.exists()) {
                        for (value in snapshot.children) {
                            val cartProductModel = value.getValue(CartProductModel::class.java)
                            if (cartProductModel != null) {
                                listProductOrderCanceled.add(cartProductModel)
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
    }

    private fun showHideCart() {
        if (listProductOrderCanceled.isEmpty()) {
            frgOrderCanceled_tvNotification.setText(R.string.title_notification)
        } else {
            frgOrderCanceled_tvNotification.setText(R.string.title_blank)
        }
    }
}