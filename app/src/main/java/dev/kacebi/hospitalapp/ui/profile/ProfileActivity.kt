package dev.kacebi.hospitalapp.ui.profile

import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dev.kacebi.hospitalapp.App
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.file_size_constants.FileSizeConstants
import dev.kacebi.hospitalapp.tools.Tools
import dev.kacebi.hospitalapp.utils.Utils
import dev.kacebi.hospitalapp.ui.authentication.LoginActivity
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.spinkit_loader_layout.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProfileActivity : AppCompatActivity() {

    private var fullName: String? = null
    private var email: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        Tools.setSupportActionBar(this, "Profile", isLastName = false, backEnabled = true)
        toolbar!!.setNavigationOnClickListener {
            onBackPressed()
        }

        fullNameEditText.inputType = InputType.TYPE_NULL
        emailEditText.inputType = InputType.TYPE_NULL
        passwordEditText.inputType = InputType.TYPE_NULL
        rePasswordEditText.inputType = InputType.TYPE_NULL
        getData()
        setUpListeners()

    }

    @Suppress("NAME_SHADOWING")
    private fun getData() {
        spinKitContainerView.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            var byteArray: ByteArray? = null
            val documentSnapshot = App.dbUsers.document(App.auth.uid!!).get().await()
            fullName = documentSnapshot["full_name"] as String?
            if (fullName == null) {
                byteArray = App.storage.child("/doctor_photos/${App.auth.uid!!}.png")
                    .getBytes(FileSizeConstants.THREE_MEGABYTES).await()
                val documentSnapshot = App.dbDoctors.document(App.auth.uid!!).get().await()
                fullName = documentSnapshot["full_name"] as String
                email = documentSnapshot["email"] as String
            } else {
                byteArray = App.storage.child("/patient_photos/${App.auth.uid!!}.png")
                    .getBytes(FileSizeConstants.THREE_MEGABYTES).await()
                fullName = fullName as String
                email = documentSnapshot["email"] as String
            }
            val bitmap = Utils.byteArrayToBitmap(byteArray)
            withContext(Dispatchers.Main) {
                spinKitContainerView.visibility = View.GONE
                profileImageView.setImageBitmap(bitmap)
                fullNameEditText.setText(fullName)
                emailEditText.setText(email)

            }
        }
    }

    private fun disableProfileChange() {
        passwordEditText.inputType = InputType.TYPE_NULL
        rePasswordEditText.inputType = InputType.TYPE_NULL

        editProfileImageButton.visibility = View.VISIBLE
        cancelChangesButton.visibility = View.GONE
        saveChangesButton.visibility = View.GONE

        passwordEditText.text.clear()
        rePasswordEditText.text.clear()
    }

    private fun enableProfileChange() {
        passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        rePasswordEditText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
        rePasswordEditText.transformationMethod = PasswordTransformationMethod.getInstance()

        editProfileImageButton.visibility = View.GONE
        cancelChangesButton.visibility = View.VISIBLE
        saveChangesButton.visibility = View.VISIBLE
    }

    private fun setUpListeners() {
        editProfileImageButton.setOnClickListener {
            enableProfileChange()
        }
        cancelChangesButton.setOnClickListener {
            disableProfileChange()
        }
        saveChangesButton.setOnClickListener {
            val newPassword = passwordEditText.text.toString()
            val newRePassword = rePasswordEditText.text.toString()

            if (newPassword.length >= 6) {
                if (newPassword == newRePassword) {
                    App.auth.currentUser!!.updatePassword(newPassword).addOnSuccessListener {
                        disableProfileChange()

                        Toast.makeText(
                            this,
                            "Data was updated successfully",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                        .addOnFailureListener {
                            Toast.makeText(this, it.message.toString(), Toast.LENGTH_LONG).show()
                            Tools.startActivity(this, LoginActivity(), true)
                        }
                } else {
                    Toast.makeText(this, "Passwords don't match", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Minimum password size is 6", Toast.LENGTH_LONG).show()
            }

        }
    }
}