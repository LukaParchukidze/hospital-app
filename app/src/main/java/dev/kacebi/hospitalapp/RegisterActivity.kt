package dev.kacebi.hospitalapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
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
        setListeners()
    }

    private fun setListeners() {
        btnRegister.setOnClickListener(this)
    }

    private fun register() {
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
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
            R.id.btnRegister -> register()
        }
    }
}


// emailRegisterEditText.setCompoundDrawablesWithIntrinsicBounds(
//                ContextCompat.getDrawable(
//                    this,
//                    R.drawable.ic_mail
//                ), null, ContextCompat.getDrawable(this, R.drawable.ic_mail), null
//            )