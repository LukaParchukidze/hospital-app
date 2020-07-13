package dev.kacebi.hospitalapp.ui.authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import dev.kacebi.hospitalapp.App
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.tools.Tools
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.coroutines.*

class ForgotPasswordActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        setListeners()
    }

    private fun resetPassword() {
        val email = forgotPasswordEmailEditText

        if (Tools.isFieldsNotEmpty(arrayOf(email))) {
            if (Tools.isEmailValid(email)) {
                App.auth.sendPasswordResetEmail(email.text.toString())
                    .addOnCompleteListener { task ->
                        CoroutineScope(Dispatchers.IO).launch {
                            if (task.isSuccessful) {

                                withContext(Dispatchers.Main) {
                                    Tools.showSnackbar(
                                        this@ForgotPasswordActivity,
                                        forgotRoot,
                                        "Redirecting to Gmail...",
                                        true,
                                        "Cancel"
                                    )

                                    delay(3000)

                                    val intent =
                                        this@ForgotPasswordActivity.packageManager.getLaunchIntentForPackage(
                                            "com.google.android.gm"
                                        )

                                    if (intent != null) {
                                        startActivity(intent)
                                        finish()
                                        this@ForgotPasswordActivity.overridePendingTransition(
                                            android.R.anim.fade_in,
                                            android.R.anim.fade_out
                                        )
                                    } else
                                        Tools.showSnackbar(
                                            this@ForgotPasswordActivity,
                                            forgotRoot,
                                            "Redirecting Failed",
                                            false,
                                            "Cancel"
                                        )
                                }
                            } else {
                                Tools.showSnackbar(
                                    this@ForgotPasswordActivity,
                                    forgotRoot,
                                    "Email not registered",
                                    false,
                                    "Cancel"
                                )
                            }
                        }

                    }
            }
        }

    }

    private fun setListeners() {
        forgotPasswordButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.forgotPasswordButton -> resetPassword()
        }
    }
}