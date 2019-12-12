package com.frorage.frontend

import android.content.Context
import android.widget.EditText
import androidx.appcompat.widget.AppCompatCheckBox
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class InputValidation (private val context: Context){

    fun isInputEditTextFilled(textInputEditText: EditText, textInputLayout: TextInputLayout, message: String): Boolean {
        val value = textInputEditText.text.toString().trim()
        if (value.isEmpty()) {
            textInputLayout.error = message
            //hideKeyboardFrom(textInputEditText)
            return false
        } else {
            textInputLayout.isErrorEnabled = false
        }

        return true
    }

    fun isInputEditTextEmail(textInputEditText: TextInputEditText, textInputLayout: TextInputLayout, message: String): Boolean {
        val value = textInputEditText.text.toString().trim()
        if (value.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            textInputLayout.error = message
            //hideKeyboardFrom(textInputEditText)
            return false
        } else {
            textInputLayout.isErrorEnabled = false
        }
        return true
    }

    fun isInputEditTextName(textInputEditText: TextInputEditText, textInputLayout: TextInputLayout, message: String): Boolean{
        val nameRegex = """^[a-zA-Z]+(([',._-][a-zA-Z ])?[a-zA-Z]*)*${'$'}""".toRegex()
        val value = textInputEditText.text.toString().trim()
        if(value.isEmpty() || !nameRegex.matches(value)){
            textInputLayout.error = message
        }else{
            textInputLayout.isErrorEnabled = false
        }
        return true
    }

    fun isInputEditPassword(textInputEditText: TextInputEditText, textInputLayout: TextInputLayout, message: String): Boolean{
        val passwordRegex = """^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#${'$'}%!\-_?&])(?=\S+${'$'}).{8,20}""".toRegex()
        val value = textInputEditText.text.toString().trim()
        if (value.isEmpty() || !passwordRegex.matches(value)){
            textInputLayout.error = message
            return false
        }else{
            textInputLayout.isErrorEnabled = false
        }

        return true
    }

    fun isInputEditTextMatches(textInputEditText1: TextInputEditText, textInputEditText2: TextInputEditText, textInputLayout: TextInputLayout, message: String): Boolean {
        val value1 = textInputEditText1.text.toString().trim()
        val value2 = textInputEditText2.text.toString().trim()
        if (!value1.contentEquals(value2)) {
            textInputLayout.error = message
           // hideKeyboardFrom(textInputEditText2)
            return false
        } else {
            textInputLayout.isErrorEnabled = false
        }
        return true
    }


    fun isCheckboxChecked(checkBox: AppCompatCheckBox, textInputLayout: TextInputLayout, message: String): Boolean{
        val value = checkBox.isChecked

        if(!value){
            textInputLayout.error = message
            return false
        }else{
            textInputLayout.isErrorEnabled = false
        }
        return true
    }
}