package com.example.musicapp.network.api

import com.example.musicapp.network.service.DeezerApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://api.deezer.com/"

private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .readTimeout(120, TimeUnit.SECONDS)
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .build()


object DeezerApi {
    val retrofitService: DeezerApiService by lazy {
        retrofit.create(DeezerApiService::class.java)
    }
}

