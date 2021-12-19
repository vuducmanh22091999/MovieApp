package com.example.movieapp.ui.cart.order_delivering

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.product.StatusCartModel
import com.example.movieapp.ui.order_history.adapter.CartHistoryAdapter
import com.example.movieapp.ui.order_history.adapter.OrderHistoryAdapter
import com.example.movieapp.utils.ORDER
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_order_delivering.*
import java.util.ArrayList

class UserOrderDeliveringFragment: BaseFragment() {
    var idUser = ""
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseOrderDelivering: DatabaseReference
    private lateinit var cartHistoryAdapter: CartHistoryAdapter
    val listProductOrderDelivering = ArrayList<StatusCartModel>()

    override fun getLayoutID(): Int {
        return R.layout.fragment_order_delivering
    }

    override fun doViewCreated() {
        auth = FirebaseAuth.getInstance()
        idUser = auth.currentUser?.uid.toString()
        databaseOrderDelivering = FirebaseDatabase.getInstance().reference.child(ORDER)

        getDataOrderDelivering()
    }

    private fun initAdapter() {
        cartHistoryAdapter =
            CartHistoryAdapter {_, _ -> }
        cartHistoryAdapter.submitList(listProductOrderDelivering)
        val linearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        frgOrderDelivering_rcvCart.setHasFixedSize(true)
        frgOrderDelivering_rcvCart.layoutManager = linearLayoutManager
        frgOrderDelivering_rcvCart.adapter = cartHistoryAdapter
    }

    private fun getDataOrderDelivering() {
        listProductOrderDelivering.clear()
        databaseOrderDelivering.child(idUser).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                view?.apply {
                    if (snapshot.exists()) {
                        snapshot.children.forEach {
                            it.children.forEach { value ->
                                val statusCartModel =
                                    value.getValue(StatusCartModel::class.java)
                                if (statusCartModel != null && statusCartModel.status == 2) {
                                    listProductOrderDelivering.add(statusCartModel)
                                }
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
        if (listProductOrderDelivering.isEmpty()) {
            frgOrderDelivering_tvNotification.setText(R.string.title_notification)
        } else {
            frgOrderDelivering_tvNotification.setText(R.string.title_blank)
        }
    }
}