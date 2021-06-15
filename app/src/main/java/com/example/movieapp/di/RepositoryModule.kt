package com.example.movieapp.di

import com.example.movieapp.data.repository.MovieRepo
import org.koin.dsl.module

val repositoryModule = module {
    factory { MovieRepo(get()) }
}
