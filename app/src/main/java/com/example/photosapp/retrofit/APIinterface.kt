package com.example.photosapp.retrofit

import com.example.photosapp.Data.ImageResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


interface APIinterface {
    @GET("photos")
    suspend fun getPhotos(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("client_id") clientId: String
    ): List<ImageResponse>
}
