package com.example.dermai.data.retrofit

import com.example.dermai.BuildConfig.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
class ApiConfig {
    companion object {
        fun getApiService(): ApiService {
            val client = OkHttpClient.Builder()
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}
