package com.example.androidapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api: PlaceApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.geoapify.com/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PlaceApiService::class.java)
    }
}
