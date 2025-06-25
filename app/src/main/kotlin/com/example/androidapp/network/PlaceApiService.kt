package com.example.androidapp.network

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response

interface PlaceApiService {
    @GET("places")
    suspend fun getPlaces(
        @Query("categories") categories: String = "catering.restaurant",
        @Query("filter", encoded = true) filter: String,
        @Query("bias", encoded = true) bias: String,
        @Query("limit") limit: Int = 15,
        @Query("apiKey") apiKey: String = "cd19448433e4488288126482bef62091"
    ): Response<ResponseBody>
}
