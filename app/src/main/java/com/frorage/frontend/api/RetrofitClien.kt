package com.frorage.frontend.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClien {
    private val interceptor = Interceptor { chain ->
        val url = chain.request().url()
            .newBuilder()
            .build()

        val request = chain.request()
            .newBuilder()
            .url(url)
            .build()
        chain.proceed(request)

        /*val original = chain.request()

        val url = chain.request().url().newBuilder().build()
        val request = chain.request()
            .newBuilder()
            .addHeader("Authorization", "")
            .url(url)
            .build()

        if (request.url().encodedPath().contains("/user/login") && request.method().equals("post") || (request.url().encodedPath().contains(
                "/user/register"
            ) && request.method().equals("post"))
        ) {
            chain.proceed(original)
        }else{
            chain.proceed(request)
        }*/
    }

    private val apiClient = OkHttpClient().newBuilder().addInterceptor(interceptor).build()

    private fun retofit(baseURL: String): Retrofit {
        return Retrofit.Builder().client(apiClient)
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    val frorageApi: FrorageApiInterface = retofit(Url.BASE_URL).create(FrorageApiInterface::class.java)
}