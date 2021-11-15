package com.example.movieapp.ui.selection_type

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.example.movieapp.R
import com.example.movieapp.data.local.AppPreferences
import com.example.movieapp.ui.login.LoginActivity
import com.example.movieapp.ui.main.UserActivity
import com.example.movieapp.utils.SPLASH_DISPLAY_LENGTH
import kotlinx.android.synthetic.main.activity_selection.*

class SelectionActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var appPreferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection)

        iniListener()
    }

    private fun iniListener() {
        actSelection_btnAdmin.setOnClickListener(this)
        actSelection_btnUser.setOnClickListener(this)
    }

    private fun moveAdminScreen() {
        Handler().postDelayed({
            val intentNewScreen = Intent(this@SelectionActivity, LoginActivity::class.java)
            intentNewScreen.putExtra("hideRegister", true)
            startActivity(intentNewScreen)
            finish()
        }, SPLASH_DISPLAY_LENGTH)
    }

    private fun moveUserScreen() {
        Handler().postDelayed({
            val intentNewScreen = Intent(this@SelectionActivity, UserActivity::class.java)
            startActivity(intentNewScreen)
            finish()
        }, SPLASH_DISPLAY_LENGTH)
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.actSelection_btnAdmin -> moveAdminScreen()
            R.id.actSelection_btnUser -> moveUserScreen()
        }
    }
}