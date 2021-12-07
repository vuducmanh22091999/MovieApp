package com.example.movieapp.ui.revenue

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.product.CartProductModel
import com.example.movieapp.ui.main.MainActivity
import com.example.movieapp.ui.revenue.adapter.AdminRevenueAdapter
import com.example.movieapp.utils.ACCOUNT
import com.example.movieapp.utils.ORDER_COMPLETED
import com.example.movieapp.utils.USER
import com.example.movieapp.utils.formatStringLong
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_admin_revenue.*
import java.util.ArrayList

class AdminRevenueFragment : BaseFragment(), View.OnClickListener {
    private lateinit var databaseOrderCompleted: DatabaseReference
    private lateinit var databaseUser: DatabaseReference
    private val listIdUser = arrayListOf<String>()
    private lateinit var adminRevenueAdapter: AdminRevenueAdapter
    val listOrderCompleted = ArrayList<CartProductModel>()
    private var totalRevenue = 0L
    val listMonth = arrayListOf<String>()

    override fun getLayoutID(): Int {
        return R.layout.fragment_admin_revenue
    }

    override fun doViewCreated() {
        databaseOrderCompleted = FirebaseDatabase.getInstance().reference.child(ORDER_COMPLETED)
        databaseUser = FirebaseDatabase.getInstance().reference.child(ACCOUNT).child(USER)

        initListener()
        getDataUser()
        getCompletedDate()
    }

    private fun initListener() {
        frgAdminRevenue_imgBack.setOnClickListener(this)
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

    private fun initAdapter() {
        adminRevenueAdapter = AdminRevenueAdapter()
        val linearLayoutManager =
            LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
        frgAdminRevenue_rcvListCart.setHasFixedSize(true)
        frgAdminRevenue_rcvListCart.layoutManager = linearLayoutManager
        frgAdminRevenue_rcvListCart.adapter = adminRevenueAdapter
    }

    private fun showHideCart() {
        if (listOrderCompleted.isEmpty()) {
            frgAdminRevenue_tvNotification.setText(R.string.title_notification)
        } else {
            frgAdminRevenue_tvNotification.setText(R.string.title_blank)
        }
    }

    private fun getDataOrderCompleted() {
        listOrderCompleted.clear()
        listIdUser.forEach { idUser ->
            databaseOrderCompleted.child(idUser).addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    view?.apply {
                        if (snapshot.exists()) {
                            for (value in snapshot.children) {
                                val cartProductModel = value.getValue(CartProductModel::class.java)
                                if (cartProductModel != null) {
                                    if (cartProductModel.isOrderCompleted) {
                                        totalRevenue += cartProductModel.totalPrice
                                        listOrderCompleted.add(cartProductModel)
                                    }
                                }
                            }
                            frgAdminRevenue_tvValueTotal.text = "${formatStringLong(totalRevenue)}$"
                            showHideCart()
                            initAdapter()
                            adminRevenueAdapter.submitList(listOrderCompleted)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

    private fun getCompletedDate() {
//        listCompletedDate.clear()
//        listOrderCompleted.forEach { cartProductModel ->
//            val month = cartProductModel.orderDateCompleted.split("/")[1]
//            val year = cartProductModel.orderDateCompleted.split("/")[2]
//            cartProductModel.orderDateCompleted.split("/")
//            if (!listCompletedDate.contains(cartProductModel.orderDateCompleted))
//              listCompletedDate.add(cartProductModel.orderDateCompleted)
////            if (!listCompletedDate.contains("$month/$year"))
////                listCompletedDate.add("$month/$year")
//        }
        listMonth.add("Pick month...")
        for (month in 1..12)
            listMonth.add("${month}/2021")
        val arrayAdapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_style,
            listMonth
        )
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        frgAdminRevenue_spinner.adapter = arrayAdapter
        pickTime()
    }

    private fun pickTime() {
        frgAdminRevenue_spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                @SuppressLint("SetTextI18n")
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val listTemp = ArrayList<CartProductModel>()
                    totalRevenue = 0L
                    listOrderCompleted.forEach { cartProductModel ->
                        val month = cartProductModel.orderDateCompleted.split("/")[1]
                        val year = cartProductModel.orderDateCompleted.split("/")[2]
                        if (listMonth[position] == "$month/$year") {
                            totalRevenue += cartProductModel.totalPrice
                            listTemp.add(cartProductModel)
                        }
                    }
                    frgAdminRevenue_tvValueTotal.text = "${formatStringLong(totalRevenue)}$"
                    if (listTemp.isEmpty())
                        frgAdminRevenue_tvNotification.setText(R.string.title_no_result)
                    else
                        frgAdminRevenue_tvNotification.setText(R.string.title_blank)
                    initAdapter()
                    adminRevenueAdapter.submitList(listTemp)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.frgAdminRevenue_imgBack -> (activity as MainActivity).onBackPressed()
        }
    }
}