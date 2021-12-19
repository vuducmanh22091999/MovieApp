package com.example.movieapp.ui.cart.order_completed

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.product.StatusCartModel
import com.example.movieapp.ui.order_history.adapter.CartHistoryAdapter
import com.example.movieapp.utils.ORDER
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_order_completed.*
import java.util.ArrayList

class UserOrderCompletedFragment: BaseFragment() {
    var idUser = ""
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseOrderCompleted: DatabaseReference
    private lateinit var cartHistoryAdapter: CartHistoryAdapter
    val listProductOrderCompleted = ArrayList<StatusCartModel>()

    override fun getLayoutID(): Int {
        return R.layout.fragment_order_completed
    }

    override fun doViewCreated() {
        auth = FirebaseAuth.getInstance()
        idUser = auth.currentUser?.uid.toString()
        databaseOrderCompleted = FirebaseDatabase.getInstance().reference.child(ORDER)

        getDataOrderCompleted()
    }

    private fun initAdapter() {
        cartHistoryAdapter =
            CartHistoryAdapter {_,_ ->}
        cartHistoryAdapter.submitList(listProductOrderCompleted)
        val linearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        frgOrderCompleted_rcvCart.setHasFixedSize(true)
        frgOrderCompleted_rcvCart.layoutManager = linearLayoutManager
        frgOrderCompleted_rcvCart.adapter = cartHistoryAdapter
    }

    private fun getDataOrderCompleted() {
        listProductOrderCompleted.clear()
        databaseOrderCompleted.child(idUser).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                view?.apply {
                    if (snapshot.exists()) {
                        snapshot.children.forEach {
                            it.children.forEach { value ->
                                val statusCartModel =
                                    value.getValue(StatusCartModel::class.java)
                                if (statusCartModel != null && statusCartModel.status == 3) {
                                    frgOrderCompleted_tvNotification.visibility = View.GONE
                                    listProductOrderCompleted.add(statusCartModel)
                                }
                            }
                        }
                        showHideCart()
                        initAdapter()
//                        orderHistoryAdapter.submitList(listProductNewOrder)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun showHideCart() {
        if (listProductOrderCompleted.isEmpty()) {
            frgOrderCompleted_tvNotification.setText(R.string.title_notification)
        } else {
            frgOrderCompleted_tvNotification.setText(R.string.title_blank)
        }
    }
}