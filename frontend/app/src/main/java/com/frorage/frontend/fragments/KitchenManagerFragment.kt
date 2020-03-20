package com.frorage.frontend.fragments


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import com.frorage.frontend.InputValidation

import com.frorage.frontend.R
import com.frorage.frontend.activities.GeneralActivity
import com.frorage.frontend.activities.KitchenCreatorActivity
import com.frorage.frontend.activities.SelectKitchenActivity
import com.frorage.frontend.api.KitchenApiRequest
import com.frorage.frontend.api.RetrofitClient
import com.frorage.frontend.model.Model
import com.frorage.frontend.storage.SharedPrefMenager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_kitchen_creator.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple Fragment subclass which implements logic connected with kitchen maganer.
 */
class KitchenManagerFragment(private val applicationContext: Context) : Fragment(),
    View.OnClickListener, AdapterView.OnItemSelectedListener {

    private var btnBack: Button? = null
    private var btnCreate: AppCompatButton? = null
    private var btnSelect: AppCompatButton? = null
    private var spinnerMenu: AppCompatSpinner? = null
    private var btnDelete: AppCompatButton? = null


    private var kitchenNameLayout: TextInputLayout? = null
    private var kitchenPasswordLayout: TextInputLayout? = null
    private var kitchenName: TextInputEditText? = null
    private var kitchenPassword: TextInputEditText? = null

    private lateinit var inputValidation: InputValidation
    private lateinit var kitchenApiRequest: KitchenApiRequest

    private var selectedKitchenNamePosition2: Int? = null
    private val mutableUserKitchenNamesList = mutableListOf<String>()
    private val mutableUserKitchenIdList = mutableListOf<Int>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView =  inflater.inflate(R.layout.fragment_kitchen_manager, container, false)

        initViews(rootView)

        initListeners()

        initObjects()

        val kitchenListForUser = makeUserKitchenList()


        spinnerMenu!!.adapter = ArrayAdapter<String>(
            applicationContext,
            android.R.layout.simple_list_item_1,
            kitchenListForUser
        )

        return rootView
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectedKitchenNamePosition2 = parent?.selectedItemPosition

        if (selectedKitchenNamePosition2 == 0) {
            onNothingSelected(parent)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        //Toast.makeText(applicationContext, "Select kitchen from list!", Toast.LENGTH_LONG).show()
    }

    /**
     *  Override function onClick which perform action when user clicked certain button.
     */
    override fun onClick(v: View) {
        val sharedPrefMenager = SharedPrefMenager.getInstance(applicationContext)
        when (v.id) {
            R.id.create_kitchen -> {
                if (verifyInputData()) {
                    val kitchenName = kitchen_name.text.toString().trim()
                    val kitchenPassword = kitchen_password.text.toString().trim()
                    val header = SharedPrefMenager.getInstance(applicationContext).token

                    if (checkIfKitchenIsAtList(kitchenName)) {
                        kitchenApiRequest.joinKitchen(kitchenName, kitchenPassword, header)
                        val joinSuccess = sharedPrefMenager.getJoinSuccess

                        if (joinSuccess) {
                            val intent = Intent(applicationContext, GeneralActivity::class.java)
                            startActivity(intent)
                            sharedPrefMenager.saveJoinSuccess(false)
                        }
                    } else {
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
            R.id.select_kitchen -> {
                if (selectedKitchenNamePosition2 != null && selectedKitchenNamePosition2 != 0){
                    val activatedKitchenName = mutableUserKitchenNamesList[selectedKitchenNamePosition2!!]
                    saveActivatedKitchenData(activatedKitchenName)

                    val intent = Intent(applicationContext, GeneralActivity::class.java)
                    startActivity(intent)

                    Toast.makeText(applicationContext, "You chose $activatedKitchenName!", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(applicationContext, "Select kitchen from list!", Toast.LENGTH_LONG).show()
                }
            }

            R.id.delete_kitchen -> {
                if (selectedKitchenNamePosition2 != null && selectedKitchenNamePosition2 != 0){
                    val activatedKitchenId = mutableUserKitchenIdList[selectedKitchenNamePosition2!!]
                    val activatedKitchenName = mutableUserKitchenNamesList[selectedKitchenNamePosition2!!]
                    val currentKitchenId = sharedPrefMenager.activatedKitchenData.kitchenId

                    deleteKitchen(activatedKitchenId, activatedKitchenName)

                    if(activatedKitchenId.equals(currentKitchenId)){
                        val intent = Intent(applicationContext, SelectKitchenActivity::class.java)
                        startActivity(intent)
                    }

                }else{
                    Toast.makeText(applicationContext, "Select kitchen from list!", Toast.LENGTH_LONG).show()
                }

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

    private fun deleteKitchen(activatedKitchenId: Int, activatedKitchenName: String) {
        val header = SharedPrefMenager.getInstance(applicationContext).token

        val call = RetrofitClient.userApi.deleteKitchen(
            header,
            activatedKitchenId
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

                when (code) {
                    200 -> {
                        Toast.makeText(applicationContext, "You deleted $activatedKitchenName!", Toast.LENGTH_LONG).show()
                    }
                    403 -> {
                        Toast.makeText(applicationContext, "You can't delete $activatedKitchenName!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }


    private fun checkIfKitchenIsAtList(kitchenName: String): Boolean {

        val allKitchenList = SharedPrefMenager.getInstance(applicationContext).allKitchenList

        if (allKitchenList == null) {
            return false
        } else {
            allKitchenList.forEach {
                if (it.kitchenName.equals(kitchenName)) {
                    return true
                }
            }
        }
        return false
    }

    private fun saveActivatedKitchenData(kitchenName: String) {
        val kitchenList = SharedPrefMenager.getInstance(applicationContext).userKitchenList
        var kitchenId: Int? = null
        var kitchenPassword: String? = null

        if (kitchenList!!.size != 0) {
            kitchenList.forEach {
                if (kitchenName.equals(it.kitchenName)) {
                    kitchenId = it.kitchenId
                    kitchenPassword = it.kitchenPassword
                }
            }
            SharedPrefMenager.getInstance(applicationContext)
                .saveActivatedKitchenData(kitchenId!!, kitchenName, kitchenPassword!!)
        }
    }

    private fun makeUserKitchenList(): MutableList<String> {
        val header = SharedPrefMenager.getInstance(applicationContext).token
        kitchenApiRequest.getKitchenListForUser(header)

        val sharedPrefMenager = SharedPrefMenager.getInstance(applicationContext)

        //val emptyList = SharedPrefMenager.getInstance(applicationContext).getEmptyListStatus

        mutableUserKitchenNamesList.clear()
        mutableUserKitchenNamesList.add(0, "Choose kitchen")
        mutableUserKitchenIdList.add(0,0)
        val kitchenList = sharedPrefMenager.userKitchenList
        if (kitchenList != null) {
            kitchenList.forEach {
                mutableUserKitchenNamesList.add(it.kitchenName)
                mutableUserKitchenIdList.add(it.kitchenId)
            }
        } else {
            val intent = Intent(applicationContext, KitchenCreatorActivity::class.java)
            startActivity(intent)
        }

        return mutableUserKitchenNamesList
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
    private fun initViews(rootView: View) {

        btnCreate = rootView.findViewById<View>(R.id.create_kitchen) as AppCompatButton
        btnSelect = rootView.findViewById<View>(R.id.select_kitchen) as AppCompatButton
        btnDelete = rootView.findViewById<View>(R.id.delete_kitchen) as AppCompatButton

        btnBack = rootView.findViewById<View>(R.id.btn_back) as Button

        kitchenNameLayout = rootView.findViewById<View>(R.id.kitchen_name_layout) as TextInputLayout
        kitchenPasswordLayout = rootView.findViewById<View>(R.id.password_layout) as TextInputLayout

        kitchenName = rootView.findViewById<View>(R.id.kitchen_name) as TextInputEditText
        kitchenPassword = rootView.findViewById<View>(R.id.kitchen_password) as TextInputEditText

        spinnerMenu = rootView.findViewById<View>(R.id.kitchen_dropdown_list_2) as AppCompatSpinner

    }

    /**
     * Function which initialize button listeners for resending e-mail with confirmation code and for confirming your account.
     */
    private fun initListeners() {
        btnCreate!!.setOnClickListener(this)
        btnSelect!!.setOnClickListener(this)
        btnBack!!.setOnClickListener(this)
        btnDelete!!.setOnClickListener(this)
        spinnerMenu!!.onItemSelectedListener = this
    }

    /**
     * Function which initialize object of InputValidation and KitchenApi Request class.
     */
    private fun initObjects() {
        inputValidation = InputValidation(applicationContext)
        kitchenApiRequest = KitchenApiRequest(applicationContext)
    }


}
