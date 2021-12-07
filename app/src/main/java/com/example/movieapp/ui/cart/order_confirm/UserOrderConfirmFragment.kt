package com.example.movieapp.ui.cart.order_confirm

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.product.CartProductModel
import com.example.movieapp.ui.order_history.adapter.OrderHistoryAdapter
import com.example.movieapp.utils.ORDER_CONFIRM
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_new_order.*
import kotlinx.android.synthetic.main.fragment_order_confirm.*
import java.util.ArrayList

class UserOrderConfirmFragment: BaseFragment() {
    var idUser = ""
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseOrderConfirm: DatabaseReference
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter
    val listProductOrderConfirm = ArrayList<CartProductModel>()

    override fun getLayoutID(): Int {
        return R.layout.fragment_order_confirm
    }

    override fun doViewCreated() {
        auth = FirebaseAuth.getInstance()
        idUser = auth.currentUser?.uid.toString()
        databaseOrderConfirm = FirebaseDatabase.getInstance().reference.child(ORDER_CONFIRM)

        getDataOrderConfirm()
    }

    private fun initAdapter() {
        orderHistoryAdapter =
            OrderHistoryAdapter { _, _ -> }
        orderHistoryAdapter.submitList(listProductOrderConfirm)
        val linearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        frgOrderConfirm_rcvCart.setHasFixedSize(true)
        frgOrderConfirm_rcvCart.layoutManager = linearLayoutManager
        frgOrderConfirm_rcvCart.adapter = orderHistoryAdapter
    }

    private fun getDataOrderConfirm() {
        listProductOrderConfirm.clear()
        databaseOrderConfirm.child(idUser).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                view?.apply {
                    if (snapshot.exists()) {
                        for (value in snapshot.children) {
                            val cartProductModel = value.getValue(CartProductModel::class.java)
                            if (cartProductModel != null) {
                                frgOrderConfirm_tvNotification.visibility = View.GONE
                                listProductOrderConfirm.add(cartProductModel)
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
        databaseOrderConfirm.child(idUser).get().addOnCompleteListener {

        }
    }

    private fun showHideCart() {
        if (listProductOrderConfirm.isEmpty()) {
            frgOrderConfirm_tvNotification.setText(R.string.title_notification)
        } else {
            frgOrderConfirm_tvNotification.setText(R.string.title_blank)
        }
    }
}