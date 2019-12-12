package com.frorage.frontend.model

import java.sql.Date

object Model {
    data class LoginResponse(val accessToken:String, val tokenType:String)
    data class RegisterResponse(val expiredDateTime: Date, val id:Int, val status:String, val token: String, val user: User, val success: Boolean, val message: String)
    data class Token (val loginResponse: LoginResponse)
    data class User(val createdAt: Date, val email:String, val enabled:Boolean, val id:Int, val password:String, val updatedAt: Date, val username:String )
}