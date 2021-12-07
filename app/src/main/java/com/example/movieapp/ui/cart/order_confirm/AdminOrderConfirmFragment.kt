package com.example.movieapp.ui.cart.order_confirm

import android.app.AlertDialog
import android.content.DialogInterface
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.product.CartProductModel
import com.example.movieapp.ui.cart.adapter.AdminCartAdapter
import com.example.movieapp.utils.*
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_new_order.*
import kotlinx.android.synthetic.main.fragment_order_confirm.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AdminOrderConfirmFragment : BaseFragment() {
    private lateinit var databaseUser: DatabaseReference
    private val listIdUser = arrayListOf<String>()
    private lateinit var databaseOrderConfirm: DatabaseReference
    private lateinit var databaseOrderDelivering: DatabaseReference
    private lateinit var adminCartAdapter: AdminCartAdapter
    private var indexSelectedStatus = -1
    val listOrderConfirm = ArrayList<CartProductModel>()

    override fun getLayoutID(): Int {
        return R.layout.fragment_order_confirm
    }

    override fun doViewCreated() {
        databaseUser = FirebaseDatabase.getInstance().reference.child(ACCOUNT).child(USER)
        databaseOrderConfirm = FirebaseDatabase.getInstance().reference.child(ORDER_CONFIRM)
        databaseOrderDelivering = FirebaseDatabase.getInstance().reference.child(ORDER_DELIVERING)
        getDataUser()
    }

    private fun showHideCart() {
        if (listOrderConfirm.isEmpty()) {
            frgOrderConfirm_tvNotification.setText(R.string.title_notification)
        } else {
            frgOrderConfirm_tvNotification.setText(R.string.title_blank)
        }
    }

    private fun initAdapter() {
        adminCartAdapter =
            AdminCartAdapter { index, _ ->
                openDialogPickOrderStatus(listOrderConfirm[index], index)
            }
        adminCartAdapter.submitList(listOrderConfirm)
        val linearLayoutManager =
            LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
        frgOrderConfirm_rcvCart.setHasFixedSize(true)
        frgOrderConfirm_rcvCart.layoutManager = linearLayoutManager
        frgOrderConfirm_rcvCart.adapter = adminCartAdapter
    }

    private fun getDataUser() {
        databaseUser.get().addOnSuccessListener {
            if (it.exists()) {
                for (value in it.children) {
                    listIdUser.add(value.key.toString())
                }
                getDataOrderConfirm()
            }
        }
    }

    private fun getDataOrderConfirm() {
        listOrderConfirm.clear()
        listIdUser.forEach { idUser ->
            databaseOrderConfirm.child(idUser).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    view?.apply {
                        if (snapshot.exists()) {
                            for (value in snapshot.children) {
                                val cartProductModel = value.getValue(CartProductModel::class.java)
                                if (cartProductModel != null) {
                                    if (cartProductModel.isOrderConfirm) {
                                        listOrderConfirm.add(cartProductModel)
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

    private fun setDatabaseOrderConfirm(cartProductModel: CartProductModel) {
        databaseOrderDelivering.child(cartProductModel.idUser!!).child(cartProductModel.idCart.toString())
            .setValue(cartProductModel)
        databaseOrderConfirm.child(cartProductModel.idUser)
            .child(cartProductModel.idCart.toString())
            .removeValue()
    }

    private fun openDialogPickOrderStatus(cartProductModel: CartProductModel, index: Int) {
        val listString = arrayOf(
            "Order is delivering"
        )
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.title_pick_order_status)
        builder.setSingleChoiceItems(listString, -1, DialogInterface.OnClickListener { _, which ->
            indexSelectedStatus = which
        })

        builder.setPositiveButton("Ok") { dialog, _ ->
            if (indexSelectedStatus == 0) {
                cartProductModel.isOrderDelivering = true
                cartProductModel.isOrderConfirm = false
                cartProductModel.isOrderCompleted = false
                cartProductModel.isOrderCanceled = false
                cartProductModel.isNewOrder = false
                cartProductModel.orderStatus = getString(R.string.title_order_delivered)
                setDatabaseOrderConfirm(cartProductModel)
                listOrderConfirm.removeAt(index)
                adminCartAdapter.notifyItemRemoved(index)
            }
            showHideCart()
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }
}