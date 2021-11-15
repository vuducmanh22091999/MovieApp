package com.example.movieapp.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.movieapp.ui.main.MainActivity

abstract class BaseFragment : Fragment() {
    abstract fun getLayoutID(): Int
    abstract fun doViewCreated()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayoutID(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doViewCreated()
    }

    fun addFragment(fragment: Fragment, id: Int, tag: String? = null) {
        if (activity is BaseActivity) {
            (activity as BaseActivity).addFragment(fragment, id, tag)
        }
    }

    fun AppCompatActivity.hideSoftKeyboard() {
        currentFocus?.run {
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                this.windowToken,
                0
            )
            if (this is EditText) this.clearFocus()
        }
    }

    fun hideKeyboard() {
        if (activity is MainActivity) {
            (activity as MainActivity).hideKeyboard()
        }
    }

    fun showLoading() {
        if (activity is MainActivity) {
            (activity as MainActivity).showLoading()
        }
    }

    fun hideLoading() {
        if (activity is MainActivity) {
            (activity as MainActivity).hideLoading()
        }
    }

    fun back() {
        if (activity is MainActivity) {
            (activity as MainActivity).onBackPressed()
        }
    }
}