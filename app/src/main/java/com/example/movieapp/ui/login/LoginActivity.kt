package com.example.movieapp.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.example.movieapp.base.BaseActivity
import com.example.movieapp.data.local.AppPreferences
import com.example.movieapp.ui.main.UserActivity
import com.example.movieapp.ui.main.MainActivity
import com.example.movieapp.utils.ACCOUNT
import com.example.movieapp.utils.ADMIN
import com.example.movieapp.utils.USER
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*
import android.view.inputmethod.InputMethodManager
import com.example.movieapp.R


class LoginActivity : BaseActivity(), View.OnClickListener {
    private lateinit var auth: FirebaseAuth
    private lateinit var appPreferences: AppPreferences
    private lateinit var databaseReference: DatabaseReference
    private var onlineID = ""
    private var isHideRegister = false

    override fun getLayoutID(): Int {
        return R.layout.activity_login
    }

    override fun doViewCreated() {
        auth = FirebaseAuth.getInstance()
        appPreferences = AppPreferences(this@LoginActivity)
        initListener()
        hideKeyboardWhenClickOutside()
        hideRegister()
    }

    private fun hideRegister() {
        isHideRegister = intent.extras?.getBoolean("hideRegister")!!
        if (isHideRegister) {
            actLogin_tvHaveAccount.visibility = View.GONE
            actLogin_tvForgotPassword.visibility = View.GONE
        }
        else {
            actLogin_tvHaveAccount.visibility = View.VISIBLE
            actLogin_tvForgotPassword.visibility = View.VISIBLE
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun hideKeyboardWhenClickOutside() {
        repeat(2) {
            actLogin_layout.setOnTouchListener { v, event ->
                val imm =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                true
            }
        }
    }

    private fun initListener() {
        actLogin_tvLogin.setOnClickListener(this)
        actLogin_tvRegister.setOnClickListener(this)
        actLogin_tvHaveAccount.setOnClickListener(this)
        actLogin_tvBackToLogin.setOnClickListener(this)
        actLogin_tvForgotPassword.setOnClickListener(this)
        actLogin_tvResetPassword.setOnClickListener(this)
        actLogin_tvBackToLoginResetPassword.setOnClickListener(this)
    }

    private fun forgotPassword() {
        actLogin_tvResetPassword.visibility = View.VISIBLE
        actLogin_tvBackToLoginResetPassword.visibility = View.VISIBLE
        actLogin_tvBackToLoginResetPassword.isEnabled = true
        actLogin_etPassword.visibility = View.GONE
        actLogin_tvLogin.visibility = View.GONE
        actLogin_tvHaveAccount.visibility = View.GONE
        actLogin_tvForgotPassword.visibility = View.GONE
    }

    private fun resetPassword() {
        val emailResetPassword = actLogin_etEmail.text.toString()
        if (TextUtils.isEmpty(emailResetPassword))
            Toast.makeText(this, "Input email for reset password", Toast.LENGTH_SHORT).show()
        else {
            auth.sendPasswordResetEmail(emailResetPassword).addOnCompleteListener {
                if (it.isSuccessful) {
                    showLoading()
                    Toast.makeText(this, "Check email for reset password", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.putExtra("hideRegister", false)
                    startActivity(intent)
                    hideLoading()
                } else {
                    Toast.makeText(this, "Email not register", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun login() {
        loginWithEmailPassword(
            actLogin_etEmail.text.toString(),
            actLogin_etPassword.text.toString()
        )
    }

    private fun loginWithEmailPassword(email: String, password: String) {
        when {
            TextUtils.isEmpty(email) ->
                Toast.makeText(this, "Don't email blank!!", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(password) ->
                Toast.makeText(this, "Don't password blank!!", Toast.LENGTH_SHORT).show()
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                Toast.makeText(this, "Wrong email format!!!", Toast.LENGTH_SHORT).show()
            else -> {
                showLoading()
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, OnCompleteListener { task ->
                        if (task.isSuccessful) {
                            appPreferences.setIsLogin(true)
                            appPreferences.setLoginEmail(email)
                            onlineID = auth.currentUser?.uid.toString()
                            if (email.contains("admin")) {
    //                            databaseReference =
    //                                FirebaseDatabase.getInstance().reference.child(ACCOUNT).child(ADMIN).child(onlineID)
    //                            databaseReference.setValue(true)
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            } else {
    //                            databaseReference =
    //                                FirebaseDatabase.getInstance().reference.child(ACCOUNT).child(USER).child(onlineID)
    //                            databaseReference.setValue(true)
                                val intent = Intent(this, UserActivity::class.java)
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
    }

    private fun register() {
        registerWithEmailPassword(
            actLogin_etEmail.text.toString(),
            actLogin_etPassword.text.toString()
        )
    }

    private fun registerWithEmailPassword(email: String, password: String) {
        when {
            TextUtils.isEmpty(email) ->
                Toast.makeText(this, "Don't leave email blank!!", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(password) ->
                Toast.makeText(this, "Don't leave password blank!!", Toast.LENGTH_SHORT).show()
            actLogin_etConfirmPassword.text.toString() != actLogin_etPassword.text.toString() ->
                Toast.makeText(this, "Confirm password failed!!!", Toast.LENGTH_SHORT).show()
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                Toast.makeText(this, "Wrong email format!!!", Toast.LENGTH_SHORT).show()
            else -> {
                showLoading()
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            onlineID = auth.currentUser?.uid.toString()
                            if (email.contains("admin")) {
                                databaseReference =
                                    FirebaseDatabase.getInstance().reference.child(ACCOUNT).child(ADMIN).child(onlineID)
                                databaseReference.setValue(true)
                            } else {
                                databaseReference =
                                    FirebaseDatabase.getInstance().reference.child(ACCOUNT).child(USER).child(onlineID)
                                databaseReference.setValue(true)
                            }
                            Toast.makeText(this, "Register success", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, LoginActivity::class.java)
                            intent.putExtra("hideRegister", false)
                            startActivity(intent)
                            hideLoading()
                        } else {
                            Toast.makeText(this, "Register failed", Toast.LENGTH_SHORT).show()
                            hideLoading()
                        }
                    }
                actLogin_etEmail.setText("")
                actLogin_etPassword.setText("")
                actLogin_etConfirmPassword.visibility = View.GONE
            }
        }
    }

    private fun showRegister() {
        actLogin_tvRegister.visibility = View.VISIBLE
        actLogin_etConfirmPassword.visibility = View.VISIBLE
        actLogin_tvLogin.visibility = View.GONE
        actLogin_tvHaveAccount.visibility = View.GONE
        actLogin_tvRegister.isEnabled = true
        actLogin_tvBackToLogin.visibility = View.VISIBLE
        actLogin_tvBackToLogin.isEnabled = true
        actLogin_tvForgotPassword.visibility = View.GONE
    }

    private fun backToLogin() {
        actLogin_tvRegister.visibility = View.GONE
        actLogin_etConfirmPassword.visibility = View.GONE
        actLogin_tvLogin.visibility = View.VISIBLE
        actLogin_tvHaveAccount.visibility = View.VISIBLE
        actLogin_tvRegister.isEnabled = false
        actLogin_tvBackToLogin.visibility = View.GONE
        actLogin_tvBackToLogin.isEnabled = false
        actLogin_tvBackToLoginResetPassword.visibility = View.GONE
        actLogin_tvBackToLoginResetPassword.isEnabled = false
        actLogin_etPassword.visibility = View.VISIBLE
        actLogin_tvResetPassword.visibility = View.GONE
        actLogin_tvResetPassword.isEnabled = false
        actLogin_tvForgotPassword.visibility = View.VISIBLE
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.actLogin_tvLogin -> login()
            R.id.actLogin_tvRegister -> register()
            R.id.actLogin_tvHaveAccount -> showRegister()
            R.id.actLogin_tvBackToLogin -> backToLogin()
            R.id.actLogin_tvForgotPassword -> forgotPassword()
            R.id.actLogin_tvResetPassword -> resetPassword()
            R.id.actLogin_tvBackToLoginResetPassword -> backToLogin()
        }
    }

}