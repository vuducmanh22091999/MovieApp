package com.example.movieapp.ui.home

import android.app.Dialog
import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.product.CartProductModel
import com.example.movieapp.data.model.product.ProductModel
import com.example.movieapp.ui.add.AddProductFragment
import com.example.movieapp.ui.cart.adapter.AdminCartAdapter
import com.example.movieapp.ui.edit.EditProductFragment
import com.example.movieapp.ui.home.adapter.ListProductAdapter
import com.example.movieapp.utils.*
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.dialog_question_delete.*
import kotlinx.android.synthetic.main.dialog_question_delete.dialogQuestionDelete_tvCancel
import kotlinx.android.synthetic.main.dialog_question_update.*
import kotlinx.android.synthetic.main.fragment_admin_cart.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.ArrayList

class AdminHomeFragment : BaseFragment(), View.OnClickListener {
    private lateinit var database: DatabaseReference
    private lateinit var databaseUser: DatabaseReference
    private lateinit var databaseOrderSuccess: DatabaseReference
    private lateinit var databaseNewOrder: DatabaseReference
    private var listProductAdidas = ArrayList<ProductModel>()
    private var listProductNike = ArrayList<ProductModel>()
    private var listProductConverse = ArrayList<ProductModel>()
    private var listProductPuma = ArrayList<ProductModel>()
    private var listProductJordan = ArrayList<ProductModel>()
    private lateinit var listProductAdapter: ListProductAdapter
    private lateinit var dialog: Dialog
    private lateinit var progress: ProgressDialog
    private val listIdUser = arrayListOf<String>()
    private val listProductOrderSuccess = ArrayList<CartProductModel>()
    private val listProductNewOrder = ArrayList<CartProductModel>()

    override fun getLayoutID(): Int {
        return R.layout.fragment_home
    }

    override fun doViewCreated() {
        database = FirebaseDatabase.getInstance().reference.child(PRODUCT)
        databaseUser = FirebaseDatabase.getInstance().reference.child(ACCOUNT).child(USER)
        databaseOrderSuccess = FirebaseDatabase.getInstance().reference.child(ORDER_SUCCESS)
        databaseNewOrder = FirebaseDatabase.getInstance().reference.child(NEW_ORDER)
        progress = ProgressDialog(context)
        initListener()
        getDataUser()
        setDataForList()
    }

    private fun getDataUser() {
        databaseUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (value in snapshot.children) {
                        listIdUser.add(value.key.toString())
                    }
                    getListProductOrderSuccess()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun showProgress() {
        progress.setMessage("Waiting get data...")
        progress.setCancelable(false)
        progress.show()
    }

    private fun dismissProgress() {
        progress.dismiss()
    }

    private fun openDialogEdit(
        productModel: ProductModel
    ) {
        dialog = context?.let { Dialog(it) }!!
        dialog.setContentView(R.layout.dialog_question_update)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.dialogQuestionUpdate_tvYes.setOnClickListener {
            val editProductFragment = EditProductFragment()
            val bundle = Bundle()
            bundle.putSerializable(PRODUCT_MODEL, productModel)
            editProductFragment.arguments = bundle
            addFragment(
                editProductFragment,
                R.id.frameLayout,
                EditProductFragment::class.java.simpleName
            )
            dialog.dismiss()
        }

        dialog.dialogQuestionUpdate_tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun openDialogDelete(nameProduct: String, idProduct: Long) {
        dialog = context?.let { Dialog(it) }!!
        dialog.setContentView(R.layout.dialog_question_delete)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.dialogQuestionDelete_tvDelete.setOnClickListener {
            deleteProduct(nameProduct, idProduct)
            dialog.dismiss()
        }

        dialog.dialogQuestionDelete_tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun getListProductOrderSuccess() {
        listIdUser.forEach { idUser ->
            databaseOrderSuccess.child(idUser).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (value in snapshot.children) {
                            val cartProductModel = value.getValue(CartProductModel::class.java)
                            if (cartProductModel != null) {
                                if (cartProductModel.isOrderSuccess) {
                                    listProductOrderSuccess.add(cartProductModel)
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

    private fun deleteProduct(nameProduct: String, idProduct: Long) {
        val listTest = ArrayList<CartProductModel>()
        listProductOrderSuccess.find {
            it.productModel?.id == idProduct
        }?.let {
            listTest.clear()
            listTest.add(it)
        }

        if (listTest.isNotEmpty()) {
            listTest.forEach {
                if (it.productModel?.id == idProduct)
                    Toast.makeText(context, "Can't delete product!!!", Toast.LENGTH_SHORT).show() }
        } else
            Toast.makeText(context, "Delete!!!", Toast.LENGTH_SHORT).show()
//                database.child(nameProduct).child(idProduct.toString()).removeValue()
    }

    private fun setDataForList() {
        product(ADIDAS)
        product(NIKE)
        product(CONVERSE)
        product(PUMA)
        product(JORDAN)
    }

    private fun product(typeProduct: String) {
        showProgress()
        database.child(typeProduct).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dismissProgress()
                if (snapshot.exists()) {
                    val listProduct = ArrayList<ProductModel>()
                    for (value in snapshot.children) {
                        val productModel = value.getValue(ProductModel::class.java)
                        if (productModel != null) {
                            listProduct.add(productModel)
                        }

                        listProductAdapter =
                            ListProductAdapter(listProduct.toList(), { index, _ ->
                                openDialogEdit(
                                    listProduct[index]
                                )
                            }, { index, _ ->
                                openDialogDelete(typeProduct, listProduct[index].id!!)
                                listProductAdapter.notifyItemChanged(index)
                            })
                        setupRecyclerView(typeProduct, listProduct, listProductAdapter)
                    }
                } else
                    hideRecyclerView(typeProduct)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun hideRecyclerView(typeProduct: String) {
        when (typeProduct) {
            ADIDAS ->
                frgHome_rcvAdidas.visibility = View.GONE
            NIKE ->
                frgHome_rcvNike.visibility = View.GONE
            CONVERSE ->
                frgHome_rcvConverse.visibility = View.GONE
            PUMA ->
                frgHome_rcvPuma.visibility = View.GONE
            JORDAN ->
                frgHome_rcvJordan.visibility = View.GONE
        }
    }

    private fun setupRecyclerView(
        typeProduct: String,
        listProduct: List<ProductModel>,
        listProductAdapter: ListProductAdapter
    ) {
        val linearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        when (typeProduct) {
            ADIDAS -> {
                listProductAdidas.clear()
                listProductAdidas.addAll(listProduct)
                frgHome_rcvAdidas.setHasFixedSize(true)
                frgHome_rcvAdidas.visibility = View.VISIBLE
                frgHome_rcvAdidas.layoutManager = linearLayoutManager
                frgHome_rcvAdidas.adapter = listProductAdapter
            }
            NIKE -> {
                listProductNike.clear()
                listProductNike.addAll(listProduct)
                frgHome_rcvNike.setHasFixedSize(true)
                frgHome_rcvNike.visibility = View.VISIBLE
                frgHome_rcvNike.layoutManager = linearLayoutManager
                frgHome_rcvNike.adapter = listProductAdapter
            }
            CONVERSE -> {
                listProductConverse.clear()
                listProductConverse.addAll(listProduct)
                frgHome_rcvConverse.setHasFixedSize(true)
                frgHome_rcvConverse.visibility = View.VISIBLE
                frgHome_rcvConverse.layoutManager = linearLayoutManager
                frgHome_rcvConverse.adapter = listProductAdapter
            }
            PUMA -> {
                listProductPuma.clear()
                listProductPuma.addAll(listProduct)
                frgHome_rcvPuma.setHasFixedSize(true)
                frgHome_rcvPuma.visibility = View.VISIBLE
                frgHome_rcvPuma.layoutManager = linearLayoutManager
                frgHome_rcvPuma.adapter = listProductAdapter
            }
            JORDAN -> {
                listProductJordan.clear()
                listProductJordan.addAll(listProduct)
                frgHome_rcvJordan.setHasFixedSize(true)
                frgHome_rcvJordan.visibility = View.VISIBLE
                frgHome_rcvJordan.layoutManager = linearLayoutManager
                frgHome_rcvJordan.adapter = listProductAdapter
            }
        }
        dismissProgress()
    }

    private fun initListener() {
        frgHome_imgAddAdidas.setOnClickListener(this)
        frgHome_imgAddNike.setOnClickListener(this)
        frgHome_imgAddConverse.setOnClickListener(this)
        frgHome_imgAddPuma.setOnClickListener(this)
        frgHome_imgAddJordan.setOnClickListener(this)
    }

    private fun addProductAdidas() {
        val addScreenFragment = AddProductFragment()
        val bundle = Bundle()
        bundle.putString(NAME_PRODUCT, ADIDAS)
        addScreenFragment.arguments = bundle
        addFragment(addScreenFragment, R.id.frameLayout)
    }

    private fun addProductNike() {
        val addScreenFragment = AddProductFragment()
        val bundle = Bundle()
        bundle.putString(NAME_PRODUCT, NIKE)
        addScreenFragment.arguments = bundle
        addFragment(addScreenFragment, R.id.frameLayout)
    }

    private fun addProductConverse() {
        val addScreenFragment = AddProductFragment()
        val bundle = Bundle()
        bundle.putString(NAME_PRODUCT, CONVERSE)
        addScreenFragment.arguments = bundle
        addFragment(addScreenFragment, R.id.frameLayout)
    }

    private fun addProductPuma() {
        val addScreenFragment = AddProductFragment()
        val bundle = Bundle()
        bundle.putString(NAME_PRODUCT, PUMA)
        addScreenFragment.arguments = bundle
        addFragment(addScreenFragment, R.id.frameLayout)
    }

    private fun addProductJordan() {
        val addScreenFragment = AddProductFragment()
        val bundle = Bundle()
        bundle.putString(NAME_PRODUCT, JORDAN)
        addScreenFragment.arguments = bundle
        addFragment(addScreenFragment, R.id.frameLayout)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.frgHome_imgAddAdidas -> addProductAdidas()
            R.id.frgHome_imgAddNike -> addProductNike()
            R.id.frgHome_imgAddConverse -> addProductConverse()
            R.id.frgHome_imgAddPuma -> addProductPuma()
            R.id.frgHome_imgAddJordan -> addProductJordan()
        }
    }
}