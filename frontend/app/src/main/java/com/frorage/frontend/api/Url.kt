package com.frorage.frontend.api

class Url {
    companion object{
        const val BASE_URL = "http://192.168.0.66:8080/api/"
        const val REGISTER_URL = "user/register"
        const val LOGIN_URL = "user/login"
        const val CONFIRM_ACCOUNT = "user/confirm-account"
        const val RESEND_EMAIL = "user/resend-email"
        const val KITCHEN = "kitchen"
        const val JOIN = "kitchen/join"
        const val KITCHEN_FOR_USER = "kitchen/list"
        const val SHOPPING_LIST = "shopping_list/{kitchen_id}"
        const val ADD_FULL_PRODUCT = "product_full"
        const val SET_TO_BUY = "set_tobuy/{to_buy}"
        const val PRODUCT_LIST = "product/list/{kitchen_id}"
        const val DELETE_PRODUCT = "product/delete/{product_id}"
        const val UPDATE_PRODUCT = "product/update"
        const val RECIPE_LIST = "recipe/list/{kitchen_id}"
        const val DELETE_RECIPE = "recipe/delete/{recipe_id}"
        const val ADD_RECIPE = "recipe"
        const val ADD_INGREDIENT = "ingredient/{kitchen_id}"
        const val GET_STATE = "recipe/available_products/{recipe_id}"
        const val GET_INGREDIENTS = "ingredient/list/{recipeId}"
        const val MOCK_URL = "http://www.mocky.io/v2/"
        const val DELETE_UPDATE_USER = "user"
        const val DELETE_KITCHEN = "kitchen/delete/{id}"

    }
}/*192.168.43.13 -> hot-spot
3.160 -> serwetki3
3.166 -> salon
3.174 -> serwetki2
0.66 -> ThisIsSparta*/