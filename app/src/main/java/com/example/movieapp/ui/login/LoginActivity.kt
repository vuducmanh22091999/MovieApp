package com.example.movieapp.ui.login

import android.accounts.Account
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.movieapp.R
import com.example.movieapp.base.BaseActivity
import com.example.movieapp.data.local.AppPreferences
import com.example.movieapp.ui.main.AdminActivity
import com.example.movieapp.ui.main.MainActivity
import com.example.movieapp.utils.ACCOUNT
import com.example.movieapp.utils.ADMIN
import com.example.movieapp.utils.USER
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity(), View.OnClickListener {
    private lateinit var auth: FirebaseAuth
    private lateinit var appPreferences: AppPreferences
    private lateinit var databaseReference: DatabaseReference
    private var onlineID = ""

    override fun getLayoutID(): Int {
        return R.layout.activity_login
    }

    override fun doViewCreated() {
        auth = FirebaseAuth.getInstance()
        appPreferences = AppPreferences(this@LoginActivity)
        initListener()

    }

    private fun initListener() {
        actLogin_tvLogin.setOnClickListener(this)
        actLogin_tvRegister.setOnClickListener(this)
        actLogin_tvHaveAccount.setOnClickListener(this)
    }

    private fun login() {
        loginWithEmailPassword(
            actLogin_etEmail.text.toString(),
            actLogin_etPassword.text.toString()
        )
    }

    private fun loginWithEmailPassword(email: String, password: String) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
            Toast.makeText(this, "Don't blank!!", Toast.LENGTH_SHORT).show()
        else {
            showLoading()
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        appPreferences.setIsLogin(true)
                        appPreferences.setLoginEmail(email)
                        onlineID = auth.currentUser?.uid.toString()
                        if (email.contains("admin")) {
                            databaseReference =
                                FirebaseDatabase.getInstance().reference.child(ACCOUNT).child(ADMIN).child(onlineID)
                            databaseReference.setValue(true)
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            databaseReference =
                                FirebaseDatabase.getInstance().reference.child(ACCOUNT).child(USER).child(onlineID)
                            databaseReference.setValue(true)
                            val intent = Intent(this, AdminActivity::class.java)
                            startActivity(intent)
                        }
                        hideLoading()
                    } else {
                        Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                        hideLoading()
                    }
                })
        }
    }

    private fun register() {
        registerWithEmailPassword(
            actLogin_etEmail.text.toString(),
            actLogin_etPassword.text.toString()
        )
    }

    private fun registerWithEmailPassword(email: String, password: String) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
            Toast.makeText(this, "Don't blank!!", Toast.LENGTH_SHORT).show()
        else {
            showLoading()
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Register success", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        hideLoading()
                    } else {
                        Toast.makeText(this, "Register failed", Toast.LENGTH_SHORT).show()
                        hideLoading()
                    }
                }
            actLogin_etEmail.setText("")
            actLogin_etPassword.setText("")
        }
    }

    private fun showRegister() {
        actLogin_tvRegister.visibility = View.VISIBLE
        actLogin_tvLogin.visibility = View.GONE
        actLogin_tvHaveAccount.visibility = View.GONE
        actLogin_tvRegister.isEnabled = true
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.actLogin_tvLogin -> login()
            R.id.actLogin_tvRegister -> register()
            R.id.actLogin_tvHaveAccount -> showRegister()
        }
    }

}