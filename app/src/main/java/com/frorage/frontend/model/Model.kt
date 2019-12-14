package com.frorage.frontend.model

object Model {
    data class LoginResponse(val accessToken:String, val tokenType:String, val message: String)
    data class RegisterResponse(val success: Boolean, val message: String)
    data class Token (val loginResponse: LoginResponse)
    data class UserRequestObj(val password: String, val usernameOrEmail: String)
    data class RegisterRequestObj(val email: String, val password: String, val username: String)

}