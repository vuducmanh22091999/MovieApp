package com.example.movieapp

import android.content.Intent
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {
    override fun getLayoutID() : Int {
        return R.layout.activity_main
    }

    override fun doViewCreated() {
        test.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }
    }
}