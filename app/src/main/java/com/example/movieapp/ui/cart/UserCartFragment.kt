package com.example.movieapp.ui.cart

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.product.CartProductModel
import com.example.movieapp.data.model.product.ProductModel
import com.example.movieapp.ui.cart.adapter.UserCartAdapter
import com.example.movieapp.ui.login.LoginActivity
import com.example.movieapp.utils.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.dialog_question_delete.*
import kotlinx.android.synthetic.main.dialog_question_login.*
import kotlinx.android.synthetic.main.fragment_user_cart.*

class UserCartFragment : BaseFragment(), View.OnClickListener {
    private lateinit var database: DatabaseReference
    private lateinit var databaseOrderSuccess: DatabaseReference
    private lateinit var databaseNewOrder: DatabaseReference
    private lateinit var databaseProduct: DatabaseReference
    private lateinit var userCartAdapter: UserCartAdapter
    private lateinit var dialog: Dialog
    private var total = 0L
    private var idUser = ""
    private lateinit var auth: FirebaseAuth
    private lateinit var progress: ProgressDialog
    private var listCartProduct = ArrayList<CartProductModel>()
    private val listProduct = ArrayList<ProductModel>()

    override fun getLayoutID(): Int {
        return R.layout.fragment_user_cart
    }

    override fun doViewCreated() {
        database = FirebaseDatabase.getInstance().reference.child(USER_CART)
        databaseProduct = FirebaseDatabase.getInstance().reference.child(PRODUCT)
        databaseOrderSuccess = FirebaseDatabase.getInstance().reference.child(ORDER_SUCCESS)
        databaseNewOrder = FirebaseDatabase.getInstance().reference.child(NEW_ORDER)
        auth = FirebaseAuth.getInstance()
        idUser = auth.currentUser?.uid.toString()
        progress = ProgressDialog(context)
        initListener()
        checkCart()
        listCartProduct()
        getDataProduct()
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

    private fun initListener() {
        frgUserCart_tvOrder.setOnClickListener(this)
    }

    private fun checkCart() {
        if (listCartProduct.isEmpty()) {
            frgUserCart_tvNotification.visibility = View.VISIBLE
            frgUserCart_tvOrder.visibility = View.GONE
            frgUserCart_tvTotal.visibility = View.GONE
            frgUserCart_tvValueTotal.visibility = View.GONE
        }
    }

    private fun dialogDeleteCart(idCart: Long) {
        dialog = context?.let { Dialog(it) }!!
        dialog.setContentView(R.layout.dialog_question_delete)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.dialogQuestionDelete_tvDelete.setOnClickListener {
            deleteItemCart(idCart)
            dialog.dismiss()
        }

        dialog.dialogQuestionDelete_tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun deleteItemCart(idCart: Long) {
        database.child(idUser).child(idCart.toString()).removeValue()
    }

    private fun listCartProduct() {
        database.child(idUser).addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    frgUserCart_tvNotification.visibility = View.GONE
                    frgUserCart_rcvListCart.visibility = View.VISIBLE
                    frgUserCart_tvOrder.visibility = View.VISIBLE
                    frgUserCart_tvTotal.visibility = View.VISIBLE
                    frgUserCart_tvValueTotal.visibility = View.VISIBLE
                    listCartProduct.clear()
                    total = 0
                    for (value in snapshot.children) {
                        val cartProductModel = value.getValue(CartProductModel::class.java)
                        if (cartProductModel != null) {
                            listCartProduct.add(cartProductModel)
                            total += cartProductModel.totalPrice
                        }
                        frgUserCart_tvValueTotal.text = "${formatStringLong(total)}$"

                        userCartAdapter = UserCartAdapter(listCartProduct.toList(), { index, _ ->
                            listCartProduct[index].amountUserOrder--
                            if (listCartProduct[index].amountUserOrder == 0L)
                                dialogDeleteCart(listCartProduct[index].idCart!!)
                            userCartAdapter.notifyItemRemoved(index)
                            updateDecreasePrice(listCartProduct, index)
                            updateCart(listCartProduct[index])
                        }, { index, _ ->
                            listCartProduct[index].amountUserOrder++
                            userCartAdapter.notifyItemChanged(index)
                            updateIncreasePrice(listCartProduct, index)
                            updateCart(listCartProduct[index])
                        })
                        val linearLayoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                        frgUserCart_rcvListCart.setHasFixedSize(true)
                        frgUserCart_rcvListCart.layoutManager = linearLayoutManager
                        frgUserCart_rcvListCart.adapter = userCartAdapter
                    }
                } else {
                    frgUserCart_tvNotification.visibility = View.VISIBLE
                    frgUserCart_rcvListCart.visibility = View.GONE
                    frgUserCart_tvTotal.visibility = View.GONE
                    frgUserCart_tvOrder.visibility = View.GONE
                    frgUserCart_tvValueTotal.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    @SuppressLint("SetTextI18n")
    private fun updateIncreasePrice(list: ArrayList<CartProductModel>, index: Int) {
        list[index].totalPrice = list[index].totalPrice + list[index].productModel?.price!!
        total += list[index].productModel?.price!!
        frgUserCart_tvValueTotal.text = "${formatStringLong(total)}$"
    }

    @SuppressLint("SetTextI18n")
    private fun updateDecreasePrice(list: ArrayList<CartProductModel>, index: Int) {
        list[index].totalPrice = list[index].totalPrice - list[index].productModel?.price!!
        total -= list[index].productModel?.price!!
        frgUserCart_tvValueTotal.text = "${formatStringLong(total)}$"
    }

    fun test() {
        val result = "result"
        parentFragmentManager.setFragmentResult("test", bundleOf("bundleKey" to result))
    }

    fun Fragment.setFragmentResult(
        requestKey: String,
        result: Bundle
    ) = parentFragmentManager.setFragmentResult(requestKey, result)

    private fun updateCart(cartProductModel: CartProductModel) {
        if (auth.currentUser?.uid?.isNotEmpty() == true)
            database.child(idUser).child(cartProductModel.idCart.toString())
                .setValue(cartProductModel)
        else
            database.child("null").child(cartProductModel.idCart.toString())
                .setValue(cartProductModel)
    }

    private fun moveToLogin() {
        dialog = context?.let { Dialog(it) }!!
        dialog.setContentView(R.layout.dialog_question_login)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.dialogQuestionLogin_tvYes.setOnClickListener {
            Handler().postDelayed({
                val intentNewScreen = Intent(requireContext(), LoginActivity::class.java)
                intentNewScreen.putExtra("hideRegister", false)
                startActivity(intentNewScreen)
            }, SPLASH_DISPLAY_LENGTH)
        }

        dialog.dialogQuestionLogin_tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun clickToOrder() {
        if (auth.currentUser?.uid?.isNotEmpty() == true) {
            var error = ""
            listCartProduct.forEach { cartProductModel ->
                listProduct.firstOrNull {
                    it.id == cartProductModel.productModel?.id
                }?.let { productModel ->
                    productModel.listSize.firstOrNull { sizeProductModel ->
                        sizeProductModel.size == cartProductModel.size
                    }?.let {
                        if (cartProductModel.amountUserOrder > it.amountSize) {
                            error += "${productModel.name} - ${it.size} - Current amount: ${it.amountSize}\n"
                        }
                    }
                }
            }

            if (error.isEmpty()) {
                showProgress()
                listCartProduct.forEach { cartProductModel ->
                    listProduct.firstOrNull {
                        it.id == cartProductModel.productModel?.id
                    }?.let { productModel ->
                        productModel.listSize.firstOrNull { sizeProductModel ->
                            sizeProductModel.size == cartProductModel.size
                        }?.let {
//                            it.amountSize -= cartProductModel.amountUserOrder
                            setDatabase(productModel.id!!, productModel)
                            setDatabaseNewOrder(cartProductModel)
                        }
                    }
                }
            } else
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        } else
            moveToLogin()
    }

    private fun setDatabase(key: Long, productModel: ProductModel) {
        databaseProduct.child(productModel.type!!).child(key.toString()).setValue(productModel)
            .addOnSuccessListener {
                dismissProgress()
                listCartProduct.clear()
                Toast.makeText(context, "Order success!!!", Toast.LENGTH_SHORT).show()
                frgUserCart_rcvListCart.visibility = View.GONE
                frgUserCart_tvValueTotal.visibility = View.GONE
                frgUserCart_tvNotification.visibility = View.VISIBLE
                frgUserCart_tvTotal.visibility = View.GONE
                frgUserCart_tvOrder.visibility = View.GONE
                database.child(idUser).removeValue()
            }
    }

    private fun setDatabaseNewOrder(cartProductModel: CartProductModel) {
        val key = System.currentTimeMillis()
        cartProductModel.isOrderSuccess = true
        cartProductModel.isNewOrder = true
        cartProductModel.orderStatus = getString(R.string.title_new_order)
        cartProductModel.idCart = key
        databaseNewOrder.child(idUser).child(key.toString()).setValue(cartProductModel).addOnCompleteListener {
            dismissProgress()
            listCartProduct.clear()
            Toast.makeText(context, "Order success!!!", Toast.LENGTH_SHORT).show()
            frgUserCart_rcvListCart.visibility = View.GONE
            frgUserCart_tvValueTotal.visibility = View.GONE
            frgUserCart_tvNotification.visibility = View.VISIBLE
            frgUserCart_tvTotal.visibility = View.GONE
            frgUserCart_tvOrder.visibility = View.GONE
            database.child(idUser).removeValue()
        }
    }

    private fun showProgress() {
        progress.setMessage("Waiting order...")
        progress.setCancelable(false)
        progress.show()
    }

    private fun dismissProgress() {
        progress.dismiss()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.frgUserCart_tvOrder -> {
                clickToOrder()
            }
        }
    }
}