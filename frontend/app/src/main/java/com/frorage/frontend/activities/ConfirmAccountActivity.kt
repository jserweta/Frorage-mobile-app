package com.frorage.frontend.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.frorage.frontend.InputValidation
import com.frorage.frontend.R
import com.frorage.frontend.api.RetrofitClient
import com.frorage.frontend.model.Model
import com.frorage.frontend.storage.SharedPrefMenager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_confirm_account.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Class which manage all fields and implements logic connected with confirm account with code from your e-mail activity.
 *
 * @property AppCompatActivity extends LoginActivity with methods which helps to add functionality to the GUI
 * @property View.OnClickListener sets listener which make buttons clickable
 *
 */
class ConfirmAccountActivity : AppCompatActivity(), View.OnClickListener{

    private var btnConfirmAccount: AppCompatButton? = null
    private var resendText: TextView? = null

    private var confirmAccountLayout: TextInputLayout? = null
    private var confirmAccount: TextInputEditText? = null

    private lateinit var inputValidation: InputValidation

    /**
     * Override function onCreate - it initialize all views, listeners and objects when the activity started.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_account)

        initViews()

        initListeners()

        initObjects()

    }


    /**
     *  Override function onClick which perform action when user clicked certain button.
     */
    override fun onClick(v: View) {
        //val sharedPrefMenager = SharedPrefMenager.getInstance(applicationContext)
        when(v.id){
            R.id.btn_confirm_account ->{
                if (verifyInputData()){
                    val accessToken = confirm_token.text.toString().trim()
                    val call = RetrofitClient.userApi.confirmAccount(
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
                            val code = response.code()
                            val body = response.body()
                            val errorBody = response.errorBody()

                            when (code) {
                                200 -> {
                                    val intent = Intent(this@ConfirmAccountActivity, LoginActivity::class.java)
                                    startActivity(intent)
                                    Toast.makeText(applicationContext, body?.message, Toast.LENGTH_LONG).show()
                                }
                                400 -> {
                                    val message = errorBody!!.string().removeRange(0,27)
                                    val messageErr = message.substringAfter("\"", message).substringBefore("\"")
                                    Toast.makeText(applicationContext, messageErr, Toast.LENGTH_LONG).show()
                                }
                                else -> {
                                    Toast.makeText(applicationContext, "Response code: $code", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    })
                }
            }

            R.id.resend_email -> {
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
                        val code = response.code()
                        val body = response.body()
                        val errorBody = response.errorBody()

                        when (code) {
                            200 -> {
                                Toast.makeText(applicationContext, body?.message, Toast.LENGTH_LONG).show()
                            }
                            400 -> {
                                val message = errorBody!!.string().removeRange(0,27)
                                val messageErr = message.substringAfter("\"", message).substringBefore("\"")
                                Toast.makeText(applicationContext, messageErr, Toast.LENGTH_LONG).show()
                            }
                            else -> {
                                Toast.makeText(applicationContext, "Response code: $code", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                })
            }
        }
    }

    /**
     * Function which verify if data in login fields are valid using InputValidation class and set statement.
     *
     * @return boolean value true when data are valid and false when they are not.
     */
    private fun verifyInputData(): Boolean {
        if (!inputValidation.isInputEditTextFilled(
                confirmAccount!!, confirmAccountLayout!!, getString(
                    R.string.error_message_access_token_empty
                )
            )
        ) {
            return false
        }
        return true
    }

    /**
     * Function which initialize all variables which are needed to add functionality to all GUI elements.
     */
    private fun initViews() {
        btnConfirmAccount = findViewById<View>(R.id.btn_confirm_account) as AppCompatButton

        resendText = findViewById<View>(R.id.resend_email) as TextView
        confirmAccountLayout = findViewById<View>(R.id.confirm_token_layout) as TextInputLayout

        confirmAccount = findViewById<View>(R.id.confirm_token) as TextInputEditText

    }

    /**
     * Function which initialize button listeners for resending e-mail with confirmation code and for confirming your account.
     */
    private fun initListeners() {
        resendText!!.setOnClickListener(this)
        btnConfirmAccount!!.setOnClickListener(this)
    }

    /**
     * Function which initialize object of InputValidation class.
     */
    private fun initObjects() {
        inputValidation = InputValidation(this@ConfirmAccountActivity)
    }

}
