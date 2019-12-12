package com.frorage.frontend.api

import com.frorage.frontend.model.Model
//import com.frorage.frontend.model.RegisterResponse
//import com.frorage.frontend.model.Token
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface FrorageApiInterface {
    @FormUrlEncoded
    @POST(Url.REGISTER_URL)
    fun register(
        @Field("email") email:String,
        @Field("password") password:String,
        @Field("username") username:String
    ):Call<Model.RegisterResponse>

    @FormUrlEncoded
    @POST(Url.LOGIN_URL)
    fun login(
        @Field("password") password: String,
        @Field("usernameOrEmail") username: String
    ):Call<Model.Token>
}