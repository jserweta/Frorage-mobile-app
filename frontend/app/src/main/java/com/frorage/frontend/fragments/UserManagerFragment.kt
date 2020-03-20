package com.frorage.frontend.fragments


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Display
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import com.frorage.frontend.InputValidation

import com.frorage.frontend.R
import com.frorage.frontend.activities.RegisterActivity
import com.frorage.frontend.activities.SelectKitchenActivity
import com.frorage.frontend.api.KitchenApiRequest
import com.frorage.frontend.api.RetrofitClient
import com.frorage.frontend.model.Model
import com.frorage.frontend.storage.SharedPrefMenager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.fragment_kitchen_manager.*
import kotlinx.android.synthetic.main.fragment_user_manager.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class UserManagerFragment  (private val applicationContext: Context): Fragment(), View.OnClickListener{

    private var btnBack: Button? = null
    private var btnChangePass: AppCompatButton? = null
    private var btnDelete: AppCompatButton? = null

    private var oldPassLayout: TextInputLayout? = null
    private var newPassLayout: TextInputLayout? = null
    private var confirmPassLayout: TextInputLayout? = null

    private var oldPassword: TextInputEditText? = null
    private var newPassword: TextInputEditText? = null
    private var confirmPassword: TextInputEditText? = null

    private lateinit var inputValidation: InputValidation
    private val sharedPrefMenager = SharedPrefMenager.getInstance(applicationContext)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val rootView = inflater.inflate(R.layout.fragment_user_manager, container, false)
        initViews(rootView)

        initListeners()

        initObjects()
        return rootView
    }

    /**
     *  Override function onClick which perform action when user clicked certain button.
     */
    override fun onClick(v: View) {

        when (v.id) {
            R.id.btn_change_password -> {
                if (verifyInputData()){
                    val oldPassword = manager_old_password.text.toString().trim()
                    val newPassword = manager_password.text.toString().trim()
                    val header = sharedPrefMenager.token

                    val call = RetrofitClient.userApi.updatePassword(
                        header,
                        Model.UpdatePasswordObj(newPassword,oldPassword)
                    )
                    call.enqueue(object : Callback<Unit>{
                        override fun onFailure(call: Call<Unit>, t: Throwable) {
                            Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                        }

                        override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                            val code = response.code()

                            when (code){
                                204 -> {
                                    sharedPrefMenager.savePassword(newPassword)
                                    Toast.makeText(applicationContext, "Password updated", Toast.LENGTH_LONG).show()
                                }
                                400 -> {
                                    Toast.makeText(applicationContext, "Wrong old password", Toast.LENGTH_LONG).show()
                                }
                                401 -> {
                                    Toast.makeText(applicationContext, "Unauthorized", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    })

                }

            }
            R.id.btn_delete_user -> {
                val header = sharedPrefMenager.token
                val call = RetrofitClient.userApi.deleteCurrentUser(
                    header
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
                        when (code) {
                            200 -> {
                                sharedPrefMenager.clear()
                                val intent = Intent(applicationContext, RegisterActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)

                                Toast.makeText(applicationContext, body?.message, Toast.LENGTH_LONG).show()
                            }
                            401 -> {
                                Toast.makeText(applicationContext, "Unauthorized", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                })
            }
            R.id.btn_back -> {
                val fragment = ProductListFragment(applicationContext)
                val fragmentManager = activity!!.supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragmentContainer, fragment)
                fragmentTransaction.commit()
            }

        }

    }

    /**
     * Function which verify if data in login fields are valid using InputValidation class and set statement.
     *
     * @return boolean value true when data are valid and false when they are not.
     */
    private fun verifyInputData(): Boolean {

        val oldPass = sharedPrefMenager.actualPassword

        if (!oldPass.equals(oldPassword)){
            R.string.diffrent_old_pass
        }
        if (!inputValidation.isInputEditTextFilled(
                oldPassword!!, oldPassLayout!!, getString(
                    R.string.error_message_password_empty
                )
            )
        ) {
            return false
        }
        if (!inputValidation.isInputEditTextFilled(
                newPassword!!, newPassLayout!!, getString(
                    R.string.error_message_password_empty
                )
            )
        ) {
            return false
        }
        if (!inputValidation.isInputEditTextFilled(
                confirmPassword!!, confirmPassLayout!!, getString(
                    R.string.error_message_password_empty
                )
            )
        ) {
            return false
        }
        if (!inputValidation.isInputEditPassword(
                oldPassword!!, oldPassLayout!!, getString(
                    R.string.error_message_password
                )
            )
        ) {
            return false
        }
        if (!inputValidation.isInputEditPassword(
                newPassword!!, newPassLayout!!, getString(
                    R.string.error_message_password
                )
            )
        ) {
            return false
        }
        if (!inputValidation.isInputEditPassword(
                confirmPassword!!, confirmPassLayout!!, getString(
                    R.string.error_message_password
                )
            )
        ) {
            return false
        }

        if (!inputValidation.isInputEditTextMatches(
                newPassword!!, confirmPassword!!,
                confirmPassLayout!!, getString(R.string.error_password_match)
            )
        ) {
            return false
        }



        return true
    }

    /**
     * Function which initialize all variables which are needed to add functionality to all GUI elements.
     */
    private fun initViews(rootView: View) {

        btnChangePass = rootView.findViewById<View>(R.id.btn_change_password) as AppCompatButton
        btnDelete = rootView.findViewById<View>(R.id.btn_delete_user) as AppCompatButton

        btnBack = rootView.findViewById<View>(R.id.btn_back) as Button

        oldPassLayout = rootView.findViewById<View>(R.id.manager_old_password_layout) as TextInputLayout
        newPassLayout = rootView.findViewById<View>(R.id.manager_password_layout) as TextInputLayout
        confirmPassLayout = rootView.findViewById<View>(R.id.manager_confirm_password_layout) as TextInputLayout

        oldPassword = rootView.findViewById<View>(R.id.manager_old_password) as TextInputEditText
        newPassword = rootView.findViewById<View>(R.id.manager_password) as TextInputEditText
        confirmPassword = rootView.findViewById<View>(R.id.manager_confirm_password) as TextInputEditText

    }

    /**
     * Function which initialize button listeners for resending e-mail with confirmation code and for confirming your account.
     */
    private fun initListeners() {
        btnChangePass!!.setOnClickListener(this)
        btnDelete!!.setOnClickListener(this)
        btnBack!!.setOnClickListener(this)
    }

    /**
     * Function which initialize object of InputValidation class.
     */
    private fun initObjects() {
        inputValidation = InputValidation(applicationContext)
    }


}
