package com.example.movieapp.ui.cart

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.account.AccountModel
import com.example.movieapp.data.model.product.CartProductModel
import com.example.movieapp.data.model.product.ProductModel
import com.example.movieapp.ui.cart.adapter.AdminCartAdapter
import com.example.movieapp.utils.*
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_admin_cart.*
import kotlinx.android.synthetic.main.fragment_edit_profile_user.*
import java.util.ArrayList

class AdminCartFragment : BaseFragment() {
    private lateinit var databaseUser: DatabaseReference
    private lateinit var databaseOrderSuccess: DatabaseReference
    private lateinit var adminCartAdapter: AdminCartAdapter
    private val listAccount = arrayListOf<AccountModel>()
    private val listIdUser = arrayListOf<String>()
    val listProductOrderSuccess = ArrayList<CartProductModel>()

    override fun getLayoutID(): Int {
        return R.layout.fragment_admin_cart
    }

    override fun doViewCreated() {
        databaseUser = FirebaseDatabase.getInstance().reference.child(ACCOUNT).child(USER)
        databaseOrderSuccess = FirebaseDatabase.getInstance().reference.child(ORDER_SUCCESS)
        getDataUser()
    }

    private fun getDataUser() {
        databaseUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (value in snapshot.children) {
                        val accountModel = value.getValue(AccountModel::class.java)
                        accountModel?.let { listAccount.add(it) }
                        listIdUser.add(value.key.toString())
                    }
                    getDataOrder()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun getDataOrder() {
        listIdUser.forEach { idUser ->
            listAccount.forEach { account ->
                databaseOrderSuccess.child(idUser).addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (value in snapshot.children) {
                                val cartProductModel = value.getValue(CartProductModel::class.java)
                                if (cartProductModel != null) {
                                    if (cartProductModel.isOrderSuccess) {
                                        listProductOrderSuccess.add(cartProductModel)
                                    }
                                    frgAdminCart_tvNotification.visibility = View.GONE
                                    adminCartAdapter =
                                        AdminCartAdapter(listProductOrderSuccess.toList(), account.userName)
                                    val linearLayoutManager =
                                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                                    frgAdminCart_rcvCart.setHasFixedSize(true)
                                    frgAdminCart_rcvCart.layoutManager = linearLayoutManager
                                    frgAdminCart_rcvCart.adapter = adminCartAdapter
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
            }
        }
    }
}