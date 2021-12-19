package com.example.movieapp.ui.cart.new_order

import android.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.product.StatusCartModel
import com.example.movieapp.ui.order_history.adapter.CartHistoryAdapter
import com.example.movieapp.utils.NEW_ORDER
import com.example.movieapp.utils.ORDER
import com.example.movieapp.utils.ORDER_CANCELED
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_new_order.*
import java.util.ArrayList

class UserNewOrderFragment : BaseFragment() {
    var idUser = ""
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseNewOrder: DatabaseReference
    private lateinit var cartHistoryAdapter: CartHistoryAdapter
    private lateinit var databaseOrderCanceled: DatabaseReference
    private lateinit var databaseOrder: DatabaseReference
    private val listProductNewOrder = ArrayList<StatusCartModel>()
    private var indexSelectedStatus = 0

    override fun getLayoutID(): Int {
        return R.layout.fragment_new_order
    }

    override fun doViewCreated() {
        auth = FirebaseAuth.getInstance()
        idUser = auth.currentUser?.uid.toString()
        databaseNewOrder = FirebaseDatabase.getInstance().reference.child(NEW_ORDER)
        databaseOrder = FirebaseDatabase.getInstance().reference.child(ORDER)
        databaseOrderCanceled = FirebaseDatabase.getInstance().reference.child(ORDER_CANCELED)

        getDataNewOrder()
    }

    private fun initAdapter() {
        cartHistoryAdapter =
            CartHistoryAdapter { index, _ ->
                openDialogPickOrderStatus(listProductNewOrder[index], index)
            }
        cartHistoryAdapter.submitList(listProductNewOrder)
        val linearLayoutManager =
            LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
        frgNewOrder_rcvCart.setHasFixedSize(true)
        frgNewOrder_rcvCart.layoutManager = linearLayoutManager
        frgNewOrder_rcvCart.adapter = cartHistoryAdapter
    }

    private fun checkIdOrder(statusCartModel: StatusCartModel) {
        val model = listProductNewOrder.firstOrNull {
            it.idOrder == statusCartModel.idOrder
        }
        if (model != null)
            model.status = statusCartModel.status
//            listProductNewOrder.remove(statusCartModel)
        else
            listProductNewOrder.add(statusCartModel)
    }

    private fun getDataNewOrder() {
        listProductNewOrder.clear()
        databaseOrder.child(idUser).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                view?.apply {
                    if (snapshot.exists()) {
                        snapshot.children.forEach {
                            it.children.forEach { value ->
                                val statusCartModel =
                                    value.getValue(StatusCartModel::class.java)
                                if (statusCartModel != null && statusCartModel.status == 0) {
                                    checkIdOrder(statusCartModel)
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

    private fun openDialogPickOrderStatus(statusCartModel: StatusCartModel, index: Int) {
        val listStatus = arrayOf(
            "Cancel order"
        )
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.title_pick_order_status)
        builder.setSingleChoiceItems(listStatus, -1) { _, which ->
            indexSelectedStatus = which
        }

        builder.setPositiveButton("Ok") { dialog, _ ->
            when (indexSelectedStatus) {
                0 -> {
                    statusCartModel.status = 4
                    statusCartModel.valueStatus = getString(R.string.title_order_cancel)
                    setDatabaseOrderCanceled(statusCartModel)
                    listProductNewOrder.removeAt(index)
                    cartHistoryAdapter.notifyItemRemoved(index)
                    cartHistoryAdapter.submitList(listProductNewOrder)
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

    private fun setDatabaseOrderCanceled(statusCartModel: StatusCartModel) {
        databaseOrder.child(statusCartModel.listProduct[0].idUser.toString())
            .child(statusCartModel.idOrder.toString()).child("0").setValue(statusCartModel)
    }

    private fun showHideCart() {
        if (listProductNewOrder.isEmpty()) {
            frgNewOrder_tvNotification.setText(R.string.title_notification)
        } else {
            frgNewOrder_tvNotification.setText(R.string.title_blank)
        }
    }
}