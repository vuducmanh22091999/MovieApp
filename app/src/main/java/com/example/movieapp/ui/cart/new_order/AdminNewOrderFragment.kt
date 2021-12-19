package com.example.movieapp.ui.cart.new_order

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
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
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class AdminNewOrderFragment : BaseFragment() {
    private lateinit var databaseUser: DatabaseReference
    private val listIdUser = arrayListOf<String>()
    private lateinit var databaseNewOrder: DatabaseReference
    private lateinit var databaseOrderConfirm: DatabaseReference
    private lateinit var databaseOrderCanceled: DatabaseReference
    private lateinit var databaseTest: DatabaseReference
    private lateinit var adminCartAdapter: AdminCartAdapter
    private var indexSelectedStatus = -1
    val listNewOrder = ArrayList<CartProductModel>()
    val listTestNewOrder = ArrayList<CartProductModel>()

    override fun getLayoutID(): Int {
        return R.layout.fragment_new_order
    }

    override fun doViewCreated() {
        databaseUser = FirebaseDatabase.getInstance().reference.child(ACCOUNT).child(USER)
        databaseNewOrder = FirebaseDatabase.getInstance().reference.child(NEW_ORDER)
        databaseOrderConfirm = FirebaseDatabase.getInstance().reference.child(ORDER_CONFIRM)
        databaseOrderCanceled = FirebaseDatabase.getInstance().reference.child(ORDER_CANCELED)
        databaseTest = FirebaseDatabase.getInstance().reference.child("test")
        getDataUser()
    }

    private fun showHideCart() {
        if (listTestNewOrder.isEmpty()) {
            frgNewOrder_tvNotification.setText(R.string.title_notification)
        } else {
            frgNewOrder_tvNotification.setText(R.string.title_blank)
        }
    }

    private fun initAdapter() {
        adminCartAdapter =
            AdminCartAdapter ()
//        adminCartAdapter.submitList(listNewOrder)
        adminCartAdapter.submitList(listTestNewOrder)
        val linearLayoutManager =
            LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
        frgNewOrder_rcvCart.setHasFixedSize(true)
        frgNewOrder_rcvCart.layoutManager = linearLayoutManager
        frgNewOrder_rcvCart.adapter = adminCartAdapter
    }

    private fun checkCart() {
        if (listNewOrder.isEmpty()) {
            frgNewOrder_rcvCart.visibility = View.GONE
            frgNewOrder_tvNotification.visibility = View.VISIBLE
        }
    }

    private fun getDataUser() {
        databaseUser.get().addOnSuccessListener {
            if (it.exists()) {
                for (value in it.children) {
                    listIdUser.add(value.key.toString())
                }
//                getDataNewOrder()
                getDatabaseTest()
            }
        }
    }

    private fun getDatabaseTest() {
        listTestNewOrder.clear()
        listIdUser.forEach {
            databaseTest.child(it).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    view?.apply {
                        if (snapshot.exists()) {
                                snapshot.children.forEach { value ->
                                    value.children.forEach { test ->
                                        val cartProductModel = test.getValue(CartProductModel::class.java)
                                        if (cartProductModel != null) {
//                                            if (cartProductModel.isNewOrder) {
                                                listTestNewOrder.add(cartProductModel)
//                                            }
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

    private fun getDataNewOrder() {
        listNewOrder.clear()
        listIdUser.forEach { idUser ->
            databaseNewOrder.child(idUser).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    view?.apply {
                        if (snapshot.exists()) {
                            for (value in snapshot.children) {
                                val cartProductModel = value.getValue(CartProductModel::class.java)
                                if (cartProductModel != null) {
                                        listNewOrder.add(cartProductModel)
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

    private fun openDialogPickOrderStatus(cartProductModel: CartProductModel, index: Int) {
        val listStatus = arrayOf(
            "Order confirm",
            "Order is canceled"
        )
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.title_pick_order_status)
        builder.setSingleChoiceItems(listStatus, -1, DialogInterface.OnClickListener { _, which ->
            indexSelectedStatus = which
        })

        builder.setPositiveButton("Ok") { dialog, _ ->
            when (indexSelectedStatus) {
                0 -> {
                    setDatabaseOrderConfirm(cartProductModel)
                    listNewOrder.removeAt(index)
                    adminCartAdapter.notifyItemRemoved(index)
                }
                1 -> {
                    setDatabaseOrderCanceled(cartProductModel)
                    listNewOrder.removeAt(index)
                    adminCartAdapter.notifyItemRemoved(index)
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
        databaseOrderCanceled.child(cartProductModel.idUser!!).child(cartProductModel.idCart.toString())
            .setValue(cartProductModel)
//        databaseNewOrder.child(cartProductModel.idUser)
//            .child(cartProductModel.idCart.toString())
//            .removeValue()
    }

    private fun setDatabaseOrderConfirm(cartProductModel: CartProductModel) {
        databaseOrderConfirm.child(cartProductModel.idUser!!).child(cartProductModel.idCart.toString())
            .setValue(cartProductModel)
//        databaseNewOrder.child(cartProductModel.idUser)
//            .child(cartProductModel.idCart.toString())
//            .removeValue()
    }
}