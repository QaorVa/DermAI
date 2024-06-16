package com.example.dermai.data.retrofit

import com.example.dermai.data.model.ResultResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("upload/")
    fun uploadImage(
        @Body image: RequestBody
    ): Call<ResultResponse>
}