package com.frorage.frontend.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.frorage.frontend.InputValidation
import com.frorage.frontend.R
import com.frorage.frontend.api.KitchenApiRequest
import com.frorage.frontend.storage.SharedPrefMenager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_kitchen_creator.*

/**
 * Class which manage all fields and implements logic connected with creating new or joining to existing kitchen activity.
 *
 * @property AppCompatActivity extends LoginActivity with methods which helps to add functionality to the GUI
 * @property View.OnClickListener sets listener which make buttons clickable
 *
 */
class KitchenCreatorActivity : AppCompatActivity(), View.OnClickListener{

    private var btnBack: ImageButton? = null
    private var btnLetFrorage: AppCompatButton? = null

    private var kitchenNameLayout: TextInputLayout? = null
    private var kitchenPasswordLayout: TextInputLayout? = null
    private var kitchenName: TextInputEditText? = null
    private var kitchenPassword: TextInputEditText? = null

    private lateinit var inputValidation: InputValidation
    private lateinit var kitchenApiRequest: KitchenApiRequest

    /**
     * Override function onCreate - it initialize all views, listeners and objects when the activity started.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kitchen_creator)



        initViews()

        initListeners()

        initObjects()

        val header = SharedPrefMenager.getInstance(applicationContext).token
        kitchenApiRequest.getAllKitchenList(header)
    }

    /**
     *  Override function onClick which perform action when user clicked certain button.
     */
    override fun onClick(v: View) {
        val sharedPrefMenager = SharedPrefMenager.getInstance(applicationContext)
        when(v.id){
            R.id.btn_let_frorage_create ->{
                if(verifyInputData()){
                    val kitchenName = kitchen_name.text.toString().trim()
                    val kitchenPassword = kitchen_password.text.toString().trim()
                    val header = SharedPrefMenager.getInstance(applicationContext).token

                    if (checkIfKitchenIsAtList(kitchenName)){
                        kitchenApiRequest.joinKitchen(kitchenName, kitchenPassword, header)
                        val joinSuccess = sharedPrefMenager.getJoinSuccess

                        if(joinSuccess){
                            val intent = Intent(applicationContext, GeneralActivity::class.java)
                            startActivity(intent)
                            sharedPrefMenager.saveJoinSuccess(false)
                        }
                    }else{
                        kitchenApiRequest.createNewKitchen(kitchenName, kitchenPassword, header)
                        //kitchenApiRequest.joinKitchen(kitchenName, kitchenPassword, header)

                        //val joinSuccess2 = sharedPrefMenager.getJoinSuccess
                        val createSuccess = sharedPrefMenager.getCreateSuccess

                        if (createSuccess) {
                            val intent = Intent(applicationContext, GeneralActivity::class.java)
                            startActivity(intent)
                            sharedPrefMenager.saveJoinSuccess(false)
                            sharedPrefMenager.saveCreateSuccess(false)
                        }
                    }
                }
            }
            R.id.btn_back -> {
                val intent = Intent(this@KitchenCreatorActivity, SelectKitchenActivity::class.java)
                startActivity(intent)
            }
        }
    }


    private fun checkIfKitchenIsAtList(kitchenName: String): Boolean{

        val allKitchenList = SharedPrefMenager.getInstance(applicationContext).allKitchenList

        if (allKitchenList == null) {
            return false
        }else{
            allKitchenList.forEach {
                if (it.kitchenName.equals(kitchenName)){
                    return true
                }
            }
        }
        return false
    }

    /**
     * Function which verify if data in login fields are valid using InputValidation class and set statement.
     *
     * @return boolean value true when data are valid and false when they are not.
     */
    private fun verifyInputData(): Boolean {
        if (!inputValidation.isInputEditTextFilled(
                kitchenName!!, kitchenNameLayout!!, getString(
                    R.string.error_message_name_empty
                )
            )
        ) {
            return false
        }
        if (!inputValidation.isInputEditTextName(
                kitchenName!!, kitchenNameLayout!!, getString(
                    R.string.error_message_name
                )
            )
        ) {
            return false
        }
        if (!inputValidation.isInputEditTextFilled(
                kitchenPassword!!, kitchenPasswordLayout!!, getString(
                    R.string.error_message_password_empty
                )
            )
        ) {
            return false
        }
        if (!inputValidation.isInputEditPassword(
                kitchenPassword!!, kitchenPasswordLayout!!, getString(
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

        btnBack = findViewById<View>(R.id.btn_back) as ImageButton
        btnLetFrorage = findViewById<View>(R.id.btn_let_frorage_create) as AppCompatButton

        kitchenNameLayout = findViewById<View>(R.id.kitchen_name_layout) as TextInputLayout
        kitchenPasswordLayout = findViewById<View>(R.id.password_layout) as TextInputLayout

        kitchenName = findViewById<View>(R.id.kitchen_name) as TextInputEditText
        kitchenPassword = findViewById<View>(R.id.kitchen_password) as TextInputEditText

    }

    /**
     * Function which initialize button listeners for resending e-mail with confirmation code and for confirming your account.
     */
    private fun initListeners() {
        btnBack!!.setOnClickListener(this)
        btnLetFrorage!!.setOnClickListener(this)
    }

    /**
     * Function which initialize object of InputValidation and KitchenApi Request class.
     */
    private fun initObjects() {
        inputValidation = InputValidation(this@KitchenCreatorActivity)
        kitchenApiRequest = KitchenApiRequest(this@KitchenCreatorActivity)
    }
}
