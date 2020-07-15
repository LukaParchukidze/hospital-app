package dev.kacebi.hospitalapp.ui.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import dev.kacebi.hospitalapp.App
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.file_size_constants.FileSizeConstants
import dev.kacebi.hospitalapp.tools.Tools
import dev.kacebi.hospitalapp.ui.authentication.LoginActivity
import dev.kacebi.hospitalapp.utils.Utils
import dev.kacebi.hospitalapp.utils.Utils.bitmapToByteArray
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.spinkit_loader_layout.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProfileActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val REQUEST_CODE_CAMERA = 10
        const val REQUEST_CODE_GALLERY = 20
    }

    private var fullName: String? = null
    private var email: String? = null
    private var birthDate: String? = null
    private var age: String? = null
    private var imageUri: Uri? = null
    private var takenImage: Bitmap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        init()
        setUpListeners()

    }

    private fun init() {

        Tools.setSupportActionBar(this, "Profile", isLastName = false, backEnabled = true)

        toolbar!!.setNavigationOnClickListener {
            onBackPressed()
        }

        fullNameEditText.inputType = InputType.TYPE_NULL
        emailEditText.inputType = InputType.TYPE_NULL
        birthDateEditText.inputType = InputType.TYPE_NULL
        passwordEditText.inputType = InputType.TYPE_NULL
        rePasswordEditText.inputType = InputType.TYPE_NULL
        profileImageView.isEnabled = false

        getData()
    }

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

    @Suppress("NAME_SHADOWING")
    private fun getData() {
        spinKitContainerView.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            val byteArray: ByteArray?
            val documentSnapshot = App.dbUsers.document(App.auth.uid!!).get().await()
            fullName = documentSnapshot["full_name"] as String?
            if (fullName == null) {
                byteArray = App.storage.child("/doctor_photos/${App.auth.uid!!}.png")
                    .getBytes(FileSizeConstants.THREE_MEGABYTES).await()
                val documentSnapshot = App.dbDoctors.document(App.auth.uid!!).get().await()
                fullName = documentSnapshot["full_name"] as String
                email = documentSnapshot["email"] as String
                age = (documentSnapshot["age"] as Long).toString()
            } else {
                byteArray = App.storage.child("/patient_photos/${App.auth.uid!!}.png")
                    .getBytes(FileSizeConstants.THREE_MEGABYTES).await()
                fullName = fullName as String
                email = documentSnapshot["email"] as String
                birthDate = documentSnapshot["birth_date"] as String
            }
            val bitmap = Utils.byteArrayToBitmap(byteArray)
            withContext(Dispatchers.Main) {
                spinKitContainerView.visibility = View.GONE
                profileImageView.setImageBitmap(bitmap)
                fullNameEditText.setText(fullName)
                emailEditText.setText(email)

                if (age == null) {
                    birthDateEditText.setText(birthDate)
                } else
                    birthDateEditText.setText("$age years old")
            }
        }
    }

    private fun enableProfileChange() {
        passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        rePasswordEditText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
        rePasswordEditText.transformationMethod = PasswordTransformationMethod.getInstance()

        editProfileImageButton.visibility = View.GONE
        cancelChangesButton.visibility = View.VISIBLE
        saveChangesButton.visibility = View.VISIBLE
        profileImageView.isEnabled = true
    }

    private fun disableProfileChange() {
        passwordEditText.inputType = InputType.TYPE_NULL
        rePasswordEditText.inputType = InputType.TYPE_NULL

        editProfileImageButton.visibility = View.VISIBLE
        cancelChangesButton.visibility = View.GONE
        saveChangesButton.visibility = View.GONE
        profileImageView.isEnabled = false

        passwordEditText.text.clear()
        rePasswordEditText.text.clear()
    }

    private fun setUpListeners() {
        editProfileImageButton.setOnClickListener(this)
        cancelChangesButton.setOnClickListener(this)
        saveChangesButton.setOnClickListener(this)
        profileImageView.setOnClickListener(this)
    }

    private fun sendImageToServer() {
        CoroutineScope(Dispatchers.IO).launch {
            val uid = App.auth.currentUser!!.uid

            if (imageUri != null) {
                App.storage.child("/patient_photos/$uid.png")
                    .putFile(imageUri!!)
                    .await()
            } else if (takenImage != null) {
                App.storage.child("/patient_photos/$uid.png")
                    .putBytes(bitmapToByteArray(takenImage!!))
                    .await()
            }
        }
    }

    private fun checkConditions() {

        val newPassword = passwordEditText
        val newRePassword = rePasswordEditText

        if ((newPassword.text.toString().isEmpty() && newRePassword.text.toString()
                .isEmpty()) && (takenImage == null && imageUri == null)
        ) {
            disableProfileChange()
        } else if ((newPassword.text.toString().isEmpty() && newRePassword.text.toString()
                .isEmpty()) && (takenImage != null || imageUri != null)
        ) {
            sendImageToServer()
            disableProfileChange()
            Tools.showSnackbar(this, profileRoot, "Image has been updated", false, "Cancel")
        } else if ((newPassword.text.toString().isNotEmpty() || newRePassword.text.toString()
                .isNotEmpty()) && (takenImage == null && imageUri == null)
        ) {
            if (Tools.isPasswordValid(newPassword)) {
                if (Tools.isPasswordsEquals(newPassword, newRePassword)) {
                    updatePassword(newPassword, false)
                }
            }
        } else if ((newPassword.text.toString().isNotEmpty() && newRePassword.text.toString()
                .isNotEmpty()) && (takenImage != null || imageUri != null)
        ) {
            if (Tools.isPasswordValid(newPassword)) {
                if (Tools.isPasswordsEquals(newPassword, newRePassword)) {
                    updatePassword(newPassword, true)
                }
            }
        }

    }

    private fun updatePassword(newPassword: EditText, sendImage: Boolean) {
        App.auth.currentUser!!.updatePassword(newPassword.text.toString())
            .addOnSuccessListener {
                disableProfileChange()

                if (sendImage)
                    sendImageToServer()

                Tools.showSnackbar(
                    this,
                    profileRoot,
                    if (sendImage)
                        "Password and Image updated successfully"
                    else
                        "Password updated successfully",
                    false,
                    "Close"
                )

            }
            .addOnFailureListener {
                Tools.showToast(
                    this,
                    it.message.toString()
                )

                Tools.startActivity(this, LoginActivity(), true)

            }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.editProfileImageButton -> {
                v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.click))
                enableProfileChange()
            }
            R.id.cancelChangesButton -> {
                v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.click))
                disableProfileChange()
            }
            R.id.saveChangesButton -> checkConditions()
            R.id.profileImageView -> bottomSheetDialog()
        }
    }

    // Get image and set to ImageView
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Get Image from Camera
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            takenImage = data!!.extras!!.get("data") as Bitmap
            profileImageView.setImageBitmap(takenImage)
            imageUri = null
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
        // Get Image from Gallery
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            imageUri = data.data
            val imageSize = DocumentFile.fromSingleUri(
                this,
                imageUri!!
            )!!.length()
            if (imageSize <= FileSizeConstants.THREE_MEGABYTES) {
                Glide.with(this).load(imageUri).into(profileImageView)
            } else {
                Tools.showToast(this, "The file size limit is 3 MB")
                imageUri = null
            }
            takenImage = null
        }
    }
}