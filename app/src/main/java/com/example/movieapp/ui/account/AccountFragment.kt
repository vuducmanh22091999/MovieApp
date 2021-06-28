package com.example.movieapp.ui.account

import android.content.Intent
import android.view.View
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.local.AppPreferences
import com.example.movieapp.ui.login.LoginActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_account.*

class AccountFragment : BaseFragment(), View.OnClickListener {
    private lateinit var appPreferences: AppPreferences
    override fun getLayoutID(): Int {
        return R.layout.fragment_account
    }

    override fun doViewCreated() {
        appPreferences = context?.let { AppPreferences(it) }!!
        initListener()
        setInfo()
    }

    private fun setInfo() {
        frgAccount_tvNameUser.text = appPreferences.getLoginUserName()
        frgAccount_tvEmailUser.text = appPreferences.getLoginEmail()
        context?.let {
            Glide.with(it).load(appPreferences.getLoginAvatar())
                .placeholder(R.drawable.ic_account).into(frgAccount_imgAvatar)
        }
    }

    private fun initListener() {
        frgAccount_tvLogout.setOnClickListener(this)
    }

    private fun logOut() {
        Firebase.auth.signOut()
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.frgAccount_tvLogout -> logOut()
        }
    }
}