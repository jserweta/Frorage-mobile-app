package com.frorage.frontend.model

object Model {

    /*Response*/
    data class LoginResponse(val userId: Int, val accessToken:String, val tokenType:String)
    data class RegisterResponse(val expiredDateTime: String, val id: Int, val status: String, val token: String, val user: User)
    data class User(val createdAt: String, val email: String, val enabled: Boolean, val id: Int, val password: String, val updatedAt: String, val username: String)
    data class CreateOrJoinKitchenResponse(val kitchenId: Int, val kitchenName: String, val kitchenPassword: String)
    data class GeneralResponse(val message: String, val success: Boolean)
   // data class AllKitchenList(val allKitchenList: Array<CreateOrJoinKitchenResponse>)
    data class ShoppingListResponse(val productName: String, val amount: Float, val unit: String, val expirationDate: String, val favourite: Boolean, val kitchenId: Int, val productId:Int, val runningOut: Boolean, val toBuy: Boolean)

    data class Product(var productName: String, var amount: Float, var unit:  String)
    data class Recipe(val kitchenId: Int, var recipeDescription:  String, val recipeId: Int, var recipeName: String)
    data class RecipeAdd(val kitchenId: Int, var recipeDescription:  String, var recipeName: String)
    data class IngredientStatus(val availableAmount: Int, val requiredAmount: Int)
    /*Request*/

    data class ConfirmToken(val token: String)
    data class CreateOrJoinKitchenObj(val kitchenName: String, val kitchenPassword: String)
    data class LoginRequestObj(val password: String, val usernameOrEmail: String)
    data class RegisterRequestObj(val email: String, val password: String, val username: String)
    data class ResendEmail(val email: String)
    data class FullProduct(val kitchenId: Int, val productName: String, var amount: Float, val unit: String, val toBuy: Boolean, var favourite: Boolean, var runningOut: Boolean, val expirationDate: String?)
    data class FullProductWithId(val productId: Int, val kitchenId: Int, val productName: String, var amount: Float, val unit: String, var toBuy: Boolean, var favourite: Boolean, var runningOut: Boolean, val expirationDate: String?)
    data class IngredientRequest(var amount: Float, var ingredientName: String, var recipeId: Int, var unit: String)

    /*other*/
    data class ShoppingListProduct(val productId: Int, val productName: String, val quantity: Float, val unit: String, var inCart: Boolean)
    data class Ingredient(var amount: Float, var ingredientName: String, var recipeId: Int, var unit: String, var ingredientId: Int, var productId: Int)
    data class Update(var amount: Float)
    data class UpdatePasswordObj(val newPassword: String, val oldPassword: String)
}