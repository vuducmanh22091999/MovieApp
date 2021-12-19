package com.example.movieapp.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.product.ProductModel
import com.example.movieapp.ui.detail.product.DetailProductFragment
import com.example.movieapp.ui.home.adapter.UserProductAdapter
import com.example.movieapp.ui.main.UserActivity
import com.example.movieapp.utils.*
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_user_home.*
import java.util.ArrayList

class UserHomeFragment : BaseFragment(), View.OnClickListener {
    private lateinit var database: DatabaseReference
    private var listProductAdidas = ArrayList<ProductModel>()
    private var listProductNike = ArrayList<ProductModel>()
    private var listProductConverse = ArrayList<ProductModel>()
    private var listProductPuma = ArrayList<ProductModel>()
    private var listProductJordan = ArrayList<ProductModel>()
    private lateinit var userProductAdapter: UserProductAdapter
    private lateinit var storage: StorageReference
    private lateinit var progress: ProgressDialog
    var listProductSearch = ArrayList<ProductModel>()
    var listName: ArrayList<String> = ArrayList()

    override fun getLayoutID(): Int {
        return R.layout.fragment_user_home
    }

    override fun doViewCreated() {
        database = FirebaseDatabase.getInstance().reference.child(PRODUCT)
        storage = FirebaseStorage.getInstance().getReference("Images")
        progress = ProgressDialog(context)
        initListener()
        hideKeyboardWhenClickOutside()
        setDataForList()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun hideKeyboardWhenClickOutside() {
        repeat(2) {
            frgUserHome_layout.setOnTouchListener { v, event ->
                val imm =
                    context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view?.windowToken, 0)
                true
            }
        }
    }

    private fun initListener() {
        frgUserHome_imgSearch.setOnClickListener(this)
    }

    private fun showProgress() {
        progress.setMessage("Waiting get data...")
        progress.setCancelable(false)
        progress.show()
    }

    private fun autoSearch() {
        listName.clear()
        listProductSearch.forEach {
            listName.add(it.name!!)
        }

        val autoSearchAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            listName,
        )
        frgUserHome_autoNameProduct.setAdapter(autoSearchAdapter)
    }

    private fun moveToDetailProduct() {
        val name = frgUserHome_autoNameProduct.text.toString()
        var productModel = ProductModel()
        listProductSearch.forEach {
            if (it.name == name)
                productModel = it
        }
        if (productModel.price != 0L) {
            val detailProductFragment = DetailProductFragment()
            val bundle = Bundle()
            bundle.putSerializable(DETAIL_PRODUCT, productModel)
            detailProductFragment.arguments = bundle
            addFragment(
                detailProductFragment,
                R.id.actUser_frameLayout,
                DetailProductFragment::class.java.simpleName
            )
            (activity as UserActivity).hideKeyboard()
        } else {
            Toast.makeText(context, "Not found product!!!", Toast.LENGTH_SHORT).show()
        }
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
                        listProductSearch.add(productModel!!)
                        autoSearch()
                        userProductAdapter =
                            UserProductAdapter(listProduct.toList()) { index, _ ->
                                val detailProductFragment = DetailProductFragment()
                                val bundle = Bundle()
                                bundle.putSerializable(DETAIL_PRODUCT, listProduct[index])
                                detailProductFragment.arguments = bundle
                                addFragment(
                                    detailProductFragment,
                                    R.id.actUser_frameLayout,
                                    DetailProductFragment::class.java.simpleName
                                )
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
        listProduct: ArrayList<ProductModel>,
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
                frgUserHome_tvAdidas.visibility = View.VISIBLE
            }
            NIKE -> {
                listProductNike.clear()
                listProductNike.addAll(listProduct)
                frgUserHome_rcvNike.setHasFixedSize(true)
                frgUserHome_rcvNike.layoutManager = linearLayoutManager
                frgUserHome_rcvNike.adapter = userProductAdapter
                frgUserHome_tvNike.visibility = View.VISIBLE
            }
            CONVERSE -> {
                listProductConverse.clear()
                listProductConverse.addAll(listProduct)
                frgUserHome_rcvConverse.setHasFixedSize(true)
                frgUserHome_rcvConverse.layoutManager = linearLayoutManager
                frgUserHome_rcvConverse.adapter = userProductAdapter
                frgUserHome_tvConverse.visibility = View.VISIBLE
            }
            PUMA -> {
                listProductPuma.clear()
                listProductPuma.addAll(listProduct)
                frgUserHome_rcvPuma.setHasFixedSize(true)
                frgUserHome_rcvPuma.layoutManager = linearLayoutManager
                frgUserHome_rcvPuma.adapter = userProductAdapter
                frgUserHome_tvPuma.visibility = View.VISIBLE
            }
            JORDAN -> {
                listProductJordan.clear()
                listProductJordan.addAll(listProduct)
                frgUserHome_rcvJordan.setHasFixedSize(true)
                frgUserHome_rcvJordan.layoutManager = linearLayoutManager
                frgUserHome_rcvJordan.adapter = userProductAdapter
                frgUserHome_tvJordan.visibility = View.VISIBLE
            }
        }
        dismissProgress()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.frgUserHome_imgSearch -> moveToDetailProduct()
        }
    }
}