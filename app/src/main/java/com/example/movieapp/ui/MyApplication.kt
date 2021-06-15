package com.example.movieapp.ui

import android.app.Application
import com.example.movieapp.di.appModule
import com.example.movieapp.di.repositoryModule
import com.example.movieapp.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            modules(listOf(appModule, repositoryModule, viewModelModule))
        }
    }
}