package com.moviles.clothingapp.ui.utils

import com.moviles.clothingapp.post.data.PostRepository.ApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitInstance {
    // valor parar pruebas en emulador http://10.0.2.2:8000/
    // valor para produccion 34.121.10.209:80
    private val BASE_URL = "http://10.0.2.2:8000/" // this URL of localhost since we run in emulator

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}