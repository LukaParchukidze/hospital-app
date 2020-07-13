package dev.kacebi.hospitalapp.ui.authentication

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import dev.kacebi.hospitalapp.App
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.extensions.setSpannableText
import dev.kacebi.hospitalapp.tools.Tools
import dev.kacebi.hospitalapp.ui.doctors_dashboard.DoctorDashboardActivity
import dev.kacebi.hospitalapp.ui.patients_dashboard.PatientDashboardActivity
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

        setListeners()
        setUpSignUpTextView()
    }

    private fun setListeners() {
        forgotPasswordTextView.setOnClickListener(this)
        signUpTextView.setOnClickListener(this)
        loginButton.setOnClickListener(this)
    }

    private fun checkConditions() {
        val email = emailLoginEditText
        val password = passwordLoginEditText

        if (Tools.isFieldsNotEmpty(arrayOf(email, password))) {
            val isEmailValid = Tools.isEmailValid(email)
            val isPasswordValid = Tools.isPasswordValid(password)

            if (isEmailValid && isPasswordValid) {
                login(email, password)
            }
        }
    }

    private fun login(email: EditText, password: EditText) {
        App.auth.signInWithEmailAndPassword(
            email.text.toString(),
            password.text.toString()
        )
            .addOnCompleteListener(this) { task ->
                CoroutineScope(Dispatchers.IO).launch {
                    if (task.isSuccessful) {
                        val uid = App.auth.currentUser!!.uid
                        val userType = App.dbUsers.document(uid).get().await()["user_type"]

                        withContext(Dispatchers.Main) {
                            if (userType != null) {
                                Tools.startActivity(
                                    this@LoginActivity,
                                    PatientDashboardActivity(), true
                                )
                            } else {
                                Tools.startActivity(
                                    this@LoginActivity,
                                    DoctorDashboardActivity(), true
                                )
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Tools.showSnackbar(
                                this@LoginActivity,
                                loginRoot,
                                "Credentials was incorrect", false,
                                "Close"
                            )
                        }
                    }
                }
            }
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

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.forgotPasswordTextView -> Tools.startActivity(
                this@LoginActivity,
                ForgotPasswordActivity(), true
            )
            R.id.signUpTextView -> Tools.startActivity(this@LoginActivity, RegisterActivity(), true)
            R.id.loginButton -> checkConditions()
        }
    }
}