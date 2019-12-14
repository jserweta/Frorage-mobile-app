package com.frorage.frontend.api

import android.provider.ContactsContract
import com.frorage.frontend.model.Model
import retrofit2.Call
import retrofit2.http.*

interface FrorageApiInterface {

    @POST(Url.REGISTER_URL)
    @Headers("Content-Type:application/json")
    fun register(
        @Body register: Model.RegisterRequestObj
    ):Call<Model.RegisterResponse>

    @POST(Url.LOGIN_URL)
    @Headers("Content-Type:application/json")
    fun login(
        @Body user: Model.UserRequestObj
    ):Call<Model.Token>
}