package com.example.movieapp.ui.login

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
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity(), View.OnClickListener {
    private val RC_SIGN_IN = 1
    private lateinit var auth: FirebaseAuth
    private var callBackManager: CallbackManager? = null
    private lateinit var appPreferences: AppPreferences

    override fun getLayoutID(): Int {
        return R.layout.activity_login
    }

    override fun doViewCreated() {
        auth = FirebaseAuth.getInstance()
        callBackManager = CallbackManager.Factory.create()
        appPreferences = AppPreferences(this@LoginActivity)
        initListener()

    }

    private fun initListener() {
        actLogin_tvLogin.setOnClickListener(this)
        actLogin_tvRegister.setOnClickListener(this)
        actLogin_tvHaveAccount.setOnClickListener(this)
    }

    private fun login() {
        loginWithEmailPassword(actLogin_etEmail.text.toString(), actLogin_etPassword.text.toString())
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
                        if (email.contains("admin")) {
                            val intent = Intent(this, AdminActivity::class.java)
                            startActivity(intent)
                        } else {
                            val intent = Intent(this, MainActivity::class.java)
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
                .addOnCompleteListener(this, OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Register success", Toast.LENGTH_SHORT).show()
                        onBackPressed()
                        hideLoading()
                    }
                })
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

    private fun loginWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun loginWithFacebook() {
        LoginManager.getInstance()
            .logInWithReadPermissions(this@LoginActivity, listOf("public_profile", "email"))
        LoginManager.getInstance()
            .registerCallback(callBackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    handleFacebookAccessToken(result.accessToken)
                }

                override fun onCancel() {

                }

                override fun onError(error: FacebookException?) {

                }

            })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userName = user?.displayName ?: ""
                    val email = user?.email ?: ""
                    val photoUrl = user?.photoUrl ?: ""

//                    appPreferences.setLoginUserName(userName)
//                    appPreferences.setLoginEmail(email)
//                    appPreferences.setLoginAvatar(photoUrl.toString())

                    switchMainScreen()
                } else {
                    Log.w("Login with Facebook", "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        this, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val email = user?.email ?: ""
                    val userName = user?.displayName ?: ""
                    val photoUrl = user?.photoUrl ?: ""

//                    appPreferences.setLoginEmail(email)
//                    appPreferences.setLoginUserName(userName)
//                    appPreferences.setLoginAvatar(photoUrl.toString())

                    switchMainScreen()
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d("Login Google", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w("Login Google", "Google sign in failed", e)
            }
        }

        callBackManager?.onActivityResult(requestCode, resultCode, data)
    }

    private fun switchMainScreen() {
        showLoading()
        finish()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.actLogin_tvLogin -> login()
            R.id.actLogin_tvRegister -> register()
            R.id.actLogin_tvHaveAccount -> showRegister()
        }
    }

}