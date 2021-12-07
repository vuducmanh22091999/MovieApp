package com.example.movieapp.ui.cart.order_completed

import android.app.AlertDialog
import android.content.DialogInterface
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.product.CartProductModel
import com.example.movieapp.data.model.product.ProductModel
import com.example.movieapp.ui.cart.adapter.AdminCartAdapter
import com.example.movieapp.utils.*
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_order_completed.*
import kotlinx.android.synthetic.main.fragment_order_confirm.*
import kotlinx.android.synthetic.main.fragment_order_delivering.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AdminOrderCompletedFragment : BaseFragment() {
    private lateinit var databaseUser: DatabaseReference
    private val listIdUser = arrayListOf<String>()
    private lateinit var databaseOrderDelivering: DatabaseReference
    private lateinit var databaseOrderCompleted: DatabaseReference
    private lateinit var databaseProduct: DatabaseReference
    private lateinit var adminCartAdapter: AdminCartAdapter
    val listOrderCompleted = ArrayList<CartProductModel>()
    private val listProduct = ArrayList<ProductModel>()

    override fun getLayoutID(): Int {
        return R.layout.fragment_order_completed
    }

    override fun doViewCreated() {
        databaseUser = FirebaseDatabase.getInstance().reference.child(ACCOUNT).child(USER)
        databaseProduct = FirebaseDatabase.getInstance().reference.child(PRODUCT)
        databaseOrderDelivering = FirebaseDatabase.getInstance().reference.child(ORDER_DELIVERING)
        databaseOrderCompleted = FirebaseDatabase.getInstance().reference.child(ORDER_COMPLETED)
        getDataUser()
        getDataProduct()
    }

    private fun showHideCart() {
        if (listOrderCompleted.isEmpty()) {
            frgOrderCompleted_tvNotification.setText(R.string.title_notification)
        } else {
            frgOrderCompleted_tvNotification.setText(R.string.title_blank)
        }
    }

    private fun initAdapter() {
        adminCartAdapter =
            AdminCartAdapter { _, _ -> }
        adminCartAdapter.submitList(listOrderCompleted)
        val linearLayoutManager =
            LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
        frgOrderCompleted_rcvCart.setHasFixedSize(true)
        frgOrderCompleted_rcvCart.layoutManager = linearLayoutManager
        frgOrderCompleted_rcvCart.adapter = adminCartAdapter
    }

    private fun getDataUser() {
        databaseUser.get().addOnSuccessListener {
            if (it.exists()) {
                for (value in it.children) {
                    listIdUser.add(value.key.toString())
                }
                getDataOrderCompleted()
            }
        }
    }

    private fun getDataProduct() {
        getDataProduct(ADIDAS)
        getDataProduct(NIKE)
        getDataProduct(CONVERSE)
        getDataProduct(PUMA)
        getDataProduct(JORDAN)
    }

    private fun getDataProduct(type: String) {
        databaseProduct.child((type)).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (value in snapshot.children) {
                        val productModel = value.getValue(ProductModel::class.java)
                        if (productModel != null) {
                            listProduct.add(productModel)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun updateProduct() {
        listOrderCompleted.forEach { cartProductModel ->
            listProduct.firstOrNull {
                it.id == cartProductModel.productModel?.id
            }?.let { productModel ->
                productModel.listSize.firstOrNull { sizeProductModel ->
                    sizeProductModel.size == cartProductModel.size
                }?.let {
                    it.amountSize -= cartProductModel.amountUserOrder
                    Toast.makeText(context, "Update database!!!", Toast.LENGTH_SHORT).show()
//                    updateDatabase(productModel.id!!, productModel)
                }
            }
        }
    }

    private fun getDataOrderCompleted() {
        listOrderCompleted.clear()
        listIdUser.forEach { idUser ->
            databaseOrderCompleted.child(idUser).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    view?.apply {
                        if (snapshot.exists()) {
                            for (value in snapshot.children) {
                                val cartProductModel = value.getValue(CartProductModel::class.java)
                                if (cartProductModel != null) {
                                    if (cartProductModel.isOrderCompleted) {
                                        listOrderCompleted.add(cartProductModel)
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

    private fun setDatabaseOrderCompleted(cartProductModel: CartProductModel) {
        val key = System.currentTimeMillis()
        databaseOrderCompleted.child(cartProductModel.idUser!!).child(key.toString())
            .setValue(cartProductModel)
        updateProduct()
        if (cartProductModel.isOrderCompleted)
            databaseOrderDelivering.child(cartProductModel.idUser)
                .child(cartProductModel.idCart.toString())
                .removeValue()
    }
}