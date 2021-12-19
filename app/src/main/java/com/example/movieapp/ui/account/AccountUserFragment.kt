package com.example.movieapp.ui.account

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.local.AppPreferences
import com.example.movieapp.ui.edit.EditProfileUserFragment
import com.example.movieapp.ui.login.LoginActivity
import com.example.movieapp.ui.order_history.OrderHistoryFragment
import com.example.movieapp.utils.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_user_account.*

class AccountUserFragment : BaseFragment(), View.OnClickListener {
    private lateinit var appPreferences: AppPreferences
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var urlAvatar: Uri

    override fun getLayoutID(): Int {
        return R.layout.fragment_user_account
    }

    override fun doViewCreated() {
        appPreferences = context?.let { AppPreferences(it) }!!
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child(ACCOUNT).child(USER)
        initListener()
        getInfoUserFromFirebase()
        hideLogout()
    }

    private fun initListener() {
        frgUserAccount_tvLogout.setOnClickListener(this)
        frgUserAccount_imgEdit.setOnClickListener(this)
        frgUserAccount_tvOrderHistory.setOnClickListener(this)
        frgUserAccount_tvLogin.setOnClickListener(this)
    }

    private fun moveToOrderHistory() {
        val orderHistoryFragment = OrderHistoryFragment()
        addFragment(
            orderHistoryFragment,
            R.id.actUser_frameLayout,
            OrderHistoryFragment::class.java.simpleName
        )
    }

    private fun hideLogout() {
        if (auth.currentUser?.uid?.isNotEmpty() == true) {
            frgUserAccount_tvLogout.visibility = View.VISIBLE
            frgUserAccount_tvLogin.visibility = View.GONE
        } else {
            frgUserAccount_tvLogout.visibility = View.GONE
            frgUserAccount_tvLogin.visibility = View.VISIBLE
        }
    }

    private fun getInfoUserFromFirebase() {
        auth.currentUser?.uid?.let {
            databaseReference.child(it).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    urlAvatar = Uri.parse(snapshot.child("urlAvatar").value.toString())
                    if (snapshot.exists() && snapshot.childrenCount > 0) {
                        frgUserAccount_tvEmailUser.text = auth.currentUser!!.email.toString()
                        if (snapshot.child("urlAvatar").value.toString() == "null")
                            frgUserAccount_imgAvatar.setImageResource(R.drawable.ic_account)
                        else
                            Picasso.get().load(urlAvatar).into(frgUserAccount_imgAvatar)
                        frgUserAccount_tvNameUser.text = snapshot.child("userName").value.toString()
                        frgUserAccount_tvPhoneUser.text =
                            snapshot.child("phoneNumber").value.toString()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

    private fun logOut() {
        val intent = Intent(activity, LoginActivity::class.java)
        intent.putExtra("hideRegister", false)
        auth.signOut()
        appPreferences.setIsLogin(false)
        appPreferences.setLoginEmail("")
        appPreferences.setLoginUserName("")
        appPreferences.setLoginAvatar("")
        startActivity(intent)
    }

    private fun logIn() {
        val intent = Intent(activity, LoginActivity::class.java)
        intent.putExtra("hideRegister", false)
        startActivity(intent)
    }

    private fun moveEditScreen() {
        val editProfileFragment = EditProfileUserFragment()
        val bundle = Bundle()
        bundle.putString(USER_NAME, frgUserAccount_tvNameUser.text.toString())
        bundle.putString(TYPE_ACCOUNT, USER)
        bundle.putString(PHONE_NUMBER, frgUserAccount_tvPhoneUser.text.toString())
        bundle.putString(URL_AVATAR, Uri.parse(urlAvatar.toString()).toString())
        editProfileFragment.arguments = bundle
        addFragment(
            editProfileFragment,
            R.id.actUser_frameLayout,
            EditProfileUserFragment::class.java.simpleName
        )
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.frgUserAccount_tvLogout -> logOut()
            R.id.frgUserAccount_imgEdit -> moveEditScreen()
            R.id.frgUserAccount_tvOrderHistory -> moveToOrderHistory()
            R.id.frgUserAccount_tvLogin -> logIn()
        }
    }
}