package com.frorage.frontend.api

import android.content.Context
import android.widget.Toast
import com.frorage.frontend.model.Model
import com.frorage.frontend.storage.SharedPrefMenager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log

class KitchenApiRequest (private val context: Context){
    private val sharedPrefMenager = SharedPrefMenager.getInstance(context)

    fun getAllKitchenList(header: String){
        val call = RetrofitClient.userApi.getAllKitchenList(
            header
        )
        call.enqueue(object : Callback<ArrayList<Model.CreateOrJoinKitchenResponse>> {
            override fun onFailure(call: Call<ArrayList<Model.CreateOrJoinKitchenResponse>>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ArrayList<Model.CreateOrJoinKitchenResponse>>,
                response: Response<ArrayList<Model.CreateOrJoinKitchenResponse>>
            ) {
                val code = response.code()
                val body = response.body()

                when (code) {
                    200 -> {
                        sharedPrefMenager.clearKitchenList("allKitchenList")
                        sharedPrefMenager.saveKitchenData(body!!, "allKitchenList")
                    }
                }
            }
        })
    }

    fun getKitchenListForUser(header: String){
        val call = RetrofitClient.userApi.getUserKitchenList(
            header
        )
        call.enqueue(object : Callback<ArrayList<Model.CreateOrJoinKitchenResponse>>{
            override fun onFailure(
                call: Call<ArrayList<Model.CreateOrJoinKitchenResponse>>,
                t: Throwable
            ) {
                Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ArrayList<Model.CreateOrJoinKitchenResponse>>,
                response: Response<ArrayList<Model.CreateOrJoinKitchenResponse>>
            ) {
                val code = response.code()
                val body = response.body()
                when (code) {
                    200 -> {
                        sharedPrefMenager.clearKitchenList("userKitchenList")
                        sharedPrefMenager.saveKitchenData(body!!, "userKitchenList")
                    }
                    401 -> {
                        Toast.makeText(context, "Unauthorized!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    fun createNewKitchen(kitchenName: String, kitchenPassword: String, header: String){

        val call = RetrofitClient.userApi.createKitchen(
            header,
            Model.CreateOrJoinKitchenObj(kitchenName, kitchenPassword)
        )
        call.enqueue(object : Callback<Model.CreateOrJoinKitchenResponse>{
            override fun onFailure(call: Call<Model.CreateOrJoinKitchenResponse>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<Model.CreateOrJoinKitchenResponse>,
                response: Response<Model.CreateOrJoinKitchenResponse>
            ) {
                val code = response.code()
                val body = response.body()
                val errorBody = response.errorBody()

                when (code) {
                    201 -> {
                        val kitchenId = body!!.kitchenId

                        sharedPrefMenager.saveActivatedKitchenData(kitchenId, kitchenName, kitchenPassword)
                        sharedPrefMenager.saveCreateSuccess(true)
                        Toast.makeText(context, "Kitchen created!", Toast.LENGTH_LONG).show()   /*${body.kitchenName}*/
                    }
                    400 -> {
                        val message = response.errorBody()!!.string().removeRange(0,27)
                        val messageErr = message.substringAfter("\"", message).substringBefore("\"")

                        Toast.makeText(context, messageErr, Toast.LENGTH_LONG).show() /*"Adding failed!"*/
                    }
                    401 -> {
                        val message = errorBody!!.string().removeRange(0,91)
                        val messageErr = message.substringBefore("\"",message)

                        Toast.makeText(context, messageErr, Toast.LENGTH_LONG).show() /*"Unauthorized!"*/
                    }
                    else -> {
                        Toast.makeText(context, "Response code: $code", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    fun joinKitchen(kitchenName: String, kitchenPassword: String, header: String){

        val call = RetrofitClient.userApi.joinKitchen(
            header,
            Model.CreateOrJoinKitchenObj(kitchenName, kitchenPassword)
        )
        call.enqueue(object : Callback<Model.CreateOrJoinKitchenResponse>{
            override fun onFailure(call: Call<Model.CreateOrJoinKitchenResponse>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<Model.CreateOrJoinKitchenResponse>,
                response: Response<Model.CreateOrJoinKitchenResponse>
            ) {
                val code = response.code()
                val body = response.body()
                val errorBody = response.errorBody()

                when (code){
                    200 -> {
                        sharedPrefMenager.saveJoinSuccess(true)
                        Toast.makeText(context, "You already belong to this kitchen!" , Toast.LENGTH_LONG).show()
                    }
                    201 -> {
                        val kitchenId = body!!.kitchenId

                        sharedPrefMenager.saveActivatedKitchenData(kitchenId,kitchenName, kitchenPassword)
                        sharedPrefMenager.saveJoinSuccess(true)
                        Toast.makeText(context, "You successfully joined $kitchenName", Toast.LENGTH_LONG).show()
                    }
                    400 -> {
                        val message = errorBody!!.string().removeRange(0,27)
                        val messageErr = message.substringAfter("\"", message).substringBefore("\"")

                        Toast.makeText(context, messageErr, Toast.LENGTH_LONG).show()
                    }
                    401 -> {
                        Toast.makeText(context, "Full authentication is required to access this resource!", Toast.LENGTH_LONG).show()   /*Unauthorized!*/
                    }
                    403 -> {
                        Toast.makeText(context, "Check if password and name are correct!", Toast.LENGTH_LONG).show()
                    }
                    404 -> {
                        val message = errorBody!!.string().removeRange(0,27)
                        val messageErr = message.substringAfter("\"", message).substringBefore("\"")

                        Toast.makeText(context, messageErr, Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        Toast.makeText(context, "Response code: $code", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }


}