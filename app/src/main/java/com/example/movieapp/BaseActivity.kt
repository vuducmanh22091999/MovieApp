package com.example.movieapp

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

abstract class BaseActivity: AppCompatActivity() {
    abstract fun getLayoutID(): Int
    abstract fun doViewCreated()

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(getLayoutID())
        doViewCreated()
    }

    fun addFragment(fragment: Fragment, id: Int, start: Int, end: Int, popStart: Int, popEnd: Int) {
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(start, end, popStart, popEnd)
                .add(id, fragment, fragment::class.java.simpleName)
                .addToBackStack(null)
                .commit()
    }

    fun back() {
        onBackPressed()
    }
}