package com.example.movieapp.ui.edit

import android.content.Intent
import android.os.Handler
import android.view.View
import android.widget.Toast
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.ui.main.AdminActivity
import com.example.movieapp.ui.main.MainActivity
import com.example.movieapp.utils.SPLASH_DISPLAY_LENGTH
import kotlinx.android.synthetic.main.fragment_edit_profile.*

class EditProfileFragment: BaseFragment(), View.OnClickListener {
    override fun getLayoutID(): Int {
        return R.layout.fragment_edit_profile
    }

    override fun doViewCreated() {
        handleBottom()
        initListener()
    }

    private fun initListener() {
        frgAccount_imgSave.setOnClickListener(this)
    }

    private fun handleBottom() {
        (activity as MainActivity).hideBottom()
    }

    private fun saveProfile() {
        showLoading()
        back()

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.frgAccount_imgSave -> saveProfile()
        }
    }
}