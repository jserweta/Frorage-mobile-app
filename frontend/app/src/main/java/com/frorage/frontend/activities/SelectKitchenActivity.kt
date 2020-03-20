package com.frorage.frontend.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import com.frorage.frontend.InputValidation
import com.frorage.frontend.R
import com.frorage.frontend.api.KitchenApiRequest
import com.frorage.frontend.api.RetrofitClient
import com.frorage.frontend.model.Model
import com.frorage.frontend.storage.SharedPrefMenager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Class which manage all fields and implements logic connected with choosing between kitchens user belong.
 *
 * @property AppCompatActivity extends LoginActivity with methods which helps to add functionality to the GUI
 * @property View.OnClickListener sets listener which make buttons clickable
 *
 */
class SelectKitchenActivity : AppCompatActivity(), View.OnClickListener, AdapterView.OnItemSelectedListener {

    private var btnLetFrorage: AppCompatButton? = null
    private var btnSwitchCreate: TextView? = null
    private var spinnerMenu: AppCompatSpinner? = null

    private lateinit var inputValidation: InputValidation
    private lateinit var kitchenApiRequest: KitchenApiRequest

    private var selectedKitchenNamePosition: Int? = null
    private val mutableUserKitchenNamesList = mutableListOf<String>()


    /**
     * Override function onCreate - it initialize all views, listeners and objects when the activity started.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_kitchen)

        initViews()

        initListeners()

        initObjects()

        //val userID = SharedPrefMenager.getInstance(applicationContext).userId

        val kitchenListForUser = makeUserKitchenList()

        spinnerMenu!!.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, kitchenListForUser)
    }


    /**
     *  Override function onClick which perform action when user clicked certain button.
     */
    override fun onClick(v: View) {
        when (v.id){
            R.id.btn_let_frorage ->{
                if (selectedKitchenNamePosition != null && selectedKitchenNamePosition != 0){
                    val activatedKitchenName = mutableUserKitchenNamesList[selectedKitchenNamePosition!!]
                    saveActivatedKitchenData(activatedKitchenName)

                    val intent = Intent(this@SelectKitchenActivity, GeneralActivity::class.java)
                    startActivity(intent)

                    Toast.makeText(applicationContext, "You chose $activatedKitchenName!", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(applicationContext, "Select kitchen from list!", Toast.LENGTH_LONG).show()
                }
            }

            R.id.btn_switch_create ->{
                val intent = Intent(this@SelectKitchenActivity, KitchenCreatorActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectedKitchenNamePosition = parent?.selectedItemPosition

        if (selectedKitchenNamePosition == 0){
            onNothingSelected(parent)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        //Toast.makeText(applicationContext, "Select kitchen from list!", Toast.LENGTH_LONG).show()
    }

    private fun saveActivatedKitchenData(kitchenName: String){
        val kitchenList = SharedPrefMenager.getInstance(applicationContext).userKitchenList
        var kitchenId: Int? = null
        var kitchenPassword: String? = null

        if (kitchenList!!.size != 0){
            kitchenList.forEach {
                if (kitchenName.equals(it.kitchenName)) {
                    kitchenId = it.kitchenId
                    kitchenPassword = it.kitchenPassword
                }
            }
            SharedPrefMenager.getInstance(applicationContext).saveActivatedKitchenData(kitchenId!!, kitchenName, kitchenPassword!!)
        }
    }

    private fun makeUserKitchenList(): MutableList<String>{
        val header = SharedPrefMenager.getInstance(applicationContext).token
        kitchenApiRequest.getKitchenListForUser(header)

        val sharedPrefMenager = SharedPrefMenager.getInstance(applicationContext)

        //val emptyList = SharedPrefMenager.getInstance(applicationContext).getEmptyListStatus

        mutableUserKitchenNamesList.clear()
        mutableUserKitchenNamesList.add(0, "Choose kitchen")
        val kitchenList = sharedPrefMenager.userKitchenList
        if (kitchenList != null){
            kitchenList.forEach {
                mutableUserKitchenNamesList.add(it.kitchenName)
            }
        }else {
            val intent = Intent(this@SelectKitchenActivity, KitchenCreatorActivity::class.java)
            startActivity(intent)
        }



        return mutableUserKitchenNamesList
    }

    /**
     * Function which verify if data password field is valid using InputValidation class and set statement.
     *
     * @return boolean value true when data are valid and false when they are not.
     */
    /*private fun verifyInputData(): Boolean {
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
    }*/

    /**
     * Function which initialize all variables which are needed to add functionality to all GUI elements.
     */
    private fun initViews() {
        btnLetFrorage = findViewById<View>(R.id.btn_let_frorage) as AppCompatButton
        btnSwitchCreate = findViewById<View>(R.id.btn_switch_create) as TextView

        spinnerMenu = findViewById<View>(R.id.kitchen_dropdown_list) as AppCompatSpinner
    }

    /**
     * Function which initialize button listeners for resending e-mail with confirmation code and for confirming your account.
     */
    private fun initListeners() {
        btnSwitchCreate!!.setOnClickListener(this)
        btnLetFrorage!!.setOnClickListener(this)
        spinnerMenu!!.onItemSelectedListener = this
    }

    /**
     * Function which initialize object of InputValidation class.
     */
    private fun initObjects() {
        inputValidation = InputValidation(this@SelectKitchenActivity)
        kitchenApiRequest = KitchenApiRequest(this@SelectKitchenActivity)
    }
}
