package com.frorage.frontend.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.constraintlayout.widget.ConstraintLayout
import com.frorage.frontend.InputValidation
import com.frorage.frontend.R
import com.frorage.frontend.api.RetrofitClient
import com.frorage.frontend.model.Model
import com.frorage.frontend.storage.SharedPrefMenager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Class which manage all fields and implements logic connected with register activity.
 *
 * @property AppCompatActivity extends LoginActivity with methods which helps to add functionality to the GUI
 * @property View.setOnClickListener sets listener which make buttons clickable
 *
 */

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private var regContainer: ConstraintLayout? = null
    private var regNameLayout: TextInputLayout? = null
    private var regEmailLayout: TextInputLayout? = null
    private var regPasswordLayout: TextInputLayout? = null
    private var regConfirmPasswordLayout: TextInputLayout? = null
    private var regCheckBoxLayout: TextInputLayout? = null

    private var regEmail: TextInputEditText? = null
    private var regName: TextInputEditText? = null
    private var regPassword: TextInputEditText? = null
    private var regConfirmPassword: TextInputEditText? = null
    private var agreementsAccept: AppCompatCheckBox? = null
    private var btnRegister: AppCompatButton? = null
    private var btnBack: ImageButton? = null

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
        setContentView(R.layout.activity_register)
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
            R.id.btn_register -> {
                if (verifyInputData()){
                    val regEmail = reg_email.text.toString().trim()
                    val regPassword = reg_password.text.toString().trim()
                    val regName = reg_username.text.toString().trim()
                    val call = RetrofitClient.userApi.register(
                        Model.RegisterRequestObj(regEmail, regPassword, regName)
                    )
                    call.enqueue(object : Callback<Model.RegisterResponse> {
                        override fun onFailure(call: Call<Model.RegisterResponse>, t: Throwable) {
                            Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                        }

                        override fun onResponse(
                            call: Call<Model.RegisterResponse>,
                            response: Response<Model.RegisterResponse>
                        ) {
                            val body = response.body()
                            val errorBody = response.errorBody()
                            val code = response.code()

                            when (code) {
                                201 -> {
                                    sharedPrefMenager.saveEmail(body!!.user.email)
                                    val intent = Intent(this@RegisterActivity, ConfirmAccountActivity::class.java)
                                    startActivity(intent)
                                    Toast.makeText(applicationContext, "Welcome ${body.user.username}!", Toast.LENGTH_LONG).show()
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

            R.id.btn_back -> {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
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
                regName!!, regNameLayout!!, getString(
                    R.string.error_message_name_empty
                )
            )
        ) {
            return false
        }
        if (!inputValidation.isInputEditTextName(
                regName!!, regNameLayout!!, getString(
                    R.string.error_message_name
                )
            )
        ) {
            return false
        }
        if (!inputValidation.isInputEditTextFilled(
                regEmail!!, regEmailLayout!!, getString(
                    R.string.error_message_email_empty
                )
            )
        ) {
            return false
        }
        if (!inputValidation.isInputEditTextEmail(
                regEmail!!, regEmailLayout!!, getString(
                    R.string.error_message_email
                )
            )
        ) {
            return false
        }
        if (!inputValidation.isInputEditTextFilled(
                regPassword!!, regPasswordLayout!!, getString(
                    R.string.error_message_password_empty
                )
            )
        ) {
            return false
        }
        if (!inputValidation.isInputEditPassword(
                regPassword!!, regPasswordLayout!!, getString(
                    R.string.error_message_password
                )
            )
        ) {
            return false
        }
        if (!inputValidation.isInputEditTextMatches(
                regPassword!!, regConfirmPassword!!,
                regConfirmPasswordLayout!!, getString(R.string.error_password_match)
            )
        ) {
            return false
        }
        if (!inputValidation.isCheckboxChecked(
                agreementsAccept!!, regCheckBoxLayout!!, getString(
                    R.string.error_message_agreements
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
        regContainer = findViewById<View>(R.id.register_container) as ConstraintLayout

        regNameLayout = findViewById<View>(R.id.reg_name_layout) as TextInputLayout
        regEmailLayout = findViewById<View>(R.id.reg_email_layout) as TextInputLayout
        regPasswordLayout = findViewById<View>(R.id.reg_password_layout) as TextInputLayout
        regConfirmPasswordLayout = findViewById<View>(R.id.reg_confirm_password_layout) as TextInputLayout
        regCheckBoxLayout = findViewById<View>(R.id.reg_checkbox_layout) as TextInputLayout

        regName = findViewById<View>(R.id.reg_username) as TextInputEditText
        regEmail = findViewById<View>(R.id.reg_email) as TextInputEditText
        regPassword = findViewById<View>(R.id.reg_password) as TextInputEditText
        regConfirmPassword = findViewById<View>(R.id.reg_confirm_password) as TextInputEditText
        agreementsAccept = findViewById<View>(R.id.agreements_accept) as AppCompatCheckBox
        btnRegister = findViewById<View>(R.id.btn_register) as AppCompatButton
        btnBack = findViewById<View>(R.id.btn_back) as ImageButton
    }

    /**
     * Function which initialize return and register button listeners.
     */
    private fun initListeners() {
        btnRegister!!.setOnClickListener(this)
        btnBack!!.setOnClickListener(this)
    }

    /**
     * Function which initialize object of InputValidation class.
     */
    private fun initObjects() {
        inputValidation = InputValidation(this@RegisterActivity)
    }
}


    /*
    private fun onTaskCompleted(response: String, task: Int) {
        Log.d("responsejson", response)
        removeSimpleProgressDialog()
        when (task) {
            RegTask -> if (isSuccess(response)) {
                saveInfo(response)
                Toast.makeText(this@RegisterActivity, "Registered Successfully!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@RegisterActivity, KitchenCreator::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                this.finish()
            } else {
                Toast.makeText(this@RegisterActivity, getErrorMessage(response), Toast.LENGTH_SHORT).show()
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

    }*/