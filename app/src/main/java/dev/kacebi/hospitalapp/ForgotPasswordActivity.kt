package dev.kacebi.hospitalapp

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        setUpSignUpTextView()
        setUpListeners()
    }

    private fun setUpSignUpTextView() {
        tvSignUp.setSpannableText("${getString(R.string.no_account)} ", Color.BLACK)
        tvSignUp.setSpannableText(getString(R.string.sign_up), ContextCompat.getColor(this, R.color.colorPrimary))
    }

    private fun setUpListeners() {
        tvSignUp.setOnClickListener(this)
    }

    private fun changeActivityTo(intent: Intent) {
        startActivity(intent)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tvSignUp -> changeActivityTo(Intent(this, RegisterActivity::class.java))
        }
    }
}