package com.example.movieapp.data.local

import android.content.Context

class AppPreferences(context: Context) {
    val LOGIN_SAVE_EMAIL = "login_save_email"
    val LOGIN_SAVE_USER_NAME = "login_save_user_name"
    val LOGIN_SAVE_PHOTO_URL = "login_save_photo_url"

    private val appPreferences =
        context.getSharedPreferences("db_movie_app", Context.MODE_PRIVATE)

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