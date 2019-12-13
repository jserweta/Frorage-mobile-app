package com.frorage.frontend.api

import retrofit2.Call
import retrofit2.http.GET

interface MockApi {
    @GET("5df261693100005d009a32a0")
    fun getMockApiRequest(): Call<MockResponse>
}