package com.frorage.frontend.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.frorage.frontend.InputValidation
import com.frorage.frontend.R
import com.frorage.frontend.api.RetrofitClien
import com.frorage.frontend.model.Model
import com.frorage.frontend.storage.SharedPrefMenager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private var logContainer: ConstraintLayout? = null
    private var logEmailLayout: TextInputLayout? = null
    private var logPasswordLayout: TextInputLayout? = null

    private var logEmail: TextInputEditText? = null
    private var logPassword: TextInputEditText? = null
    private var btnRegister: Button? = null
    private var btnLogin: Button? = null

    private lateinit var inputValidation: InputValidation

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //supportActionBar!!.hide()

        // initializing the views
        initViews()

        // initializing the listeners
        initListeners()

        // initializing the objects
        initObjects()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_login -> {
                verifyInputData()

                val logEmail = log_email.text.toString().trim()
                val logPassword = log_password.text.toString().trim()

                RetrofitClien.frorageApi.login(
                    logPassword,
                    logEmail
                )
                    .enqueue(object : Callback<Model.Token> {
                        override fun onFailure(call: Call<Model.Token>, t: Throwable) {
                            Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                        }

                        override fun onResponse(
                            call: Call<Model.Token>,
                            response: Response<Model.Token>
                        ) {
                            if (response.code() == 201) {
                                SharedPrefMenager.getInstance(applicationContext)
                                    .saveToken(response.body()?.loginResponse!!)

                               /* val tokenInterceptor = TokenInterceptor(
                                    SharedPrefMenager.getInstance(applicationContext).token
                                )*/

                                val intent = Intent(this@LoginActivity, MyItemsActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                Toast.makeText(applicationContext, "Welcome!", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(
                                    applicationContext, "Email or password is not correct!", Toast.LENGTH_LONG).show()
                            }
                        }

                    })
            }

            R.id.btn_register -> {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }
        }

    }

    private fun verifyInputData() {
        if (!inputValidation.isInputEditTextFilled(
                logEmail!!, logEmailLayout!!, getString(
                    R.string.error_message_email_empty
                )
            )
        ) {
            return
        }
        if (!inputValidation.isInputEditTextEmail(
                logEmail!!, logEmailLayout!!, getString(
                    R.string.error_message_email
                )
            )
        ) {
            return
        }
        if (!inputValidation.isInputEditTextFilled(
                logPassword!!, logPasswordLayout!!, getString(
                    R.string.error_message_password_empty
                )
            )
        ) {
            return
        }
        if (!inputValidation.isInputEditPassword(
                logPassword!!, logPasswordLayout!!, getString(
                    R.string.error_message_password
                )
            )
        ) {
            return
        }


        /* try {
            login()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }*/
    }


    private fun initViews() {
        logContainer = findViewById<View>(R.id.login_container) as ConstraintLayout

        logEmailLayout = findViewById<View>(R.id.log_email_layout) as TextInputLayout
        logPasswordLayout = findViewById<View>(R.id.log_password_layout) as TextInputLayout

        logEmail = findViewById<View>(R.id.log_email) as TextInputEditText
        logPassword = findViewById<View>(R.id.log_password) as TextInputEditText
        btnLogin = findViewById<View>(R.id.btn_login) as AppCompatButton
        btnRegister = findViewById<View>(R.id.btn_register) as AppCompatButton
    }

    private fun initListeners() {
        btnRegister!!.setOnClickListener(this)

        btnLogin!!.setOnClickListener(this)
    }

    private fun initObjects() {
        inputValidation = InputValidation(this@LoginActivity)

    }


    override fun onStart() {
        super.onStart()

        if (SharedPrefMenager.getInstance(this).isLoggedIn) {
            val intent = Intent(applicationContext, MyItemsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

}


/*
private fun onTaskCompleted(response: String, task: Int) {
    Log.d("responsejson", response)
    removeSimpleProgressDialog()  //will remove progress dialog
    when (task) {
        LoginTask -> if (isSuccess(response)) {
            saveInfo(response)
            Toast.makeText(this@LoginActivity, "Login Successfully!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@LoginActivity, KitchenCreator::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            this.finish()
        } else {
            Toast.makeText(this@LoginActivity, getErrorMessage(response), Toast.LENGTH_SHORT).show()
        }
    }
}

fun showSimpleProgressDialog(context: Context, title: String?, msg: String, isCancelable: Boolean) {
    try {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(context, title, msg)
            mProgressDialog!!.setCancelable(isCancelable)
        }
        if (!mProgressDialog!!.isShowing) {
            mProgressDialog!!.show()
        }

    } catch (ie: IllegalArgumentException) {
        ie.printStackTrace()
    } catch (re: RuntimeException) {
        re.printStackTrace()
    } catch (e: Exception) {
        e.printStackTrace()
    }

}

fun removeSimpleProgressDialog() {
    try {
        if (mProgressDialog != null) {
            if (mProgressDialog!!.isShowing) {
                mProgressDialog!!.dismiss()
                mProgressDialog = null
            }
        }
    } catch (ie: IllegalArgumentException) {
        ie.printStackTrace()

    } catch (re: RuntimeException) {
        re.printStackTrace()
    } catch (e: Exception) {
        e.printStackTrace()
    }

}
*/

