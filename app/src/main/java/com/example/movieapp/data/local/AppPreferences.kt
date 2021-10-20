package com.example.movieapp.data.local

import android.content.Context

class AppPreferences(context: Context) {
    private val LOGIN_SAVE_EMAIL = "login_save_email"
    private val LOGIN_SAVE_USER_NAME = "login_save_user_name"
    private val LOGIN_SAVE_PHOTO_URL = "login_save_photo_url"
    private val IS_LOGIN = "is_login"

    private val appPreferences =
        context.getSharedPreferences("db_movie_app", Context.MODE_PRIVATE)

    fun setIsLogin(boolean: Boolean) {
        appPreferences.edit().putBoolean(IS_LOGIN, boolean).apply()
    }

    fun getIsLogin(): Boolean {
        return appPreferences.getBoolean(IS_LOGIN, false)
    }

    fun setLoginEmail(email: String) {
        appPreferences.edit().putString(LOGIN_SAVE_EMAIL, email).apply()
    }

    fun getLoginEmail(): String? {
        return appPreferences.getString(LOGIN_SAVE_EMAIL, null)
    }

    fun setLoginUserName(userName: String) {
        appPreferences.edit().putString(LOGIN_SAVE_USER_NAME, userName).apply()
    }

    fun getLoginUserName(): String? {
        return appPreferences.getString(LOGIN_SAVE_USER_NAME, null)
    }

    fun setLoginAvatar(photoUrl: String) {
        appPreferences.edit().putString(LOGIN_SAVE_PHOTO_URL, photoUrl).apply()
    }

    fun getLoginAvatar(): String? {
        return appPreferences.getString(LOGIN_SAVE_PHOTO_URL, null)
    }
}