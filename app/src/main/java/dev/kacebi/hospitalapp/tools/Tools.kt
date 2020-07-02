package dev.kacebi.hospitalapp.tools

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.EditText
import android.widget.Toast
import dev.kacebi.hospitalapp.ui.authentication.LoginActivity
import java.util.regex.Pattern.compile

object Tools {
    fun isEmailValid(emailEditText: EditText): Boolean {
        val email = emailEditText.text

        val emailRegex = compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )

        return if (!emailRegex.matcher(email).matches()) {
            emailEditText.error = "Invalid Email"
            false
        } else
            true

    }

    fun showToast(context: Context, text: String) {
        return Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    fun isFieldsNotEmpty(editTextArray: Array<EditText>): Boolean{
        var result = true

        for(editText in editTextArray){
            if(editText.text.isEmpty()){
                editText.error = "Empty Field"
                result = false
            }
        }
        return result
    }

    fun isPasswordValid(passwordEditText: EditText): Boolean {
        if(passwordEditText.text.toString().length < 6){
            passwordEditText.error = "Minimum size: 6"
            return false
        }
        return true
    }

    fun startActivity(context: Context, activity: Activity){
        val intent = Intent(context, activity::class.java)
        context.startActivity(intent)
    }

}