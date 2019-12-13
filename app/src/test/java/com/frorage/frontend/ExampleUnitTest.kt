package com.frorage.frontend

import com.frorage.frontend.api.MockApi
import com.frorage.frontend.api.Url.Companion.MOCK_URL
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.junit.Test

import org.junit.Assert.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun isApiResponding() {
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(MOCK_URL)
            .client(OkHttpClient.Builder().addInterceptor { chain ->
                val original = chain.request()

                val requestBuilder = original.newBuilder() // here we can add authorization token
                    .header("Authorization", "")

                val request = requestBuilder.build()
                chain.proceed(request)
            }.build())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val userApi = retrofit.create(MockApi::class.java)
        val call = userApi.getMockApiRequest()
        //call.enqueue(this) // you will need callbacks
        val execute = call.execute()
        if(execute.isSuccessful)
        {
            assert(execute.body() != null)
            println(execute.message())
            println("body: " + execute.body()?.message)
        }else{
            assert(false)
        }

    }
}
