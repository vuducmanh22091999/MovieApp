package com.example.movieapp.ui.confirm_order

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.account.AccountModel
import com.example.movieapp.data.model.product.CartProductModel
import com.example.movieapp.ui.confirm_order.adapter.ConfirmOderAdapter
import com.example.movieapp.ui.main.UserActivity
import com.example.movieapp.utils.ACCOUNT
import com.example.movieapp.utils.USER
import com.example.movieapp.utils.USER_CART
import com.example.movieapp.utils.formatStringLong
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_confirm_order.*

class ConfirmOrderFragment: BaseFragment(), View.OnClickListener {
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseUser: DatabaseReference
    private lateinit var databaseCart: DatabaseReference
    private var idUser = ""
    private var total = 0L
    private var listCartProduct = ArrayList<CartProductModel>()
    private lateinit var confirmOderAdapter: ConfirmOderAdapter
    private val accountModel = AccountModel()
    private lateinit var dialog: Dialog
    private val AUTOCOMPLETE_REQUEST_CODE = 1

    override fun getLayoutID(): Int {
        return R.layout.fragment_confirm_order
    }

    override fun doViewCreated() {
        auth = FirebaseAuth.getInstance()
        databaseUser = FirebaseDatabase.getInstance().reference.child(ACCOUNT).child(USER)
        databaseCart = FirebaseDatabase.getInstance().reference.child(USER_CART)
        idUser = auth.currentUser?.uid.toString()
        handleBottom()
        initListener()
        getInfoUserFromFirebase()
        listCartProduct()
    }

    private fun initListener() {
        frgConfirmOrder_imgAddAddress.setOnClickListener(this)
    }

    private fun handleBottom() {
        (activity as UserActivity).hideBottom()
    }

    private fun getInfoUserFromFirebase() {
        auth.currentUser?.uid?.let {
            databaseUser.child(it).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists() && snapshot.childrenCount > 0) {
                        frgConfirmOrder_tvUserNameOrder.text = snapshot.child("userName").value.toString()
                        frgConfirmOrder_tvUserPhoneOrder.text = snapshot.child("phoneNumber").value.toString()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

    private fun listCartProduct() {
        databaseCart.child(idUser).addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    total = 0
                    for (value in snapshot.children) {
                        val cartProductModel = value.getValue(CartProductModel::class.java)
                        if (cartProductModel != null) {
                            listCartProduct.add(cartProductModel)
                            total += cartProductModel.totalPrice
                        }
                        frgConfirmOrder_tvValueTotal.text = "${formatStringLong(total)}$"

                        confirmOderAdapter = ConfirmOderAdapter(listCartProduct.toList())
                        val linearLayoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                        frgConfirmOrder_rcvListOrder.setHasFixedSize(true)
                        frgConfirmOrder_rcvListOrder.layoutManager = linearLayoutManager
                        frgConfirmOrder_rcvListOrder.adapter = confirmOderAdapter
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun addAddress() {
        Places.initialize(requireContext(), getString(R.string.key_google_map))
        val fields = listOf(Place.Field.ID, Place.Field.NAME)

        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(requireContext())
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        Log.i("testLocation", "Place: ${place.name}, ${place.id}")
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i("testLocation", status.statusMessage.toString())
                    }
                }
                Activity.RESULT_CANCELED -> {
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.frgConfirmOrder_imgAddAddress -> addAddress()
        }
    }
}