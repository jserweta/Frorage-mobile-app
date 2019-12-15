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
    ):Call<Model.LoginResponse>

    @POST(Url.CONFIRM_ACCOUNT)
    @Headers("Content-Type:application/json")
    fun confirmAcc(
        @Body token: Model.ConfirmToken
    ):Call<Model.GeneralResponse>

    @POST(Url.RESEND_EMAIL)
    @Headers("Content-Type:application/json")
    fun resendEmail(
        @Body email: Model.ResendEmail
    ):Call<Model.GeneralResponse>
}