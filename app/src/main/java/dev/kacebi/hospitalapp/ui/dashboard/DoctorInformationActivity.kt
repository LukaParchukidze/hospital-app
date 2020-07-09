package dev.kacebi.hospitalapp.ui.dashboard

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log.d
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import dev.kacebi.hospitalapp.App
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.ui.chat.ChatActivity
import kotlinx.android.synthetic.main.activity_doctor_information.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DoctorInformationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_information)

        val doctorId = intent.extras!!.getString("doctorId")!!
        val lastName = intent.extras!!.getString("lastName")!!
        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar!!.title = "Dr. $lastName"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        getDoctor(doctorId)

        messageDoctorButton.setOnClickListener {
            val intent = Intent(this@DoctorInformationActivity, ChatActivity::class.java)
            intent.putExtra("doctorId", doctorId)
            intent.putExtra("lastName", lastName)
            startActivity(intent)
        }
    }

    private fun getDoctor(doctorId: String) {
        progressBar.visibility = View.VISIBLE
        scrollView.visibility = View.GONE
        CoroutineScope(Dispatchers.IO).launch {
            val doctor = App.dbDoctors.document(doctorId).get().await().toObject(DoctorModel::class.java)
            val byteArray = App.storage.child("$doctorId.png").getBytes(1024 * 1024L).await()
            val bitmapDrawable = BitmapDrawable(resources, BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size))
            doctor!!.drawable = bitmapDrawable

            withContext(Dispatchers.Main) {
                progressBar.visibility = View.GONE
                scrollView.visibility = View.VISIBLE
                doctorImage.setImageDrawable(doctor.drawable)
                specialtyTextView.text = doctor.specialty
                fullNameTextView.text = doctor.full_name
                ageTextView.text = "Age: " + doctor.age.toString()
                emailTextView.text = "Email: " + doctor.email
                workingHoursTextView.text = doctor.start_time + "-" + doctor.end_time + ", Monday to Friday"
                aboutTextView.text = doctor.about
                experienceTextView.text = "Experience: " + doctor.working_experience + " Years"
                phoneNumber1.text = doctor.phone_numbers?.get(0) ?: "NOT AVAILABLE"
                phoneNumber2.text = doctor.phone_numbers?.get(1) ?: "NOT AVAILABLE"

                d("doctorModel", doctor.toString())
            }
        }

    }
}