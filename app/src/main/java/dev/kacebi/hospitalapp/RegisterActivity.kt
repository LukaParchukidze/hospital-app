package dev.kacebi.hospitalapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        registerButton.setOnClickListener {
            emailRegisterEditText.setError("Invalid")

            passwordRegisterEditText.setError("Invalid")
        }

    }
}


// emailRegisterEditText.setCompoundDrawablesWithIntrinsicBounds(
//                ContextCompat.getDrawable(
//                    this,
//                    R.drawable.ic_mail
//                ), null, ContextCompat.getDrawable(this, R.drawable.ic_mail), null
//            )