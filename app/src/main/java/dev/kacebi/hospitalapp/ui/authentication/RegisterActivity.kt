package dev.kacebi.hospitalapp.ui.authentication

//    0. Change Email Regex +
//    0.5 Tools Functions +
//    1. Re-Password must be added
//    2. Birth Date Calendar
//    3. Gender (RadioGroup > RadioButtons)
//    4. Sign Up conditions check
//    5. Choose Image (Camera and Gallery)
//    6. ScrollView in Register XML

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import dev.kacebi.hospitalapp.App
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.tools.Tools
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

//        l@k.com 123123
        setListeners()
    }

    private fun setListeners() {
        registerButton.setOnClickListener(this)
    }

    private fun register() {
        val email = emailRegisterEditText.text.toString()
        val password = passwordRegisterEditText.text.toString()
        App.firebase.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                CoroutineScope(Dispatchers.IO).launch {
                    if (task.isSuccessful) {
                        val uid = App.firebase.currentUser!!.uid
                        App.dbRef.document(uid).set(
                            mapOf(
                                "uid" to uid,
                                "email" to email,
                                "user_type" to "patient"
                            )
                        ).await()

                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Register was successful",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Register was unsuccessful",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
    }

    private fun testEmail() {
        val email = emailRegisterEditText
        val password = passwordRegisterEditText
        val firstName = firstNameEditText
        val lastName = lastNameEditText

        Tools.showToast(this,"${Tools.isFieldsNotEmpty(arrayOf(email,password,firstName,lastName))}")
//        Tools.showToast(this, Tools.isEmailValid(email).toString())
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
//            R.id.registerButton -> register()
            R.id.registerButton -> testEmail()
        }
    }
}