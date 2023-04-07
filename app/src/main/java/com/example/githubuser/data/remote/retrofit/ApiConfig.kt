package com.example.githubuser.data.remote.retrofit

import com.example.githubuser.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {

    companion object {
        fun getApiService(): ApiService {
            val client = OkHttpClient.Builder()
                .addInterceptor(
                    Interceptor { chain ->
                        val request = chain.request()
                        val requestHeaders = request.newBuilder()
                            .addHeader("Authorization", BuildConfig.KEY)
                            .build()
                        chain.proceed(requestHeaders)
                    }
                )
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}