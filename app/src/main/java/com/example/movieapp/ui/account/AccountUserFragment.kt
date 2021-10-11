package com.example.movieapp.ui.account

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.local.AppPreferences
import com.example.movieapp.ui.edit.EditProfileFragment
import com.example.movieapp.ui.login.LoginActivity
import com.example.movieapp.utils.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_account.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_user_account.*

class AccountUserFragment: BaseFragment(), View.OnClickListener {
    private lateinit var appPreferences: AppPreferences
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var urlAvatar : Uri

    override fun getLayoutID(): Int {
        return R.layout.fragment_user_account
    }

    override fun doViewCreated() {
        appPreferences = context?.let { AppPreferences(it) }!!
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child(ACCOUNT).child(USER)
        initListener()
        getInfoUserFromFirebase()
    }

    private fun initListener() {
        frgUserAccount_tvLogout.setOnClickListener(this)
        frgUserAccount_imgEdit.setOnClickListener(this)
    }

    private fun getInfoUserFromFirebase() {
        auth.currentUser?.uid?.let {
            databaseReference.child(it).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    urlAvatar = Uri.parse(snapshot.child("urlAvatar").value.toString())
                    if (snapshot.exists() && snapshot.childrenCount > 0) {
                        frgUserAccount_tvEmailUser.text = auth.currentUser!!.email.toString()
                        if (snapshot.child("urlAvatar").value.toString() == "")
                            frgUserAccount_imgAvatar.setImageResource(R.drawable.ic_account)
                        else
                            Picasso.get().load(urlAvatar).into(frgUserAccount_imgAvatar)
                        frgUserAccount_tvNameUser.text = snapshot.child("userName").value.toString()
                        frgUserAccount_tvPhoneUser.text = snapshot.child("phoneNumber").value.toString()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

    private fun logOut() {
        val intent = Intent(activity, LoginActivity::class.java)
        auth.signOut()
        appPreferences.setIsLogin(false)
        appPreferences.setLoginEmail("")
        appPreferences.setLoginUserName("")
        appPreferences.setLoginAvatar("")
        startActivity(intent)
    }

    private fun moveEditScreen() {
        val editProfileFragment = EditProfileFragment()
        val bundle = Bundle()
        bundle.putString(USER_NAME, frgUserAccount_tvNameUser.text.toString())
        bundle.putString(TYPE_ACCOUNT, USER)
        bundle.putString(PHONE_NUMBER, frgUserAccount_tvPhoneUser.text.toString())
        bundle.putString(URL_AVATAR, Uri.parse(urlAvatar.toString()).toString())
        editProfileFragment.arguments = bundle
        addFragment(editProfileFragment, R.id.actUser_frameLayout, EditProfileFragment::class.java.simpleName)
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.frgUserAccount_tvLogout -> logOut()
            R.id.frgUserAccount_imgEdit -> moveEditScreen()
        }
    }
}