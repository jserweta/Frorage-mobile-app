package com.frorage.frontend.activities

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.frorage.frontend.InputValidation
import com.frorage.frontend.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class KitchenCreator : AppCompatActivity() /*View.OnClickListener*/{

    /*//private var btnBack: ImageButton? = null
    private var btnCreateOrJoin: AppCompatButton? = null
    private var doCreate: CheckBox? = null

    private var kitchenNameLayout: TextInputLayout? = null
    private var kitchenPasswordLayout: TextInputLayout? = null
    private var kitchenName: TextInputEditText? = null
    private var kitchenPassword: TextInputEditText? = null

    private lateinit var inputValidation: InputValidation*/


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kitchen_creator)

        /*initViews()

        initListeners()

        initObjects()*/
    }

   /* private fun initObjects() {
        inputValidation = InputValidation(this@KitchenCreator)
    }

    private fun initListeners() {
      //  btnBack!!.setOnClickListener(this)
        btnCreateOrJoin!!.setOnClickListener(this)
    }

    private fun initViews() {

       // btnBack = findViewById<View>(R.id.btn_back) as ImageButton
        btnCreateOrJoin = findViewById<View>(R.id.btn_let_frorage) as AppCompatButton
        doCreate = findViewById<View>(R.id.create_kitchen) as CheckBox

        kitchenNameLayout = findViewById<View>(R.id.kitchen_name_layout) as TextInputLayout
        kitchenPasswordLayout = findViewById<View>(R.id.password_layout) as TextInputLayout

        kitchenName = findViewById<View>(R.id.kitchen_name) as TextInputEditText
        kitchenPassword = findViewById<View>(R.id.kitchen_password) as TextInputEditText

    }

    override fun onClick(v: View) {
        *//*when(v.id){
            R.id.btn_let_frorage ->{
                verifyInputData()
            }
        }*//*
    }

    private fun verifyInputData() {

    }*/

}
