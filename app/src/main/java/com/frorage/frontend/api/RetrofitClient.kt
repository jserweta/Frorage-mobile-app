package com.frorage.frontend.api

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitClient {
    private val gson = GsonBuilder()
            /*yyyy-MM-dd'T'HH:mm:ss.SSS'Z'*/
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        .create()

    private val retrofit = Retrofit.Builder()
        .baseUrl(Url.BASE_URL)
        .client(OkHttpClient.Builder().addInterceptor{chain ->
            val original = chain.request()

            val requestBuilder = original.newBuilder()
                .header("Authorization", "")

            val request = requestBuilder.build()
            chain.proceed(request)
        }.build())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val userApi = retrofit.create(FrorageApiInterface::class.java)

   /* private val interceptor = Interceptor { chain ->
        val url = chain.request().url()
            .newBuilder()
            .build()

        val request = chain.request()
            .newBuilder()
            .url(url)
            .build()
        chain.proceed(request)

        val original = chain.request()

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
        }
    }

    private val apiClient = OkHttpClient().newBuilder().addInterceptor(interceptor).build()

    private fun retofit(baseURL: String): Retrofit {
        return Retrofit.Builder().client(apiClient)
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    val frorageApi: FrorageApiInterface = retofit(Url.BASE_URL).create(FrorageApiInterface::class.java)*/
}