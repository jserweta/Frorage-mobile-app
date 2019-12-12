package com.frorage.frontend.storage

import android.content.Context
import com.frorage.frontend.model.Model

class SharedPrefMenager private constructor(private val mCtx: Context) {

    val isLoggedIn: Boolean
        get() {
            val sharedPreferances = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferances.getInt("id", -1) != -1
        }

    val token: String
        get() {
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            val access = sharedPreferences.getString("accessToken", null)!!
            val type = sharedPreferences.getString("tokenType", null)!!
            return "$type $access"
        }

    fun saveToken(token: Model.LoginResponse){
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("accessToken", token.accessToken)
        editor.putString("tokenType", token.tokenType)

        editor.apply()
    }

    fun clear(){
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.clear()
        editor.apply()
    }

    companion object{
        private val SHARED_PREF_NAME = "my_shared_pref"
        private var mInstance: SharedPrefMenager? = null
        @Synchronized
        fun getInstance(mCtx: Context): SharedPrefMenager{
            if (mInstance == null){
                mInstance = SharedPrefMenager(mCtx)
            }
            return mInstance as SharedPrefMenager
        }
    }
}