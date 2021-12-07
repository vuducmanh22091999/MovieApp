package com.example.movieapp.ui.cart.order_canceled

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.product.CartProductModel
import com.example.movieapp.ui.cart.adapter.AdminCartAdapter
import com.example.movieapp.utils.*
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_order_canceled.*
import java.util.ArrayList

class AdminOrderCanceledFragment : BaseFragment() {
    private lateinit var databaseUser: DatabaseReference
    private val listIdUser = arrayListOf<String>()
    private lateinit var databaseOrderCanceled: DatabaseReference
    private lateinit var adminCartAdapter: AdminCartAdapter
    val listOrderCanceled = ArrayList<CartProductModel>()

    override fun getLayoutID(): Int {
        return R.layout.fragment_order_canceled
    }

    override fun doViewCreated() {
        databaseUser = FirebaseDatabase.getInstance().reference.child(ACCOUNT).child(USER)
        databaseOrderCanceled = FirebaseDatabase.getInstance().reference.child(ORDER_CANCELED)
        getDataUser()
    }

    private fun showHideCart() {
        if (listOrderCanceled.isEmpty()) {
            frgOrderCanceled_tvNotification.setText(R.string.title_notification)
        } else {
            frgOrderCanceled_tvNotification.setText(R.string.title_blank)
        }
    }

    private fun initAdapter() {
        adminCartAdapter =
            AdminCartAdapter { _, _ -> }
        adminCartAdapter.submitList(listOrderCanceled)
        val linearLayoutManager =
            LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
        frgOrderCanceled_rcvCart.setHasFixedSize(true)
        frgOrderCanceled_rcvCart.layoutManager = linearLayoutManager
        frgOrderCanceled_rcvCart.adapter = adminCartAdapter
    }

    private fun getDataUser() {
        databaseUser.get().addOnSuccessListener {
            if (it.exists()) {
                for (value in it.children) {
                    listIdUser.add(value.key.toString())
                }
                getDataOrderCanceled()
            }
        }
    }

    private fun getDataOrderCanceled() {
        listOrderCanceled.clear()
        listIdUser.forEach { idUser ->
            databaseOrderCanceled.child(idUser).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    view?.apply {
                        if (snapshot.exists()) {
                            for (value in snapshot.children) {
                                val cartProductModel = value.getValue(CartProductModel::class.java)
                                if (cartProductModel != null) {
                                    if (cartProductModel.isOrderCanceled) {
                                        listOrderCanceled.add(cartProductModel)
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
    }
}