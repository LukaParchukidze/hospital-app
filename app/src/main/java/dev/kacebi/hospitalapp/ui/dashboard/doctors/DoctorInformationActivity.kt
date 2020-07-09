package dev.kacebi.hospitalapp.ui.dashboard.doctors

import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log.d
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import dev.kacebi.hospitalapp.App
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.tools.Tools
import dev.kacebi.hospitalapp.ui.chat.ChatActivity
import dev.kacebi.hospitalapp.ui.dashboard.AppointmentTime
import dev.kacebi.hospitalapp.ui.dashboard.AppointmentTimesAdapter
import kotlinx.android.synthetic.main.activity_doctor_information.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.android.synthetic.main.dialog_appointment_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*


class DoctorInformationActivity : AppCompatActivity() {

    private lateinit var doctorId: String
    private lateinit var lastName: String
    private lateinit var doctor: DoctorModel

    private val appointmentTimes = mutableListOf<AppointmentTime>()
    private var adapter: AppointmentTimesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_information)

        Tools.setSupportActionBar(this,"Dr. $lastName")
        doctorId = intent.extras!!.getString("doctorId")!!
        lastName = intent.extras!!.getString("lastName")!!

        getDoctor(doctorId)

        messageDoctorButton.setOnClickListener {
            val intent = Intent(this@DoctorInformationActivity, ChatActivity::class.java)
            intent.putExtra("doctorId", doctorId)
            intent.putExtra("lastName", lastName)
            startActivity(intent)
        }

        bookDoctorButton.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog() {

        if (adapter != null) {
            appointmentTimes.clear()
            adapter!!.notifyDataSetChanged()
        }

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_appointment_layout)

        val params: ViewGroup.LayoutParams = dialog.window!!.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = params as WindowManager.LayoutParams

        dialog.closeDialogButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.applyDialogButton.setOnClickListener {
            if (adapter != null && adapter!!.click != -1) {
                CoroutineScope(Dispatchers.IO).launch {
                    val data = App.dbDoctors.document(doctorId).collection("patients").document(App.auth.uid!!).get().await().data
                    if (data == null) {
                        App.dbDoctors.document(doctorId).collection("patients").document(App.auth.uid!!).set(
                            hashMapOf(
                                "start_time" to appointmentTimes[adapter!!.click].start_time,
                                "end_time" to appointmentTimes[adapter!!.click].end_time
                            )).await()
                        withContext(Dispatchers.Main) {
                            dialog.dismiss()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@DoctorInformationActivity, "You've already been registered", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else {
                dialog.dismiss()
            }
        }

        adapter = AppointmentTimesAdapter(appointmentTimes)
        dialog.appointmentsRecyclerView.adapter = adapter
        dialog.appointmentsRecyclerView.layoutManager = LinearLayoutManager(
            applicationContext,
            LinearLayoutManager.VERTICAL,
            false
        )

        dialog.show()

        dialog.appointmentsProgressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            val size =
                App.dbDoctors.document(doctorId).collection("patients").get().await().size()
            getTimes(doctor.start_time, doctor.end_time, 30)

            if (size == 0) {
                withContext(Dispatchers.Main) {
                    dialog.appointmentsProgressBar.visibility = View.GONE
                    adapter!!.notifyDataSetChanged()
                }
            } else {
                val patients =
                    App.dbDoctors.document(doctorId).collection("patients").get().await()
                for (document in patients.documents) {
                    val start_time = document["start_time"] as String
                    for (i in 0 until appointmentTimes.size) {
                        if (start_time == appointmentTimes[i].start_time) {
                            appointmentTimes[i].available = false
                            break
                        }
                    }
                }
                withContext(Dispatchers.Main) {
                    dialog.appointmentsProgressBar.visibility = View.GONE
                    adapter!!.notifyDataSetChanged()
                }
            }
        }

    }

    // time formatting for appointment management
    private fun getTimes(start_time: String, end_time: String, increment: Int) {
        if (start_time == end_time)
            return
        val df = SimpleDateFormat("HH:mm")
        val d = df.parse(start_time)
        val cal = Calendar.getInstance()
        cal.time = d
        cal.add(Calendar.MINUTE, increment)
        val newTime = df.format(cal.time).toString()
        appointmentTimes.add(AppointmentTime(start_time, newTime))
        getTimes(newTime, end_time, increment)
    }

    private fun getDoctor(doctorId: String) {
        progressBar.visibility = View.VISIBLE
        scrollView.visibility = View.GONE
        CoroutineScope(Dispatchers.IO).launch {
            doctor =
                App.dbDoctors.document(doctorId).get().await()
                    .toObject(DoctorModel::class.java)!!
            val byteArray = App.storage.child("$doctorId.png").getBytes(1024 * 1024L).await()
            val bitmapDrawable = BitmapDrawable(
                resources,
                BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            )
            doctor.drawable = bitmapDrawable

            withContext(Dispatchers.Main) {
                progressBar.visibility = View.GONE
                scrollView.visibility = View.VISIBLE
                doctorImage.setImageDrawable(doctor.drawable)
                specialtyTextView.text = doctor.specialty
                fullNameTextView.text = doctor.full_name
                ageTextView.text = "Age: " + doctor.age.toString()
                emailTextView.text = "Email: " + doctor.email
                workingHoursTextView.text =
                    doctor.start_time + "-" + doctor.end_time + ", Monday to Friday"
                aboutTextView.text = doctor.about
                experienceTextView.text = "Experience: " + doctor.working_experience + " Years"
                phoneNumber1.text = doctor.phone_numbers?.get(0) ?: "NOT AVAILABLE"
                phoneNumber2.text = doctor.phone_numbers?.get(1) ?: "NOT AVAILABLE"

                d("doctorModel", doctor.toString())
            }
        }

    }
}