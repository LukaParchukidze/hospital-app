package dev.kacebi.hospitalapp.ui.authentication

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log.d
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import dev.kacebi.hospitalapp.*
import dev.kacebi.hospitalapp.extensions.setSpannableText
import dev.kacebi.hospitalapp.tools.Tools
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setUpSignUpTextView()
        setListeners()
    }

    private fun setUpSignUpTextView() {
        signUpTextView.setSpannableText("${getString(R.string.no_account)} ", Color.BLACK)
        signUpTextView.setSpannableText(
            getString(R.string.sign_up),
            ContextCompat.getColor(
                this,
                R.color.colorPrimary
            )
        )
    }

    private fun setListeners() {
        forgotPasswordTextView.setOnClickListener(this)
        signUpTextView.setOnClickListener(this)
        loginButton.setOnClickListener(this)
    }

    private fun changeActivityTo(intent: Intent) {
        startActivity(intent)
    }

    private fun login() {
        val email = emailLoginEditText
        val password = passwordLoginEditText

        if (Tools.isFieldsNotEmpty(arrayOf(email, password))) {
            val isEmailValid = Tools.isEmailValid(email)
            val isPasswordValid = Tools.isPasswordValid(password)

            if (isEmailValid && isPasswordValid) {
                App.firebase.signInWithEmailAndPassword(
                    email.text.toString(),
                    password.text.toString()
                )
                    .addOnCompleteListener(this) { task ->
                        CoroutineScope(Dispatchers.IO).launch {
                            if (task.isSuccessful) {
                                d("isSuccessful", " YES")
                                val uid = App.firebase.currentUser!!.uid

                                val userType = App.dbRef.document(uid).get().await()["user_type"]
                                withContext(Dispatchers.Main) {
                                    if (userType == "patient") {
                                        Toast.makeText(
                                            this@LoginActivity,
                                            "Patient Here",
                                            Toast.LENGTH_LONG
                                        )
                                            .show()
                                    } else {
                                        Toast.makeText(
                                            this@LoginActivity,
                                            "Doctor Here",
                                            Toast.LENGTH_LONG
                                        )
                                            .show()
                                    }
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Log in was unsuccessful",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                }

                            }
                        }
                    }
            } else
                return
        }

        Tools.showToast(this, "Oops, I am Here")

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.forgotPasswordTextView -> changeActivityTo(
                Intent(
                    this,
                    ForgotPasswordActivity::class.java
                )
            )
            R.id.signUpTextView -> changeActivityTo(Intent(this, RegisterActivity::class.java))
            R.id.loginButton -> login()
        }
    }
}