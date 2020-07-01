package dev.kacebi.hospitalapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
        tvSignUp.setSpannableText("${getString(R.string.no_account)} ", Color.BLACK)
        tvSignUp.setSpannableText(getString(R.string.sign_up), ContextCompat.getColor(this, R.color.colorPrimary))
    }

    private fun setListeners() {
        tvForgotPassword.setOnClickListener(this)
        tvSignUp.setOnClickListener(this)
        btnLogin.setOnClickListener(this)
    }

    private fun changeActivityTo(intent: Intent) {
        startActivity(intent)
    }

    private fun login() {
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        App.firebase.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                CoroutineScope(Dispatchers.IO).launch {
                    if (task.isSuccessful) {
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
                                Toast.makeText(this@LoginActivity, "Doctor Here", Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                    }
                    else {
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

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tvForgotPassword -> changeActivityTo(
                Intent(
                    this,
                    ForgotPasswordActivity::class.java
                )
            )
            R.id.tvSignUp -> changeActivityTo(Intent(this, RegisterActivity::class.java))
            R.id.btnLogin -> login()
        }
    }
}