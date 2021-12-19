package com.example.movieapp.ui.cart

import android.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.product.ProductModel
import com.example.movieapp.data.model.product.StatusCartModel
import com.example.movieapp.ui.cart.adapter.AdminViewPagerAdapter
import com.example.movieapp.ui.cart.adapter.StatusCartAdapter
import com.example.movieapp.utils.*
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_admin_cart.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AdminCartFragment : BaseFragment() {
    private lateinit var adminViewPagerAdapter: AdminViewPagerAdapter
    private lateinit var databaseUser: DatabaseReference
    private val listIdUser = arrayListOf<String>()
    private val listStatusSpinner = arrayListOf<String>()
    private lateinit var databaseOrder: DatabaseReference
    private lateinit var statusCartAdapter: StatusCartAdapter
    private var listOrder = ArrayList<StatusCartModel>()
    private var currentDate = ""
    private lateinit var databaseProduct: DatabaseReference
    private val listProduct = ArrayList<ProductModel>()
    var listStatus = arrayOf(
        "Order confirm", //1
        "Order is delivering", //2
        "Order completed", //3
        "Order is canceled" //4
    )
    var listOrderCopy = arrayListOf<StatusCartModel>()

    override fun getLayoutID(): Int {
        return R.layout.fragment_admin_cart
    }

    override fun doViewCreated() {
//        adminViewPagerAdapter = AdminViewPagerAdapter(childFragmentManager,
//            FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
//        frgAdminCart_viewPager.adapter = adminViewPagerAdapter
//        frgAdminCart_tabLayout.setupWithViewPager(frgAdminCart_viewPager)
        databaseUser = FirebaseDatabase.getInstance().reference.child(ACCOUNT).child(USER)
        databaseOrder = FirebaseDatabase.getInstance().reference.child(ORDER)
        databaseProduct = FirebaseDatabase.getInstance().reference.child(PRODUCT)
        getDataUser()
        getCurrentDate()
        getDataProduct()
    }

    private fun initAdapterStatus() {
        statusCartAdapter = StatusCartAdapter { index, _ ->
            openDialogPickOrderStatus(listOrder[index], index)
        }
        val linearLayoutManager =
            LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
        frgAdminCart_rcvCart.setHasFixedSize(true)
        frgAdminCart_rcvCart.layoutManager = linearLayoutManager
        frgAdminCart_rcvCart.adapter = statusCartAdapter
    }

    private fun getCurrentDate() {
        val currentTime = Calendar.getInstance().time
        val sdf = SimpleDateFormat("dd/M/yyyy")
        currentDate = sdf.format(currentTime)
        Log.d("testDateTime ", currentDate)
    }

    private fun getDataUser() {
        databaseUser.get().addOnSuccessListener {
            if (it.exists()) {
                for (value in it.children) {
                    listIdUser.add(value.key.toString())
                }
                getDatabaseOrder()
                setDataForSpinner()
            }
        }
    }

    private fun getDataProduct() {
        dataProduct(ADIDAS)
        dataProduct(NIKE)
        dataProduct(CONVERSE)
        dataProduct(PUMA)
        dataProduct(JORDAN)
    }

    private fun dataProduct(type: String) {
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

    private fun checkIdOrder(statusCartModel: StatusCartModel) {
        val model = listOrder.firstOrNull {
            it.idOrder == statusCartModel.idOrder
        }
        if (model != null)
//            model.status = statusCartModel.status
            listOrder.remove(statusCartModel)
        else
            listOrder.add(statusCartModel)
    }

    private fun getDatabaseOrder() {
        listOrder.clear()
        listIdUser.forEach {
            databaseOrder.child(it).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    view?.apply {
                        if (snapshot.exists()) {
                            snapshot.children.forEach {
                                it.children.forEach { value ->
                                    val statusCartModel =
                                        value.getValue(StatusCartModel::class.java)
                                    if (statusCartModel != null) {
                                        checkIdOrder(statusCartModel)
                                    }
                                }
                            }
                            listOrderCopy = listOrder.toMutableList() as ArrayList<StatusCartModel>
                            initAdapterStatus()
                            statusCartAdapter.submitList(listOrder)
                        }
                        if (listOrder.isEmpty())
                            frgAdminCart_tvNotification.setText(R.string.title_no_result)
                        else
                            frgAdminCart_tvNotification.setText(R.string.title_blank)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

    private fun addStatus(statusCartModel: StatusCartModel): Array<String> {
        val list = arrayListOf<String>()
        when (statusCartModel.status) {
            0 -> {
                list.add(listStatus[0])
                list.add(listStatus[3])
            }
            1 -> {
                list.add(listStatus[1])
                list.add(listStatus[3])
            }
            2 -> {
                list.add(listStatus[2])
                list.add(listStatus[3])
            }
        }
        return list.toTypedArray()
    }

    private fun openDialogPickOrderStatus(
        statusCartModel: StatusCartModel,
        index: Int
    ) {
        var valueSelected = ""
        val listTemp = addStatus(statusCartModel)
        if (listTemp.isNotEmpty()) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(R.string.title_pick_order_status)
            builder.setSingleChoiceItems(
                listTemp, -1
            ) { _, which ->
                valueSelected = listTemp[which]
            }

            builder.setPositiveButton("Ok") { dialog, _ ->
                when (valueSelected) {
                    listStatus[0] -> {
                        statusCartModel.status = 1
                        statusCartModel.valueStatus = getString(R.string.title_order_confirm)
                        updateStatus(statusCartModel)
                    }
                    listStatus[1] -> {
                        statusCartModel.status = 2
                        statusCartModel.valueStatus = getString(R.string.title_order_delivered)
                        updateStatus(statusCartModel)
                    }
                    listStatus[2] -> {
                        statusCartModel.status = 3
                        statusCartModel.valueStatus = getString(R.string.title_order_completed)
                        statusCartModel.completedDate = currentDate
                        updateStatus(statusCartModel)
                        updateProduct()
                    }
                    listStatus[3] -> {
                        statusCartModel.status = 4
                        statusCartModel.valueStatus = getString(R.string.title_order_cancel)
                        updateStatus(statusCartModel)
                    }
                }
                if (listOrder.size != listOrderCopy.size)
                    deleteItem(index)
                dialog.dismiss()
            }

            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }
    }

    private fun updateStatus(statusCartModel: StatusCartModel) {
        databaseOrder.child(statusCartModel.listProduct[0].idUser.toString())
            .child(statusCartModel.idOrder.toString()).child("0").setValue(statusCartModel)
    }

    private fun updateProduct() {
        listOrder.forEach { statusCartModel ->
            listProduct.firstOrNull {
                it.id == statusCartModel.listProduct[0].productModel?.id
            }?.let { productModel ->
                productModel.listSize.firstOrNull { sizeProductModel ->
                    sizeProductModel.size == statusCartModel.listProduct[0].productModel!!.listSize[0].size
                }?.let {
                    it.amountSize -= statusCartModel.listProduct[0].amountUserOrder
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

    private fun setDataForSpinner() {
        listStatusSpinner.add("All order cart...")
        listStatusSpinner.add("New order")
        listStatusSpinner.add("Order confirm")
        listStatusSpinner.add("Order is delivering")
        listStatusSpinner.add("Order completed")
        listStatusSpinner.add("Order is canceled")
        val arrayAdapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_style,
            listStatusSpinner
        )
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        frgAdminCart_spinner.adapter = arrayAdapter
        filterStatus()
    }

    private fun filterStatus() {
        frgAdminCart_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    frgAdminCart_tvNotification.setText(R.string.title_blank)
                    getDatabaseOrder()
                } else {
                    val listTemp = arrayListOf<StatusCartModel>()
                    val valueStatusPick = listStatusSpinner[position]
                    listOrderCopy.forEach { statusCartModel ->
                        if (statusCartModel.valueStatus.contains(valueStatusPick)) {
                            listTemp.add(statusCartModel)
                        }
                    }
                    if (listTemp.isEmpty())
                        frgAdminCart_tvNotification.setText(R.string.title_no_result)
                    else
                        frgAdminCart_tvNotification.setText(R.string.title_blank)
                    listOrder = listTemp
                    initAdapterStatus()
                    statusCartAdapter.submitList(listOrder)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

    private fun deleteItem(index: Int) {
        val list = listOrder.toMutableList()
        list.removeAt(index)
        initAdapterStatus()
        statusCartAdapter.notifyItemRemoved(index)
        statusCartAdapter.submitList(list)
        if (list.isEmpty())
            frgAdminCart_tvNotification.setText(R.string.title_no_result)
        else
            frgAdminCart_tvNotification.setText(R.string.title_blank)

    }

}