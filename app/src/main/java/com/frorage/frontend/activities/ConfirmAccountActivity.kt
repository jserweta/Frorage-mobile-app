package com.frorage.frontend.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.frorage.frontend.InputValidation
import com.frorage.frontend.R
import com.frorage.frontend.api.RetrofitClient
import com.frorage.frontend.model.Model
import com.frorage.frontend.storage.SharedPrefMenager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_confirm_account.*
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConfirmAccountActivity : AppCompatActivity(), View.OnClickListener{

    // private var btnBack: ImageButton? = null
    private var btnLetForage: AppCompatButton? = null

    private var resendText: TextView? = null

    private var confirmAccountLayout: TextInputLayout? = null
    private var confirmAccount: TextInputEditText? = null

    private lateinit var inputValidation: InputValidation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_account)

        initViews()

        initListeners()

        initObjects()

    }

    private fun initObjects() {
        inputValidation = InputValidation(this@ConfirmAccountActivity)

    }


    private fun initListeners() {
        // btnBack!!.setOnClickListener(this)
        resendText!!.setOnClickListener(this)
        btnLetForage!!.setOnClickListener(this)
    }

    private fun initViews() {
        btnLetForage = findViewById<View>(R.id.btn_let_frorage) as AppCompatButton
        //btnBack = findViewById<View>(R.id.btn_back) as ImageButton

        resendText = findViewById<View>(R.id.resend_email) as TextView
        confirmAccountLayout = findViewById<View>(R.id.confirm_token_layout) as TextInputLayout

        confirmAccount = findViewById<View>(R.id.confirm_token) as TextInputEditText

    }

    private fun verifyInputData() {
        if (!inputValidation.isInputEditTextFilled(
                confirmAccount!!, confirmAccountLayout!!, getString(
                    R.string.error_message_access_token_empty
                )
            )
        ) {
            return
        }
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.btn_let_frorage ->{
                verifyInputData()

                val accessToken = confirm_token.text.toString().trim()

                val call = RetrofitClient.userApi.confirmAcc(
                    Model.ConfirmToken(accessToken)
                )
                call.enqueue(object : Callback<Model.GeneralResponse> {
                    override fun onFailure(call: Call<Model.GeneralResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(
                        call: Call<Model.GeneralResponse>,
                        response: Response<Model.GeneralResponse>
                    ) {
                        if (response.code() == 200){
                            val intent = Intent(this@ConfirmAccountActivity, LoginActivity::class.java)
                            startActivity(intent)
                            Toast.makeText(applicationContext, response.body()?.message, Toast.LENGTH_LONG).show() //lub response.body()?.status
                        }else if (response.code() == 400){
                            Toast.makeText(applicationContext, response.body()?.message, Toast.LENGTH_LONG).show()
                        }else{
                            Toast.makeText(applicationContext, response.code(), Toast.LENGTH_LONG).show()
                        }
                    }
                })
            }
            R.id.resend_email -> {
                //TODO(Find email value which is send from server in RegisterActivity )
               // val email = RegisterActivity().reg_email.text.toString().trim()
                val email = SharedPrefMenager.getInstance(applicationContext).email
                val call = RetrofitClient.userApi.resendEmail(
                    Model.ResendEmail(email)
                )
                call.enqueue(object : Callback<Model.GeneralResponse>{
                    override fun onFailure(call: Call<Model.GeneralResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(
                        call: Call<Model.GeneralResponse>,
                        response: Response<Model.GeneralResponse>
                    ) {
                        if (response.code() == 200){
                            Toast.makeText(applicationContext, response.body()?.message, Toast.LENGTH_LONG).show() //lub response.body()?.status
                        }else if (response.code() == 400){
                            Toast.makeText(applicationContext, response.body()?.message, Toast.LENGTH_LONG).show()
                        }else{
                            Toast.makeText(applicationContext, response.code(), Toast.LENGTH_LONG).show()
                        }
                    }
                })

            }
            /*R.id.btn_back ->{
                val intent = Intent(this@ConfirmAccountActivity, RegisterActivity::class.java)
                startActivity(intent)
            }*/
        }
    }
}
