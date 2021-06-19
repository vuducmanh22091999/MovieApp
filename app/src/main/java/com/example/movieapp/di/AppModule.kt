package com.example.movieapp.di

import com.example.movieapp.data.remote.MovieAppServices
import com.example.movieapp.utils.BASE_URL
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val appModule = module {
    single { providerGson() }
    single { providerRetrofit(get()) }
    single { providerApp(get()) }
    single { providerHttp() }
}

fun providerGson(): Gson = GsonBuilder().create()

fun providerRetrofit(gson: Gson): Retrofit =
    Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(gson))
        .build()

fun providerApp(retrofit: Retrofit): MovieAppServices =
    retrofit.create(MovieAppServices::class.java)

fun providerHttp(): OkHttpClient {
    val okHttpBuilder = OkHttpClient.Builder()
    okHttpBuilder.readTimeout(60, TimeUnit.SECONDS)
    okHttpBuilder.connectTimeout(60, TimeUnit.SECONDS)
    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.BODY
    okHttpBuilder.addInterceptor(logging)
    return okHttpBuilder.build()
}

