package com.example.movieapp.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

abstract class BaseActivity: AppCompatActivity() {
    abstract fun getLayoutID(): Int
    abstract fun doViewCreated()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutID())
        doViewCreated()
    }

    fun addFragment(fragment: Fragment, id: Int) {
        supportFragmentManager.beginTransaction()
                .add(id, fragment, fragment::class.java.simpleName)
                .addToBackStack(null)
                .commit()
    }
}