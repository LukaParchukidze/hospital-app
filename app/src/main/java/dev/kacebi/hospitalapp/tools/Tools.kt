package dev.kacebi.hospitalapp.tools

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log.d
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import de.hdodenhof.circleimageview.CircleImageView
import dev.kacebi.hospitalapp.App
import dev.kacebi.hospitalapp.R
import kotlinx.android.synthetic.main.dialog_cancel_appointment_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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

    fun isFieldsNotEmpty(editTextArray: Array<EditText>): Boolean {
        var result = true

        for (editText in editTextArray) {
            if (editText.text.isEmpty()) {
                editText.error = "Empty Field"
                result = false
            }
        }
        return result
    }

    fun isPasswordValid(passwordEditText: EditText): Boolean {
        if (passwordEditText.text.toString().length < 6) {
            passwordEditText.error = "Minimum size: 6"
            return false
        }
        return true
    }

    fun isPasswordsEquals(passwordEditText: EditText, rePasswordEditText: EditText): Boolean {
        return if (passwordEditText.text.toString() == rePasswordEditText.text.toString())
            true
        else {
            rePasswordEditText.error = "Re-Password doesn't match"
            false
        }
    }

    fun isTextViewsNotEmpty(pairs: Map<String, TextView>): Boolean {
        var result = true

        for ((key, value) in pairs) {
            if (key.contains("Empty")) {
                value.error = ""
                result = false
            } else
                value.error = null
        }
        return result
    }

    fun isImageViewNotEmpty(image: CircleImageView): Boolean {
        return image.drawable != null
    }

    fun startActivity(context: Context, activity: Activity, isFinished: Boolean) {
        val intent = Intent(context, activity::class.java)

        if (isFinished)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(intent)

        (context as Activity).overridePendingTransition(
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
    }

    fun showSnackbar(
        context: Context,
        rootView: View,
        message: String,
        isContinuable: Boolean,
        actionText: String
    ) {
        val snackbar = Snackbar.make(
            rootView,
            message,
            if (isContinuable)
                Snackbar.LENGTH_INDEFINITE
            else
                Snackbar.LENGTH_LONG
        )

        snackbar.setAction(actionText) {
            snackbar.dismiss()
        }


        snackbar.setActionTextColor(
            ContextCompat.getColor(
                context,
                R.color.snackbar_action_text_color
            )
        )

        snackbar.show()
    }

    fun setSupportActionBar(activity: AppCompatActivity, title: String, isLastName: Boolean, backEnabled: Boolean){
        activity.setSupportActionBar(activity.findViewById(R.id.toolbar))
        d("toolbarYle", title)
        d("toolbarYle", isLastName.toString())
        if (!isLastName)
            activity.findViewById<TextView>(R.id.toolbarTitle).text = title
        else
            activity.findViewById<TextView>(R.id.toolbarTitle).text = "Dr. $title"

        activity.supportActionBar!!.setDisplayShowTitleEnabled(false)
        activity.supportActionBar!!.setDisplayHomeAsUpEnabled(backEnabled)
    }

    fun initDialog(
        activity: AppCompatActivity,
        patientId: String = "",
        doctorId: String = "",
        layout: Int,
        negativeButtonId: Int = 0,
        positiveButtonId: Int = 0,
        status: String = "",
        changeStatus: Boolean
    ): Dialog {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false)
        dialog.setContentView(layout)

        val params: ViewGroup.LayoutParams = dialog.window!!.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = params as WindowManager.LayoutParams

        if (changeStatus)
            changeStatusDialog(dialog, patientId, doctorId, negativeButtonId, positiveButtonId, status)

        return dialog
    }

    private fun changeStatusDialog(dialog: Dialog, patientId: String, doctorId: String, negativeButtonId: Int, positiveButtonId: Int, status: String) {
        val negativeButton = dialog.findViewById<Button>(negativeButtonId)
        val positiveButton = dialog.findViewById<Button>(positiveButtonId)

        positiveButton.setOnClickListener {
            dialog.dismiss()

            CoroutineScope(Dispatchers.IO).launch {
                App.dbDoctors.document(doctorId).collection("patients").document(patientId)
                    .update("status", status).await()
                App.dbUsers.document(patientId).collection("doctors").document(doctorId)
                    .update("status", status).await()
            }
        }
        negativeButton.setOnClickListener {
            dialog.dismiss()
        }
    }

}