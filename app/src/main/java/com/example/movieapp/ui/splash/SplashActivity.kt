package com.example.movieapp.ui.splash

import android.content.Intent
import android.os.Handler
import com.example.movieapp.R
import com.example.movieapp.base.BaseActivity
import com.example.movieapp.data.local.AppPreferences
import com.example.movieapp.ui.login.LoginActivity
import com.example.movieapp.ui.main.MainActivity
import com.example.movieapp.utils.SPLASH_DISPLAY_LENGTH
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashActivity : BaseActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var appPreferences: AppPreferences

    override fun getLayoutID(): Int {
        return R.layout.activity_splash
    }

    override fun doViewCreated() {
        auth = Firebase.auth
        appPreferences = AppPreferences(this)
        openNewScreen()
    }

    private fun openNewScreen() {
        val user = auth.currentUser
        if (user != null) {
            Handler().postDelayed({
                val intentMainScreen = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intentMainScreen)
                finish()
            }, SPLASH_DISPLAY_LENGTH)
        } else {
            Handler().postDelayed({
                val intentNewScreen = Intent(this@SplashActivity, LoginActivity::class.java)
                startActivity(intentNewScreen)
                finish()
            }, SPLASH_DISPLAY_LENGTH)
        }

    }
}