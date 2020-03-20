package com.frorage.frontend.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.frorage.frontend.InputValidation
import com.frorage.frontend.R
import com.frorage.frontend.api.RetrofitClient
import com.frorage.frontend.model.Model
import com.frorage.frontend.storage.SharedPrefMenager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Class which manage all fields and implements logic connected with login activity.
 *
 * @property AppCompatActivity extends LoginActivity with methods which helps to add functionality to the GUI
 * @property View.setOnClickListener sets listener which make buttons clickable
 *
 */

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private var logContainer: ConstraintLayout? = null
    private var logEmailLayout: TextInputLayout? = null
    private var logPasswordLayout: TextInputLayout? = null

    private var logEmail: TextInputEditText? = null
    private var logPassword: TextInputEditText? = null
    private var btnRegister: AppCompatButton? = null
    private var btnLogin: AppCompatButton? = null

    private lateinit var inputValidation: InputValidation


    /**
     * Override function onStart which at the very beginning has to check if user is logged in and if yes it put user to GeneralActivity.
     */
    override fun onStart() {
        super.onStart()
        if (SharedPrefMenager.getInstance(this).isLoggedIn) {
            val intent = Intent(applicationContext, GeneralActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }


    /**
     * Override function onCreate - it initialize all views, listeners and objects when the activity started.
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // initializing the views
        initViews()

        // initializing the listeners
        initListeners()

        // initializing the objects
        initObjects()
    }

    /**
     *  Override function onClick which perform action when user clicked certain button.
     */
    override fun onClick(v: View) {
        val sharedPrefMenager = SharedPrefMenager.getInstance(applicationContext)
        when (v.id) {
            R.id.btn_login -> {
                if (verifyInputData()){
                    val logEmail = log_email.text.toString().trim()
                    val logPassword = log_password.text.toString().trim()

                    val call = RetrofitClient.userApi.login(
                        Model.LoginRequestObj(logPassword, logEmail)
                    )
                    call.enqueue(object : Callback<Model.LoginResponse> {
                        override fun onFailure(call: Call<Model.LoginResponse>, t: Throwable) {
                            Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                        }

                        override fun onResponse(
                            call: Call<Model.LoginResponse>,
                            response: Response<Model.LoginResponse>
                        ) {
                            val code = response.code()
                            val body = response.body()
                            val errorBody = response.errorBody()

                            when (code) {
                                200 -> {
                                    sharedPrefMenager.saveToken(body!!)
                                    sharedPrefMenager.savePassword(logPassword)
                                    val intent = Intent(this@LoginActivity, SelectKitchenActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)

                                    Toast.makeText(applicationContext, "Welcome!", Toast.LENGTH_LONG).show()
                                }
                                401 -> {
                                    val message = errorBody!!.string().removeRange(0,91)
                                    val messageErr = message.substringBefore("\"",message)
                                    Toast.makeText(applicationContext,"$messageErr!", Toast.LENGTH_LONG).show()     /*"Email or password is not correct!"*/
                                }
                                else -> {
                                    Toast.makeText(applicationContext, "Response code: $code", Toast.LENGTH_LONG).show()
                                }
                            }
                        }

                    })
                }
            }

            R.id.btn_register -> {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
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
                logEmail!!, logEmailLayout!!, getString(
                    R.string.error_message_email_empty
                )
            )
        ) {
            return false
        }
        if (!inputValidation.isInputEditTextEmail(
                logEmail!!, logEmailLayout!!, getString(
                    R.string.error_message_email
                )
            )
        ) {
            return false
        }
        if (!inputValidation.isInputEditTextFilled(
                logPassword!!, logPasswordLayout!!, getString(
                    R.string.error_message_password_empty
                )
            )
        ) {
            return false
        }
        if (!inputValidation.isInputEditPassword(
                logPassword!!, logPasswordLayout!!, getString(
                    R.string.error_message_password
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
        logContainer = findViewById<View>(R.id.login_container) as ConstraintLayout

        logEmailLayout = findViewById<View>(R.id.log_email_layout) as TextInputLayout
        logPasswordLayout = findViewById<View>(R.id.log_password_layout) as TextInputLayout

        logEmail = findViewById<View>(R.id.log_email) as TextInputEditText
        logPassword = findViewById<View>(R.id.log_password) as TextInputEditText
        btnLogin = findViewById<View>(R.id.btn_login) as AppCompatButton
        btnRegister = findViewById<View>(R.id.btn_register) as AppCompatButton
    }

    /**
     * Function which initialize login and register button listeners.
     */
    private fun initListeners() {
        btnRegister!!.setOnClickListener(this)
        btnLogin!!.setOnClickListener(this)
    }

    /**
     * Function which initialize object of InputValidation class.
     */
    private fun initObjects() {
        inputValidation = InputValidation(this@LoginActivity)
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

