package com.example.movieapp.ui.order_history

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.product.CartProductModel
import com.example.movieapp.ui.main.UserActivity
import com.example.movieapp.ui.order_history.adapter.OrderHistoryAdapter
import com.example.movieapp.utils.ORDER_SUCCESS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_order_history.*
import java.util.ArrayList

class OrderHistoryFragment: BaseFragment() {
    private lateinit var databaseOrderSuccess: DatabaseReference
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter
    private var idUser = ""
    private lateinit var auth: FirebaseAuth
    val listProductOrderSuccess = ArrayList<CartProductModel>()

    override fun getLayoutID(): Int {
        return R.layout.fragment_order_history
    }

    override fun doViewCreated() {
        databaseOrderSuccess = FirebaseDatabase.getInstance().reference.child(ORDER_SUCCESS)
        auth = FirebaseAuth.getInstance()
        idUser = auth.currentUser?.uid.toString()
        handleBottom()
        getData()
    }

    private fun handleBottom() {
        (activity as UserActivity).hideBottom()
    }

    private fun getData() {
        databaseOrderSuccess.child(idUser).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (value in snapshot.children) {
                        val cartProductModel = value.getValue(CartProductModel::class.java)
                        if (cartProductModel != null) {
                            if (cartProductModel.isOrderSuccess) {
                                listProductOrderSuccess.add(cartProductModel)
                            }
                            orderHistoryAdapter =
                                OrderHistoryAdapter(listProductOrderSuccess.toList())
                            val linearLayoutManager =
                                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                            frgOrderHistory_rcvList.setHasFixedSize(true)
                            frgOrderHistory_rcvList.layoutManager = linearLayoutManager
                            frgOrderHistory_rcvList.adapter = orderHistoryAdapter
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}