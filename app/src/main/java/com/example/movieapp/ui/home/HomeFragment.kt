package com.example.movieapp.ui.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.product.ProductModel
import com.example.movieapp.ui.add.AddScreenFragment
import com.example.movieapp.ui.home.adapter.ListProductAdapter
import com.example.movieapp.utils.*
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.ArrayList

class HomeFragment : BaseFragment(), View.OnClickListener {
    private lateinit var database: DatabaseReference
    private var listProduct = ArrayList<ProductModel>()
    private lateinit var listProductAdapter: ListProductAdapter

    override fun getLayoutID(): Int {
        return R.layout.fragment_home
    }

    override fun doViewCreated() {
        database = FirebaseDatabase.getInstance().reference.child(PRODUCT)
        initListener()
        setDataForList()
    }

    private fun setDataForList() {
        productAdidas()
        productNike()
        productConverse()
        productPuma()
        productJordan()
    }

    private fun productAdidas() {
        database.child(ADIDAS).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (value in snapshot.children) {
                        val string = value.getValue(ProductModel::class.java)
                        if (string != null) {
//                            listProduct.clear()
                            listProduct.add(string)
                        }

                        listProductAdapter = ListProductAdapter(listProduct.toList()) { index, _ ->
                            Toast.makeText(context, listProduct[index].name, Toast.LENGTH_SHORT).show()
                        }

                        val linearLayoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        frgHome_rcvAdidas.setHasFixedSize(true)
                        frgHome_rcvAdidas.layoutManager = linearLayoutManager
                        frgHome_rcvAdidas.adapter = listProductAdapter
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun productNike() {
        database.child(NIKE).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (value in snapshot.children) {
                        val string = value.getValue(ProductModel::class.java)
                        if (string != null) {
//                            listProduct.clear()
                            listProduct.add(string)
                        }

                        listProductAdapter = ListProductAdapter(listProduct.toList()) { index, _ ->
                            Toast.makeText(context, listProduct[index].name, Toast.LENGTH_SHORT).show()
                        }

                        val linearLayoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        frgHome_rcvNike.setHasFixedSize(true)
                        frgHome_rcvNike.layoutManager = linearLayoutManager
                        frgHome_rcvNike.adapter = listProductAdapter
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun productConverse() {
        database.child(CONVERSE).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (value in snapshot.children) {
                        val string = value.getValue(ProductModel::class.java)
                        if (string != null) {
//                            listProduct.clear()
                            listProduct.add(string)
                        }

                        listProductAdapter = ListProductAdapter(listProduct.toList()) { index, _ ->
                            Toast.makeText(context, listProduct[index].name, Toast.LENGTH_SHORT).show()
                        }

                        val linearLayoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        frgHome_rcvConverse.setHasFixedSize(true)
                        frgHome_rcvConverse.layoutManager = linearLayoutManager
                        frgHome_rcvConverse.adapter = listProductAdapter
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun productPuma() {
        database.child(PUMA).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (value in snapshot.children) {
                        val string = value.getValue(ProductModel::class.java)
                        if (string != null) {
//                            listProduct.clear()
                            listProduct.add(string)
                        }

                        listProductAdapter = ListProductAdapter(listProduct.toList()) { index, _ ->
                            Toast.makeText(context, listProduct[index].name, Toast.LENGTH_SHORT).show()
                        }

                        val linearLayoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        frgHome_rcvPuma.setHasFixedSize(true)
                        frgHome_rcvPuma.layoutManager = linearLayoutManager
                        frgHome_rcvPuma.adapter = listProductAdapter
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun productJordan() {
        database.child(JORDAN).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (value in snapshot.children) {
                        val string = value.getValue(ProductModel::class.java)
                        if (string != null) {
//                            listProduct.clear()
                            listProduct.add(string)
                        }

                        listProductAdapter = ListProductAdapter(listProduct.toList()) { index, _ ->
                            Toast.makeText(context, listProduct[index].name, Toast.LENGTH_SHORT).show()
                        }

                        val linearLayoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        frgHome_rcvJordan.setHasFixedSize(true)
                        frgHome_rcvJordan.layoutManager = linearLayoutManager
                        frgHome_rcvJordan.adapter = listProductAdapter
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun initListener() {
        frgHome_imgAddAdidas.setOnClickListener(this)
        frgHome_imgAddNike.setOnClickListener(this)
        frgHome_imgAddConverse.setOnClickListener(this)
        frgHome_imgAddPuma.setOnClickListener(this)
        frgHome_imgAddJordan.setOnClickListener(this)
    }

    private fun addProductAdidas() {
        val addScreenFragment = AddScreenFragment()
        val bundle = Bundle()
        bundle.putString(NAME_PRODUCT, ADIDAS)
        addScreenFragment.arguments = bundle
        addFragment(addScreenFragment, R.id.frameLayout)
    }

    private fun addProductNike() {
        val addScreenFragment = AddScreenFragment()
        val bundle = Bundle()
        bundle.putString(NAME_PRODUCT, NIKE)
        addScreenFragment.arguments = bundle
        addFragment(addScreenFragment, R.id.frameLayout)
    }

    private fun addProductConverse() {
        val addScreenFragment = AddScreenFragment()
        val bundle = Bundle()
        bundle.putString(NAME_PRODUCT, CONVERSE)
        addScreenFragment.arguments = bundle
        addFragment(addScreenFragment, R.id.frameLayout)
    }

    private fun addProductPuma() {
        val addScreenFragment = AddScreenFragment()
        val bundle = Bundle()
        bundle.putString(NAME_PRODUCT, PUMA)
        addScreenFragment.arguments = bundle
        addFragment(addScreenFragment, R.id.frameLayout)
    }

    private fun addProductJordan() {
        val addScreenFragment = AddScreenFragment()
        val bundle = Bundle()
        bundle.putString(NAME_PRODUCT, JORDAN)
        addScreenFragment.arguments = bundle
        addFragment(addScreenFragment, R.id.frameLayout)
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.frgHome_imgAddAdidas -> addProductAdidas()
            R.id.frgHome_imgAddNike -> addProductNike()
            R.id.frgHome_imgAddConverse -> addProductConverse()
            R.id.frgHome_imgAddPuma -> addProductPuma()
            R.id.frgHome_imgAddJordan -> addProductJordan()
        }
    }
}