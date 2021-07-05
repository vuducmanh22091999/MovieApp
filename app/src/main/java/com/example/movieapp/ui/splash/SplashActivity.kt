package com.example.movieapp.ui.splash

import android.content.Intent
import android.os.Handler
import com.example.movieapp.R
import com.example.movieapp.base.BaseActivity
import com.example.movieapp.ui.login.LoginActivity
import com.example.movieapp.ui.main.MainActivity
import com.example.movieapp.utils.SPLASH_DISPLAY_LENGTH

class SplashActivity : BaseActivity() {

    override fun getLayoutID(): Int {
        return R.layout.activity_splash
    }

    override fun doViewCreated() {
        openNewScreen()
    }

    private fun openNewScreen() {
        Handler().postDelayed({
            val intentNewScreen = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intentNewScreen)
            finish()
        }, SPLASH_DISPLAY_LENGTH)
    }
}