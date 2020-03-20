package com.frorage.frontend.storage

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.frorage.frontend.model.Model
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class SharedPrefMenager private constructor(private val mCtx: Context) {

    val isLoggedIn: Boolean
        get() {
            val sharedPreferances = mCtx.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
            return sharedPreferances.getInt("userId", -1) != -1
        }

    /*fun saveUserId(userId: Int){
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("userId", userId)
        editor.apply()
    }*/
    fun clearUserId(){
        val sharedPrefMenager = mCtx.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        val editor = sharedPrefMenager.edit()

        editor.remove("userId")
        editor.apply()
    }
    val getJoinSuccess: Boolean
        get() {
            val sharedPrefMenager = mCtx.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
            return sharedPrefMenager.getBoolean("join", false)
        }

    val getCreateSuccess: Boolean
        get() {
            val sharedPrefMenager = mCtx.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
            return sharedPrefMenager.getBoolean("create", false)
        }

    fun saveJoinSuccess(join: Boolean) {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("join", join)
        editor.apply()
    }

    fun saveCreateSuccess(create: Boolean) {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("create", create)
        editor.apply()
    }

    val activatedKitchenData: Model.CreateOrJoinKitchenResponse
        get() {
            val sharedPreferances = mCtx.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
            return Model.CreateOrJoinKitchenResponse(
                sharedPreferances.getInt("activatedKitchenId", -1),
                sharedPreferances.getString("activatedKitchenName", "")!!,
                sharedPreferances.getString("activatedKitchenPassword", "")!!
            )
        }

    fun saveActivatedKitchenData(kitchenId: Int, kitchenName: String, kitchenPassword: String) {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.remove("activatedKitchenId")
        editor.remove("activatedKitchenName")
        editor.remove("activatedKitchenPassword")
        editor.apply()

        editor.putInt("activatedKitchenId", kitchenId)
        editor.putString("activatedKitchenName", kitchenName)
        editor.putString("activatedKitchenPassword", kitchenPassword)
        editor.apply()
    }


    val actualPassword: String
        get() {
            val sharedPreferances = mCtx.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
            return sharedPreferances.getString("password", "")!!
        }


    fun savePassword(logPassword: String) {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("password", logPassword)
        editor.apply()
    }

    val email: String
        get() {
            val sharedPreferances = mCtx.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
            return sharedPreferances.getString("email", "")!!
        }


    fun saveEmail(email: String) {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("email", email)
        editor.apply()
    }

    val allKitchenList: ArrayList<Model.CreateOrJoinKitchenResponse>?
        get() {
            val sharedPreferances = mCtx.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
            val gson = Gson()
            val json = sharedPreferances.getString("allKitchenList", null)
            val type = object : TypeToken<ArrayList<Model.CreateOrJoinKitchenResponse?>?>() {}.type

            return gson.fromJson(json, type)
        }

    val userKitchenList: ArrayList<Model.CreateOrJoinKitchenResponse>?
        get() {
            val sharedPreferances = mCtx.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
            val gson = Gson()
            val json = sharedPreferances.getString("userKitchenList", null)
            val type = object : TypeToken<ArrayList<Model.CreateOrJoinKitchenResponse?>?>() {}.type

            return gson.fromJson(json, type)
        }

    fun saveKitchenData(kitchen: ArrayList<Model.CreateOrJoinKitchenResponse>, key: String) {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(kitchen)
        editor.putString(key, json)
        editor.apply()
    }

    val token: String
        get() {
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)

            val tokenType = sharedPreferences.getString("tokenType", "")!!
            val accsessToken = sharedPreferences.getString("accessToken", "")!!
            return "$tokenType $accsessToken"
        }

    fun saveToken(token: Model.LoginResponse) {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("tokenType", token.tokenType)
        editor.putString("accessToken", token.accessToken)
        editor.putInt("userId", token.userId)
        editor.apply()
    }

    fun clear() {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.clear()
        editor.apply()
    }

    fun clearKitchenList(key: String) {
        val sharedPrefMenager = mCtx.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        val editor = sharedPrefMenager.edit()

        editor.remove(key)
        editor.apply()
    }


    companion object {
        private val SHARED_PREF_NAME = "my_shared_pref"
        private var mInstance: SharedPrefMenager? = null
        @Synchronized
        fun getInstance(mCtx: Context): SharedPrefMenager {
            if (mInstance == null) {
                mInstance = SharedPrefMenager(mCtx)
            }
            return mInstance as SharedPrefMenager
        }
    }
}