package com.example.movieapp.ui.cart.order_confirm

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.product.CartProductModel
import com.example.movieapp.data.model.product.StatusCartModel
import com.example.movieapp.ui.order_history.adapter.CartHistoryAdapter
import com.example.movieapp.ui.order_history.adapter.OrderHistoryAdapter
import com.example.movieapp.utils.ORDER
import com.example.movieapp.utils.ORDER_CONFIRM
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_new_order.*
import kotlinx.android.synthetic.main.fragment_order_confirm.*
import java.util.ArrayList

class UserOrderConfirmFragment : BaseFragment() {
    var idUser = ""
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseOrderConfirm: DatabaseReference
    private lateinit var cartHistoryAdapter: CartHistoryAdapter
    val listProductOrderConfirm = ArrayList<StatusCartModel>()

    override fun getLayoutID(): Int {
        return R.layout.fragment_order_confirm
    }

    override fun doViewCreated() {
        auth = FirebaseAuth.getInstance()
        idUser = auth.currentUser?.uid.toString()
        databaseOrderConfirm = FirebaseDatabase.getInstance().reference.child(ORDER)

        getDataOrderConfirm()
    }

    private fun initAdapter() {
        cartHistoryAdapter =
            CartHistoryAdapter { _, _ -> }
        cartHistoryAdapter.submitList(listProductOrderConfirm)
        val linearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        frgOrderConfirm_rcvCart.setHasFixedSize(true)
        frgOrderConfirm_rcvCart.layoutManager = linearLayoutManager
        frgOrderConfirm_rcvCart.adapter = cartHistoryAdapter
    }

    private fun getDataOrderConfirm() {
//        listProductOrderConfirm.clear()
        databaseOrderConfirm.child(idUser).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listProductOrderConfirm.clear()
                view?.apply {
                    if (snapshot.exists()) {
                        snapshot.children.forEach {
                            it.children.forEach { value ->
                                val statusCartModel =
                                    value.getValue(StatusCartModel::class.java)
                                if (statusCartModel != null && statusCartModel.status == 1) {
                                    listProductOrderConfirm.add(statusCartModel)
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
        if (listProductOrderConfirm.isEmpty()) {
            frgOrderConfirm_tvNotification.setText(R.string.title_notification)
        } else {
            frgOrderConfirm_tvNotification.setText(R.string.title_blank)
        }
    }
}