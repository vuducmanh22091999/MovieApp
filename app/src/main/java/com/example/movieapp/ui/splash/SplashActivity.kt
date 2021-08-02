package com.example.movieapp.ui.splash

import android.content.Intent
import android.os.Handler
import com.example.movieapp.R
import com.example.movieapp.base.BaseActivity
import com.example.movieapp.data.local.AppPreferences
import com.example.movieapp.ui.login.LoginActivity
import com.example.movieapp.ui.main.AdminActivity
import com.example.movieapp.ui.main.MainActivity
import com.example.movieapp.utils.SPLASH_DISPLAY_LENGTH
import com.facebook.CallbackManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashActivity : BaseActivity() {
    private lateinit var appPreferences: AppPreferences
    override fun getLayoutID(): Int {
        return R.layout.activity_splash
    }

    override fun doViewCreated() {
        appPreferences = AppPreferences(this)
        openNewScreen()
    }

    private fun openNewScreen() {
        if (appPreferences.getIsLogin() == true) {
            if (appPreferences.getLoginEmail()?.contains("admin") == true) {
                Handler().postDelayed({
                    val intentNewScreen = Intent(this@SplashActivity, AdminActivity::class.java)
                    startActivity(intentNewScreen)
                    finish()
                }, SPLASH_DISPLAY_LENGTH)
            } else {
                Handler().postDelayed({
                    val intentNewScreen = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intentNewScreen)
                    finish()
                }, SPLASH_DISPLAY_LENGTH)
            }

        } else {
            Handler().postDelayed({
                val intentNewScreen = Intent(this@SplashActivity, LoginActivity::class.java)
                startActivity(intentNewScreen)
                finish()
            }, SPLASH_DISPLAY_LENGTH)
        }

    }
}