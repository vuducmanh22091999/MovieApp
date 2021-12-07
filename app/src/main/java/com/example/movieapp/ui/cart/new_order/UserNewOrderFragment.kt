package com.example.movieapp.ui.cart.new_order

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.product.CartProductModel
import com.example.movieapp.ui.order_history.adapter.OrderHistoryAdapter
import com.example.movieapp.utils.NEW_ORDER
import com.example.movieapp.utils.ORDER_CANCELED
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_new_order.*
import java.util.ArrayList

class UserNewOrderFragment : BaseFragment() {
    var idUser = ""
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseNewOrder: DatabaseReference
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter
    private lateinit var databaseOrderCanceled: DatabaseReference
    val listProductNewOrder = ArrayList<CartProductModel>()
    var indexSelectedStatus = 0

    override fun getLayoutID(): Int {
        return R.layout.fragment_new_order
    }

    override fun doViewCreated() {
        auth = FirebaseAuth.getInstance()
        idUser = auth.currentUser?.uid.toString()
        databaseNewOrder = FirebaseDatabase.getInstance().reference.child(NEW_ORDER)
        databaseOrderCanceled = FirebaseDatabase.getInstance().reference.child(ORDER_CANCELED)

        getDataNewOrder()
    }

    private fun initAdapter() {
        orderHistoryAdapter =
            OrderHistoryAdapter { index, _ ->
                openDialogPickOrderStatus(listProductNewOrder[index], index)
            }
        orderHistoryAdapter.submitList(listProductNewOrder)
        val linearLayoutManager =
            LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
        frgNewOrder_rcvCart.setHasFixedSize(true)
        frgNewOrder_rcvCart.layoutManager = linearLayoutManager
        frgNewOrder_rcvCart.adapter = orderHistoryAdapter
    }

    private fun getDataNewOrder() {
        listProductNewOrder.clear()
        databaseNewOrder.child(idUser).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                view?.apply {
                    if (snapshot.exists()) {
                        for (value in snapshot.children) {
                            val cartProductModel = value.getValue(CartProductModel::class.java)
                            if (cartProductModel != null) {
                                listProductNewOrder.add(cartProductModel)
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

    private fun openDialogPickOrderStatus(cartProductModel: CartProductModel, index: Int) {
        val listStatus = arrayOf(
            "Cancel order"
        )
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.title_pick_order_status)
        builder.setSingleChoiceItems(listStatus, -1, DialogInterface.OnClickListener { _, which ->
            indexSelectedStatus = which
        })

        builder.setPositiveButton("Ok") { dialog, _ ->
            when (indexSelectedStatus) {
                0 -> {
                    cartProductModel.isOrderCanceled = true
                    cartProductModel.isOrderConfirm = false
                    cartProductModel.isOrderDelivering = false
                    cartProductModel.isOrderCompleted = false
                    cartProductModel.isNewOrder = false
                    cartProductModel.orderStatus = getString(R.string.title_order_cancel)
                    setDatabaseOrderCanceled(cartProductModel)
                    listProductNewOrder.removeAt(index)
                    orderHistoryAdapter.notifyItemRemoved(index)
                }
            }
            showHideCart()
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun setDatabaseOrderCanceled(cartProductModel: CartProductModel) {
        databaseOrderCanceled.child(cartProductModel.idUser!!)
            .child(cartProductModel.idCart.toString())
            .setValue(cartProductModel)
//        databaseNewOrder.child(cartProductModel.idUser)
//            .child(cartProductModel.idCart.toString())
//            .removeValue()
    }

    private fun showHideCart() {
        if (listProductNewOrder.isEmpty()) {
            frgNewOrder_tvNotification.setText(R.string.title_notification)
        } else {
            frgNewOrder_tvNotification.setText(R.string.title_blank)
        }
    }
}