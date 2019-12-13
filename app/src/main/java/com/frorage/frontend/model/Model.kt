package com.frorage.frontend.model

import java.time.LocalDateTime

object Model {
    data class LoginResponse(val accessToken:String, val tokenType:String, val message: String)
    data class RegisterResponse(val success: Boolean, val message: String)
    data class Token (val loginResponse: LoginResponse)
    data class User(val createdAt: LocalDateTime, val email:String, val enabled:Boolean, val id:Int, val password:String, val updatedAt: LocalDateTime, val username:String )
}