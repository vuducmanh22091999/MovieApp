package com.example.movieapp.ui.cart.order_delivering

import android.app.AlertDialog
import android.content.DialogInterface
import android.renderscript.Sampler
import android.util.Log
import android.view.View
import android.widget.TextView
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
import kotlinx.android.synthetic.main.fragment_user_account.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AdminOrderDeliveringFragment : BaseFragment() {
    private lateinit var databaseUser: DatabaseReference
    private val listIdUser = arrayListOf<String>()
    private lateinit var databaseOrderDelivering: DatabaseReference
    private lateinit var databaseOrderCompleted: DatabaseReference
    private lateinit var databaseProduct: DatabaseReference
    private lateinit var adminCartAdapter: AdminCartAdapter
    private var indexSelectedStatus = -1
    val listOrderDelivering = ArrayList<CartProductModel>()
    private val listProduct = ArrayList<ProductModel>()
    var currentDate = ""

    override fun getLayoutID(): Int {
        return R.layout.fragment_order_delivering
    }

    override fun doViewCreated() {
        databaseUser = FirebaseDatabase.getInstance().reference.child(ACCOUNT).child(USER)
        databaseOrderDelivering = FirebaseDatabase.getInstance().reference.child(ORDER_DELIVERING)
        databaseProduct = FirebaseDatabase.getInstance().reference.child(PRODUCT)
        databaseOrderCompleted = FirebaseDatabase.getInstance().reference.child(ORDER_COMPLETED)
        getDataUser()
        getDataProduct()
        getCurrentDate()
    }

    private fun showHideCart() {
        if (listOrderDelivering.isEmpty()) {
            frgOrderDelivering_tvNotification.setText(R.string.title_notification)
        } else {
            frgOrderDelivering_tvNotification.setText(R.string.title_blank)
        }
    }

    private fun getCurrentDate() {
        val currentTime = Calendar.getInstance().time
        val sdf = SimpleDateFormat("dd/M/yyyy")
        currentDate = sdf.format(currentTime)
        Log.d("testDateTime ", currentDate)
    }

    private fun initAdapter() {
        adminCartAdapter =
            AdminCartAdapter { index, _ ->
                openDialogPickOrderStatus(listOrderDelivering[index], index)
            }
        adminCartAdapter.submitList(listOrderDelivering)
        val linearLayoutManager =
            LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
        frgOrderDelivering_rcvCart.setHasFixedSize(true)
        frgOrderDelivering_rcvCart.layoutManager = linearLayoutManager
        frgOrderDelivering_rcvCart.adapter = adminCartAdapter
    }

    private fun getDataUser() {
        databaseUser.get().addOnSuccessListener {
            if (it.exists()) {
                for (value in it.children) {
                    listIdUser.add(value.key.toString())
                }
                getDataOrderDelivering()
            }
        }
    }

    private fun getDataOrderDelivering() {
        listOrderDelivering.clear()
        listIdUser.forEach { idUser ->
            databaseOrderDelivering.child(idUser)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        view?.apply {
                            if (snapshot.exists()) {
                                for (value in snapshot.children) {
                                    val cartProductModel = value.getValue(CartProductModel::class.java)
                                    if (cartProductModel != null) {
                                        if (cartProductModel.isOrderDelivering) {
                                            listOrderDelivering.add(cartProductModel)
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
        listOrderDelivering.forEach { cartProductModel ->
            listProduct.firstOrNull {
                it.id == cartProductModel.productModel?.id
            }?.let { productModel ->
                productModel.listSize.firstOrNull { sizeProductModel ->
                    sizeProductModel.size == cartProductModel.size
                }?.let {
                    it.amountSize -= cartProductModel.amountUserOrder
                    updateDatabase(productModel.id!!, productModel)
                }
            }
        }
    }

    private fun updateDatabase(key: Long, productModel: ProductModel) {
        databaseProduct.child(productModel.type!!).child(key.toString()).setValue(productModel)
            .addOnSuccessListener {
                Toast.makeText(context, "Update database!!!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setDatabaseOrderDelivering(cartProductModel: CartProductModel) {
        databaseOrderCompleted.child(cartProductModel.idUser!!).child(cartProductModel.idCart.toString())
            .setValue(cartProductModel)
        updateProduct()
        databaseOrderDelivering.child(cartProductModel.idUser)
            .child(cartProductModel.idCart.toString())
            .removeValue()
    }

    private fun openDialogPickOrderStatus(cartProductModel: CartProductModel, index: Int) {
        val listString = arrayOf(
            "Order completed"
        )
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.title_pick_order_status)
        builder.setSingleChoiceItems(listString, -1, DialogInterface.OnClickListener { _, which ->
            indexSelectedStatus = which
        })

        builder.setPositiveButton("Ok") { dialog, _ ->
            if (indexSelectedStatus == 0) {
                cartProductModel.isOrderCompleted = true
                cartProductModel.isOrderConfirm = false
                cartProductModel.isOrderDelivering = false
                cartProductModel.isOrderCanceled = false
                cartProductModel.orderDateCompleted = currentDate
                cartProductModel.isNewOrder = false
                cartProductModel.orderStatus = getString(R.string.title_order_completed)
                setDatabaseOrderDelivering(cartProductModel)
                listOrderDelivering.removeAt(index)
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