package dev.kacebi.hospitalapp

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_login.*

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
    }

    private fun changeActivityTo(intent: Intent) {
        startActivity(intent)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tvForgotPassword -> changeActivityTo(Intent(this, ForgotPasswordActivity::class.java))
            R.id.tvSignUp -> changeActivityTo(Intent(this, RegisterActivity::class.java))
        }
    }
}