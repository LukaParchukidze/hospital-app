package dev.kacebi.hospitalapp.ui.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import dev.kacebi.hospitalapp.R

class ForgotPasswordActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
    }

    private fun changeActivityTo(intent: Intent) {
        startActivity(intent)
    }

    override fun onClick(v: View?) {

    }
}