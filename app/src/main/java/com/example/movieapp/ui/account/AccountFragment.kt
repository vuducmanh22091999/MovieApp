package com.example.movieapp.ui.account

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import com.bumptech.glide.Glide
import com.example.movieapp.BuildConfig
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.local.AppPreferences
import com.example.movieapp.ui.edit.EditProfileFragment
import com.example.movieapp.ui.login.LoginActivity
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_account.*

class AccountFragment : BaseFragment(), View.OnClickListener {
    private lateinit var appPreferences: AppPreferences
    private lateinit var auth: FirebaseAuth



    override fun getLayoutID(): Int {
        return R.layout.fragment_account
    }

    override fun doViewCreated() {
        auth = Firebase.auth
        appPreferences = context?.let { AppPreferences(it) }!!
//        checkLogin()
        initListener()
        setInfoApp()
//        setInfo()
    }

    private fun checkLogin() {
        val user = auth.currentUser
        if (user == null) {
            val intentNewScreen = Intent(context, LoginActivity::class.java)
            startActivity(intentNewScreen)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setInfoApp() {
        val versionName = BuildConfig.VERSION_NAME
        val versionCode = BuildConfig.VERSION_CODE
        frgAccount_tvTitleAbout.text = "Version Name = $versionName\n" +
                                        "Version Code = $versionCode"
    }

    private fun setInfoUser() {
        frgAccount_tvNameUser.text = appPreferences.getLoginUserName()
        frgAccount_tvEmailUser.text = appPreferences.getLoginEmail()
        if (getIDUserFacebook().isNotEmpty()) {
            context?.let {
                Glide.with(it).load(urlAvatar()).placeholder(R.drawable.ic_account).into(frgAccount_imgAvatar)
            }
        } else {
            context?.let {
                Glide.with(it).load(appPreferences.getLoginAvatar())
                    .placeholder(R.drawable.ic_account).into(frgAccount_imgAvatar)
            }
        }
    }

    private fun getIDUserFacebook(): String {
        var facebookUserId = ""
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            for (profile in user.providerData) {
                if (FacebookAuthProvider.PROVIDER_ID == profile.providerId) {
                    facebookUserId = profile.uid
                }
            }
        }
        return facebookUserId
    }

    private fun urlAvatar(): String {
        return "https://graph.facebook.com/${getIDUserFacebook()}/picture?type=large"
    }

    private fun initListener() {
        frgAccount_tvLogout.setOnClickListener(this)
        frgAccount_imgEdit.setOnClickListener(this)
    }

    private fun logOut() {
        Firebase.auth.signOut()
        val intent = Intent(activity, LoginActivity::class.java)
        appPreferences.setLoginEmail("")
        appPreferences.setLoginUserName("")
        appPreferences.setLoginAvatar("")
        startActivity(intent)
    }

    private fun moveEditScreen() {
        addFragment(EditProfileFragment(), R.id.frameLayout, EditProfileFragment::class.java.simpleName)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.frgAccount_tvLogout -> logOut()
            R.id.frgAccount_imgEdit -> moveEditScreen()
        }
    }

    override fun onResume() {
        super.onResume()
        setInfoUser()
    }
}