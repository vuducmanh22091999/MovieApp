package com.example.movieapp.ui.cart.order_canceled

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.product.StatusCartModel
import com.example.movieapp.ui.order_history.adapter.CartHistoryAdapter
import com.example.movieapp.ui.order_history.adapter.OrderHistoryAdapter
import com.example.movieapp.utils.ORDER
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_order_canceled.*
import java.util.ArrayList

class UserOrderCanceledFragment: BaseFragment() {
    var idUser = ""
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseOrder: DatabaseReference
    private lateinit var cartHistoryAdapter: CartHistoryAdapter
    val listProductOrderCanceled = ArrayList<StatusCartModel>()

    override fun getLayoutID(): Int {
        return R.layout.fragment_order_canceled
    }

    override fun doViewCreated() {
        auth = FirebaseAuth.getInstance()
        idUser = auth.currentUser?.uid.toString()
        databaseOrder = FirebaseDatabase.getInstance().reference.child(ORDER)

        getDataOrderCanceled()
    }

    private fun initAdapter() {
        cartHistoryAdapter =
            CartHistoryAdapter { _, _ ->}
        cartHistoryAdapter.submitList(listProductOrderCanceled)
        val linearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        frgOrderCanceled_rcvCart.setHasFixedSize(true)
        frgOrderCanceled_rcvCart.layoutManager = linearLayoutManager
        frgOrderCanceled_rcvCart.adapter = cartHistoryAdapter
    }

    private fun getDataOrderCanceled() {
        listProductOrderCanceled.clear()
        databaseOrder.child(idUser).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                view?.apply {
                    if (snapshot.exists()) {
                        snapshot.children.forEach {
                            it.children.forEach { value ->
                                val statusCartModel =
                                    value.getValue(StatusCartModel::class.java)
                                if (statusCartModel != null && statusCartModel.status == 4) {
                                    listProductOrderCanceled.add(statusCartModel)
                                }
                            }
                        }
                        showHideCart()
                        initAdapter()
//                        orderHistoryAdapter.submitList(listProductOrderCanceled)
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