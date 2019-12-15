package com.frorage.frontend.model

object Model {
    data class LoginResponse(val accessToken:String, val tokenType:String )

    data class RegisterResponse(val expiredDateTime: String, val id: Int, val status: String, val token: String, val user: User)
    data class User(val createdAt: String, val email: String, val enabled: Boolean, val id: Int, val password: String, val updatedAt: String, val username: String)
    data class ConfirmToken(val token: String)

    data class UserRequestObj(val password: String, val usernameOrEmail: String)
    data class RegisterRequestObj(val email: String, val password: String, val username: String)
    data class GeneralResponse(val message: String, val success: Boolean)
    data class ResendEmail(val email: String)

}