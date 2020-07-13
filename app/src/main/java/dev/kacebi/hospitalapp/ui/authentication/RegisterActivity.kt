package dev.kacebi.hospitalapp.ui.authentication


import android.R.attr.bitmap
import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log.d
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.documentfile.provider.DocumentFile
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import dev.kacebi.hospitalapp.App
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.extensions.setNewColor
import dev.kacebi.hospitalapp.extensions.setSpannableText
import dev.kacebi.hospitalapp.file_size_constants.FileSizeConstants
import dev.kacebi.hospitalapp.tools.Tools
import dev.kacebi.hospitalapp.tools.Utils
import dev.kacebi.hospitalapp.tools.Utils.bitmapToByteArray
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*


class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val REQUEST_CODE_CAMERA = 10
        const val REQUEST_CODE_GALLERY = 20
    }

    private val calendar = Calendar.getInstance()
    private var birthDate: String = "dateEmpty"
    private var gender: String = "genderEmpty"
    private var imageUri: Uri? = null
    private var takenImage: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        init()
    }

    private fun init() {
        // Calendar (Get Date function)
        pickDate()

        // Sign Up TextView on the bottom
        setUpSignUpTextView()

        // OnClickListeners Initialization
        setListeners()

        // Bottom Sheet Dialog for Image Upload (Camera/Gallery)
        imageUpload.setOnClickListener {
            bottomSheetDialog()
        }
    }

    // Bottom Sheet Dialog for Images
    private fun bottomSheetDialog() {
        val dialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val bottomSheetView = View.inflate(this, R.layout.dialog_bottom_sheet_layout, null)

        bottomSheetView.findViewById<LinearLayout>(R.id.openCameraBottomSheet).setOnClickListener {
            openCamera()
            dialog.dismiss()
        }

        bottomSheetView.findViewById<LinearLayout>(R.id.openGalleryBottomSheet).setOnClickListener {
            openGallery()
            dialog.dismiss()
        }

        dialog.setContentView(bottomSheetView)
        dialog.show()
    }

    // Must be take out in Tools/Utils
    // Open Camera Function
    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (takePictureIntent.resolveActivity(this.packageManager) != null)
            startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA)
        else
            Tools.showToast(this, "Unable to open Camera")
    }

    // Must be take out in Tools/Utils
    // Open Gallery Function
    private fun openGallery() {
        val gallery = Intent();
        gallery.type = "image/*";
        gallery.action = Intent.ACTION_GET_CONTENT;

        startActivityForResult(
            Intent.createChooser(gallery, "Select Picture"),
            REQUEST_CODE_GALLERY
        )
    }

    private fun changeUIAfterImageUpload() {
        chooserImageView.visibility = View.GONE
        uploadedImageCircleImageView.visibility = View.VISIBLE
        uploadImageTextView.text = getString(R.string.change_image)
        uploadImageTextView.setNewColor(this, R.color.edittexts_hint_color)
        uploadImageTextView.error = null
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
        birthDate = selectedDateTextView.text.toString()
        d("dateTest", birthDate)
    }

    private fun register() {
        val firstName = firstNameEditText
        val lastName = lastNameEditText
        val email = emailRegisterEditText
        val password = passwordRegisterEditText
        val rePassword = rePasswordRegisterEditText

        if (Tools.isFieldsNotEmpty(arrayOf(firstName, lastName, email, password, rePassword))) {
            val isEmailValid = Tools.isEmailValid(email)
            val isPasswordValid = Tools.isPasswordValid(password)

            if (isEmailValid && isPasswordValid) {
                if (Tools.isPasswordsEquals(password, rePassword)) {
                    if (Tools.isTextViewsNotEmpty(
                            mapOf(
                                birthDate to (findViewById(R.id.birthDateTextView)),
                                gender to (findViewById(R.id.genderTextView))
                            )
                        )
                    ) {
                        if (Tools.isImageViewNotEmpty(uploadedImageCircleImageView)) {

                            App.auth.createUserWithEmailAndPassword(
                                email.text.toString(),
                                password.text.toString()
                            ).addOnCompleteListener(this) { task ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    if (task.isSuccessful) {
                                        val uid = App.auth.currentUser!!.uid
                                        App.dbUsers.document(uid).set(
                                            mapOf(
                                                "email" to email.text.toString(),
                                                "user_type" to "patient",
                                                "first_name" to firstName.text.toString(),
                                                "last_name" to lastName.text.toString(),
                                                "full_name" to "${firstName.text} ${lastName.text}",
                                                "gender" to gender,
                                                "birth_date" to birthDate
                                            )
                                        ).await()

                                        if (imageUri != null) {
                                            App.storage.child("/patient_photos/$uid.png")
                                                .putFile(imageUri!!)
                                                .await()
                                        } else {
                                            App.storage.child("/patient_photos/$uid.png")
                                                .putBytes(bitmapToByteArray(takenImage!!))
                                                .await()
                                        }

                                        withContext(Dispatchers.Main) {
                                            App.auth.signOut()

                                            Tools.showSnackbar(
                                                this@RegisterActivity,
                                                registerRoot,
                                                "Register was successful", false,
                                                "Close"
                                            )

                                            delay(2000)

                                            Tools.startActivity(
                                                this@RegisterActivity,
                                                LoginActivity(),
                                                true
                                            )
                                        }
                                    } else {
                                        withContext(Dispatchers.Main) {
                                            Tools.showSnackbar(
                                                this@RegisterActivity,
                                                registerRoot,
                                                "Email already registered", true, "Close"
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            uploadImageTextView.error = ""
                            uploadImageTextView.setNewColor(this, R.color.errorColor)
                        }
                    }
                }
            }
        } else {
            Tools.showSnackbar(this, registerRoot, "Fill in all fields", false, "Close")
        }
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

    // OnClickListeners Initialization
    private fun setListeners() {
        registerButton.setOnClickListener(this)
        maleRadioButton.setOnClickListener(this)
        femaleRadioButton.setOnClickListener(this)
        signInTextView.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.registerButton -> register()
            R.id.maleRadioButton -> gender = "Male"
            R.id.femaleRadioButton -> gender = "Female"
            R.id.signInTextView -> Tools.startActivity(this, LoginActivity(), true)
        }
    }


    // Get image and set to ImageView
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Get Image from Camera
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            takenImage = data!!.extras!!.get("data") as Bitmap
            uploadedImageCircleImageView.setImageBitmap(takenImage)
            imageUri = null
            changeUIAfterImageUpload()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
        // Get Image from Gallery
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            imageUri = data.data
            val imageSize = DocumentFile.fromSingleUri(
                this@RegisterActivity,
                imageUri!!
            )!!.length()
            if (imageSize <= FileSizeConstants.THREE_MEGABYTES) {
                Glide.with(this).load(imageUri).into(uploadedImageCircleImageView)
                changeUIAfterImageUpload()
            } else {
                Toast.makeText(
                    this@RegisterActivity,
                    "The file size limit is 3 MB",
                    Toast.LENGTH_LONG
                ).show()
                imageUri = null
            }
            takenImage = null
        }
    }

}