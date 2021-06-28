package com.example.movieapp.base

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.movieapp.R

abstract class BaseActivity: AppCompatActivity() {
    abstract fun getLayoutID(): Int
    abstract fun doViewCreated()

    private var dialog: AlertDialog? = null

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

    fun hideKeyboard() {
        val inputManager: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            this.currentFocus!!.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    @SuppressLint("InflateParams")
    fun showLoading() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val layoutInflater: LayoutInflater = this.layoutInflater
        builder.setView(layoutInflater.inflate(R.layout.dialog_loading, null))
        builder.setCancelable(true)

        dialog = builder.create()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.show()
    }

    fun hideLoading() {
        dialog?.dismiss()
    }
}