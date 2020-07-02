package dev.kacebi.hospitalapp.ui.authentication


import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import com.bumptech.glide.Glide
import dev.kacebi.hospitalapp.App
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.extensions.setSpannableText
import dev.kacebi.hospitalapp.tools.Tools
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val REQUEST_CODE_CAMERA = 10
    }

    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

//        l@k.com 123123
        init()

    }

    private fun init() {
        // Calendar (Get Date function)
        pickDate()

        // Sign Up TextView on the bottom
        setUpSignUpTextView()

        // OnClickListeners Initialization
        setListeners()

        // Open Camera
        uploadPicture.setOnClickListener {
            openCamera()
        }
    }

    private fun register() {
        val email = emailRegisterEditText.text.toString()
        val password = passwordRegisterEditText.text.toString()
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

    // Open Camera Function
    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (takePictureIntent.resolveActivity(this.packageManager) != null)
            startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA)
        else
            Tools.showToast(this, "Unable to open Camera")
    }

    // Get Date Function
    private fun pickDate() {
        val dateSetListener =
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }

        chooseDate.setOnClickListener {
            DatePickerDialog(
                this@RegisterActivity,
                R.style.DatePickerDialogTheme,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    // Update UI After Getting Date Function
    private fun updateDateInView() {
        val dateFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(dateFormat, Locale.US)
        selectedDateTextView.text = sdf.format(calendar.time)
    }

    // Setup Sign Up TextView on the bottom of View
    private fun setUpSignUpTextView() {
        signInTextView.setSpannableText("${getString(R.string.have_an_accout)} ", Color.BLACK)
        signInTextView.setSpannableText(
            getString(R.string.log_in),
            ContextCompat.getColor(
                this,
                R.color.colorPrimary
            )
        )
    }

    // For Test
    private fun testEmail() {
        val firstName = firstNameEditText
        val lastName = lastNameEditText
        val email = emailRegisterEditText
        val password = passwordRegisterEditText
        val rePassword = rePasswordRegisterEditText

        Tools.showToast(
            this,
            "${Tools.isFieldsNotEmpty(arrayOf(firstName, lastName, email, password, rePassword))}"
        )
//        Tools.showToast(this, Tools.isEmailValid(email).toString())
    }

    // OnClickListeners Initialization
    private fun setListeners() {
        registerButton.setOnClickListener(this)
        signInTextView.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
//            R.id.registerButton -> register()
            R.id.registerButton -> testEmail()
            R.id.signInTextView -> Tools.startActivity(this, LoginActivity())
        }
    }


    // Get image and set to ImageView
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Get Image from Camera
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            val takenImage = data!!.extras!!.get("data") as Bitmap
            cameraCircleImageView.setImageBitmap(takenImage)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}