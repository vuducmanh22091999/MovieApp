package com.example.movieapp.ui.cart

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.product.CartProductModel
import com.example.movieapp.ui.cart.adapter.UserCartAdapter
import com.example.movieapp.utils.USER_CART
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_user_cart.*

class UserCartFragment: BaseFragment() {
    private lateinit var database: DatabaseReference
    private lateinit var userCartAdapter: UserCartAdapter

    override fun getLayoutID(): Int {
        return R.layout.fragment_user_cart
    }

    override fun doViewCreated() {
        database = FirebaseDatabase.getInstance().reference.child(USER_CART)
        listProduct()
    }

    private fun listProduct() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val listProduct = ArrayList<CartProductModel>()
                    for (value in snapshot.children) {
                        val cartProductModel = value.getValue(CartProductModel::class.java)
                        if (cartProductModel != null) {
                            listProduct.add(cartProductModel)
                        }

                        userCartAdapter = UserCartAdapter(listProduct.toList())
                        val linearLayoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                        frgUserCart_rcvListCart.setHasFixedSize(true)
                        frgUserCart_rcvListCart.layoutManager = linearLayoutManager
                        frgUserCart_rcvListCart.adapter = userCartAdapter
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}