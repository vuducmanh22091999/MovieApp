package com.example.movieapp.ui.detail.product

import android.view.View
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.product.CartProductModel
import com.example.movieapp.data.model.product.ProductModel
import com.example.movieapp.ui.main.UserActivity
import com.example.movieapp.utils.DETAIL_PRODUCT
import com.example.movieapp.utils.USER_CART
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_detail_product.*

class DetailProductFragment : BaseFragment(), View.OnClickListener {
    private var detailProductModel = ProductModel()
    private lateinit var database: DatabaseReference
    private lateinit var storage: StorageReference

    override fun getLayoutID(): Int {
        return R.layout.fragment_detail_product
    }

    override fun doViewCreated() {
        database = FirebaseDatabase.getInstance().reference.child(USER_CART)
        storage = FirebaseStorage.getInstance().getReference("Images")
        initListener()
        handleBottom()
        getInfoFromUserHome()
    }

    private fun initListener() {
        frgDetailProduct_tvAddToCart.setOnClickListener(this)
    }

    private fun handleBottom() {
        (activity as UserActivity).hideBottom()
    }

    private fun getInfoFromUserHome() {
        detailProductModel = arguments?.getSerializable(DETAIL_PRODUCT) as ProductModel
        Picasso.get().load(detailProductModel.urlAvatar).into(frgDetailProduct_imgAvatar)
        frgDetailProduct_tvTitleNameProduct.text = detailProductModel.name
//        frgDetailProduct_tvAmount.text = detailProductModel.amount.toString()
        frgDetailProduct_tvPrice.text = detailProductModel.price.toString()
    }

    private fun addToCart() {
        val key = System.currentTimeMillis().toInt()
        val productModel = CartProductModel(
            key,
            ProductModel(
                type = detailProductModel.type,
                id = detailProductModel.id,
                urlAvatar = detailProductModel.urlAvatar,
                name = detailProductModel.name,
                amount = 1,
                price = detailProductModel.price
            )
        )
        database.child(key.toString()).setValue(productModel)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.frgDetailProduct_tvAddToCart -> addToCart()
        }
    }
}