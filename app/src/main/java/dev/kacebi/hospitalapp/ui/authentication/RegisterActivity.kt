package dev.kacebi.hospitalapp.ui.authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import dev.kacebi.hospitalapp.App
import dev.kacebi.hospitalapp.R
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

//        btnRegister.setOnClickListener {
//            emailRegisterEditText.error = "Invalid"
//
//            passwordRegisterEditText.error = "Invalid"
//        }

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

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.registerButton -> register()
        }
    }
}


// emailRegisterEditText.setCompoundDrawablesWithIntrinsicBounds(
//                ContextCompat.getDrawable(
//                    this,
//                    R.drawable.ic_mail
//                ), null, ContextCompat.getDrawable(this, R.drawable.ic_mail), null
//            )