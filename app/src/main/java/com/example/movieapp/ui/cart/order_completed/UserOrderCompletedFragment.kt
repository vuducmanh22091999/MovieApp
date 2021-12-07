package com.example.movieapp.ui.cart.order_completed

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.product.CartProductModel
import com.example.movieapp.ui.order_history.adapter.OrderHistoryAdapter
import com.example.movieapp.utils.NEW_ORDER
import com.example.movieapp.utils.ORDER_COMPLETED
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_new_order.*
import kotlinx.android.synthetic.main.fragment_order_canceled.*
import kotlinx.android.synthetic.main.fragment_order_completed.*
import java.util.ArrayList

class UserOrderCompletedFragment: BaseFragment() {
    var idUser = ""
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseOrderCompleted: DatabaseReference
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter
    val listProductOrderCompleted = ArrayList<CartProductModel>()

    override fun getLayoutID(): Int {
        return R.layout.fragment_order_completed
    }

    override fun doViewCreated() {
        auth = FirebaseAuth.getInstance()
        idUser = auth.currentUser?.uid.toString()
        databaseOrderCompleted = FirebaseDatabase.getInstance().reference.child(ORDER_COMPLETED)

        getDataOrderCompleted()
    }

    private fun initAdapter() {
        orderHistoryAdapter =
            OrderHistoryAdapter {_,_ ->}
        orderHistoryAdapter.submitList(listProductOrderCompleted)
        val linearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        frgOrderCompleted_rcvCart.setHasFixedSize(true)
        frgOrderCompleted_rcvCart.layoutManager = linearLayoutManager
        frgOrderCompleted_rcvCart.adapter = orderHistoryAdapter
    }

    private fun getDataOrderCompleted() {
        listProductOrderCompleted.clear()
        databaseOrderCompleted.child(idUser).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                view?.apply {
                    if (snapshot.exists()) {
                        for (value in snapshot.children) {
                            val cartProductModel = value.getValue(CartProductModel::class.java)
                            if (cartProductModel != null) {
                                frgOrderCompleted_tvNotification.visibility = View.GONE
                                listProductOrderCompleted.add(cartProductModel)
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
        databaseOrderCompleted.child(idUser).get().addOnCompleteListener {

        }
    }

    private fun showHideCart() {
        if (listProductOrderCompleted.isEmpty()) {
            frgOrderCompleted_tvNotification.setText(R.string.title_notification)
        } else {
            frgOrderCompleted_tvNotification.setText(R.string.title_blank)
        }
    }
}