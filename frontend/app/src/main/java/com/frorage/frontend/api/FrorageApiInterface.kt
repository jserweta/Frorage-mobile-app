package com.frorage.frontend.api

import com.frorage.frontend.model.Model
import retrofit2.Call
import retrofit2.http.*

interface FrorageApiInterface{

    @POST(Url.REGISTER_URL)
    @Headers("Content-Type:application/json")
    fun register(
        @Body register: Model.RegisterRequestObj
    ):Call<Model.RegisterResponse>

    @POST(Url.LOGIN_URL)
    @Headers("Content-Type:application/json")
    fun login(
        @Body user: Model.LoginRequestObj
    ):Call<Model.LoginResponse>

    @POST(Url.CONFIRM_ACCOUNT)
    @Headers("Content-Type:application/json")
    fun confirmAccount(
        @Body token: Model.ConfirmToken
    ):Call<Model.GeneralResponse>

    @POST(Url.RESEND_EMAIL)
    @Headers("Content-Type:application/json")
    fun resendEmail(
        @Body email: Model.ResendEmail
    ):Call<Model.GeneralResponse>

    @POST(Url.KITCHEN)
    @Headers("Content-Type:application/json")
    fun createKitchen(
        @Header("Authorization") header: String,
        @Body addKitchen: Model.CreateOrJoinKitchenObj
    ):Call<Model.CreateOrJoinKitchenResponse>

    @GET(Url.KITCHEN)
    @Headers("Content-Type:application/json")
    fun getAllKitchenList(
        @Header("Authorization") header: String
    ):Call<ArrayList<Model.CreateOrJoinKitchenResponse>>

    @POST(Url.JOIN)
    @Headers("Content-Type:application/json")
    fun joinKitchen(
        @Header("Authorization") header: String,
        @Body joinKitchen: Model.CreateOrJoinKitchenObj
    ):Call<Model.CreateOrJoinKitchenResponse>

    @GET(Url.KITCHEN_FOR_USER)
    @Headers("Content-Type:application/json")
    fun getUserKitchenList(
        @Header("Authorization") header: String
    ):Call<ArrayList<Model.CreateOrJoinKitchenResponse>>

    @GET(Url.SHOPPING_LIST)
    @Headers("Content-Type:application/json")
    fun getShoppingList(
        @Header("Authorization") header: String,
        @Path("kitchen_id") kitchen_id: Int
    ):Call<ArrayList<Model.ShoppingListResponse>>

    @POST(Url.ADD_FULL_PRODUCT)
    @Headers("Content-Type:application/json")
    fun addProduct(
        @Header("Authorization") header: String,
        @Body fullProductAdd: Model.FullProduct
    ):Call<String>

    @POST(Url.SET_TO_BUY)
    @Headers("Content-Type:application/json")
    fun setToBuy(
        @Header("Authorization") header: String,
        @Path("to_buy") toBuy: Boolean,
        @Body listToBuy: ArrayList<Int>
    ):Call<Model.GeneralResponse>

    @GET(Url.PRODUCT_LIST)
    @Headers("Content-Type:application/json")
    fun getProductList(
        @Header("Authorization") header: String,
        @Path("kitchen_id") kitchen_id: Int
    ):Call<ArrayList<Model.FullProductWithId>>

    @DELETE(Url.DELETE_PRODUCT)
    @Headers("Content-Type:application/json")
    fun deleteProduct(
        @Header("Authorization") header: String,
        @Path("product_id") product_id: Int
    ):Call<Model.GeneralResponse>

    @PATCH(Url.UPDATE_PRODUCT)
    @Headers("Content-Type:application/json")
    fun updateProduct(
        @Header("Authorization") header: String,
        @Body product: Model.FullProductWithId
    ):Call<Unit>

    @GET(Url.RECIPE_LIST)
    @Headers("Content-Type:application/json")
    fun getRecipeList(
        @Header("Authorization") header: String,
        @Path("kitchen_id") kitchen_id: Int
    ):Call<ArrayList<Model.Recipe>>

    @DELETE(Url.DELETE_RECIPE)
    @Headers("Content-Type:application/json")
    fun deleteRecipe(
        @Header("Authorization") header: String,
        @Path("recipe_id") recipe_id: Int
    ):Call<Model.GeneralResponse>

    @POST(Url.ADD_RECIPE)
    @Headers("Content-Type:application/json")
    fun addRecipe(
        @Header("Authorization") header: String,
        @Body recipeAdd: Model.RecipeAdd
    ):Call<Model.Recipe>

    @POST(Url.ADD_INGREDIENT)
    @Headers("Content-Type:application/json")
    fun addIngredient(
        @Header("Authorization") header: String,
        @Path("kitchen_id") kitchen_id: Int,
        @Body ingredient: Model.IngredientRequest
    ):Call<Model.GeneralResponse>

    @GET(Url.GET_STATE)
    @Headers("Content-Type:application/json")
    fun getState(
        @Header("Authorization") header: String,
        @Path("recipe_id") recipe_id: Int
    ):Call<Model.IngredientStatus>

    @GET(Url.GET_INGREDIENTS)
    @Headers("Content-Type:application/json")
    fun getIngredients(
        @Header("Authorization") header: String,
        @Path("recipeId") recipeId: Int
    ):Call<ArrayList<Model.Ingredient>>

    @PATCH(Url.UPDATE_PRODUCT)
    @Headers("Content-Type:application/json")
    fun littleProductUpdate(
        @Header("Authorization") header: String,
        @Body product: Model.FullProductWithId
    ):Call<Unit>

    @DELETE(Url.DELETE_UPDATE_USER)
    @Headers("Content-Type:application/json")
    fun deleteCurrentUser(
        @Header("Authorization") header: String
    ):Call<Model.GeneralResponse>

    @PATCH(Url.DELETE_UPDATE_USER)
    @Headers("Content-Type:application/json")
    fun updatePassword(
        @Header("Authorization") header: String,
        @Body oldAndNewPassword: Model.UpdatePasswordObj
    ):Call<Unit>

    @DELETE(Url.DELETE_KITCHEN)
    @Headers("Content-Type:application/json")
    fun deleteKitchen(
        @Header("Authorization") header: String,
        @Path("id") id: Int
    ):Call<Model.GeneralResponse>
}