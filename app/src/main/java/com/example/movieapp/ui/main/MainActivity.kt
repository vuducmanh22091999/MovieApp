package com.example.movieapp.ui.main

import com.example.movieapp.R
import com.example.movieapp.TestFragment
import com.example.movieapp.base.BaseActivity


class MainActivity : BaseActivity() {
    override fun getLayoutID() : Int {
        return R.layout.activity_main
    }

    override fun doViewCreated() {
        addFragment(TestFragment(), R.id.frameLayout)
    }
}