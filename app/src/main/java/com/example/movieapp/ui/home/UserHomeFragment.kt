package com.example.movieapp.ui.home

import android.app.ProgressDialog
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.product.ProductModel
import com.example.movieapp.ui.home.adapter.UserProductAdapter
import com.example.movieapp.utils.*
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_user_home.*
import java.util.ArrayList

class UserHomeFragment : BaseFragment() {
    private lateinit var database: DatabaseReference
    private var listProductAdidas = ArrayList<ProductModel>()
    private var listProductNike = ArrayList<ProductModel>()
    private var listProductConverse = ArrayList<ProductModel>()
    private var listProductPuma = ArrayList<ProductModel>()
    private var listProductJordan = ArrayList<ProductModel>()
    private lateinit var userProductAdapter: UserProductAdapter
    private lateinit var storage: StorageReference
    private lateinit var progress: ProgressDialog

    override fun getLayoutID(): Int {
        return R.layout.fragment_user_home
    }

    override fun doViewCreated() {
        database = FirebaseDatabase.getInstance().reference.child(PRODUCT)
        storage = FirebaseStorage.getInstance().getReference("Images")
        progress = ProgressDialog(context)
        setDataForList()
    }

    private fun showProgress() {
        progress.setMessage("Waiting get data...")
        progress.setCancelable(false)
        progress.show()
    }

    private fun dismissProgress() {
        progress.dismiss()
    }

    private fun setDataForList() {
        userProduct(ADIDAS)
        userProduct(NIKE)
        userProduct(CONVERSE)
        userProduct(PUMA)
        userProduct(JORDAN)
    }

    private fun userProduct(typeProduct: String) {
        showProgress()
        database.child(typeProduct).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val listProduct = ArrayList<ProductModel>()
                    for (value in snapshot.children) {
                        val productModel = value.getValue(ProductModel::class.java)
                        if (productModel != null) {
                            listProduct.add(productModel)
                        }

                        userProductAdapter =
                            UserProductAdapter(listProduct.toList()) { index, string ->
                            Toast.makeText(context, listProduct[index].toString(), Toast.LENGTH_SHORT).show()
                            }
                        setupRecyclerView(typeProduct, listProduct, userProductAdapter)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun setupRecyclerView(
        typeProduct: String,
        listProduct: List<ProductModel>,
        userProductAdapter: UserProductAdapter
    ) {
        val linearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        when (typeProduct) {
            ADIDAS -> {
                listProductAdidas.clear()
                listProductAdidas.addAll(listProduct)
                frgUserHome_rcvAdidas.setHasFixedSize(true)
                frgUserHome_rcvAdidas.layoutManager = linearLayoutManager
                frgUserHome_rcvAdidas.adapter = userProductAdapter
            }
            NIKE -> {
                listProductNike.clear()
                listProductNike.addAll(listProduct)
                frgUserHome_rcvNike.setHasFixedSize(true)
                frgUserHome_rcvNike.layoutManager = linearLayoutManager
                frgUserHome_rcvNike.adapter = userProductAdapter
            }
            CONVERSE -> {
                listProductConverse.clear()
                listProductConverse.addAll(listProduct)
                frgUserHome_rcvConverse.setHasFixedSize(true)
                frgUserHome_rcvConverse.layoutManager = linearLayoutManager
                frgUserHome_rcvConverse.adapter = userProductAdapter
            }
            PUMA -> {
                listProductPuma.clear()
                listProductPuma.addAll(listProduct)
                frgUserHome_rcvPuma.setHasFixedSize(true)
                frgUserHome_rcvPuma.layoutManager = linearLayoutManager
                frgUserHome_rcvPuma.adapter = userProductAdapter
            }
            JORDAN -> {
                listProductJordan.clear()
                listProductJordan.addAll(listProduct)
                frgUserHome_rcvJordan.setHasFixedSize(true)
                frgUserHome_rcvJordan.layoutManager = linearLayoutManager
                frgUserHome_rcvJordan.adapter = userProductAdapter
            }
        }
        dismissProgress()
    }

}