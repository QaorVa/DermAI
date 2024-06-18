package com.example.dermai.data.retrofit

import com.example.dermai.data.model.ResultResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("upload/")
    fun uploadImage(
        @Part image: MultipartBody.Part
    ): Call<ResultResponse>
}