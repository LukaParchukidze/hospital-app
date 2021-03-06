package dev.kacebi.hospitalapp.ui.patients_dashboard.doctors.full_information

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log.d
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import dev.kacebi.hospitalapp.App
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.file_size_constants.FileSizeConstants
import dev.kacebi.hospitalapp.tools.Tools
import dev.kacebi.hospitalapp.utils.Utils
import dev.kacebi.hospitalapp.ui.chat.activities.ChatActivity
import dev.kacebi.hospitalapp.ui.patients_dashboard.doctors.full_information.models.AppointmentTimeModel
import dev.kacebi.hospitalapp.ui.patients_dashboard.doctors.full_information.models.DoctorModel
import kotlinx.android.synthetic.main.activity_doctor_information.*
import kotlinx.android.synthetic.main.dialog_appointment_layout.*
import kotlinx.android.synthetic.main.toolbar_layout.*
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

    private val appointmentTimes = mutableListOf<AppointmentTimeModel>()
    private var adapter: AppointmentTimesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_information)

        init()
    }

    private fun init() {
        lastName = intent.extras!!.getString("lastName", "")

        Tools.setSupportActionBar(this, lastName, isLastName = true, backEnabled = true)
        toolbar!!.setNavigationOnClickListener {
            onBackPressed()
        }

        doctorId = intent.extras!!.getString("doctorId", "")

        getDoctor(doctorId)

        messageDoctorButton.setOnClickListener {
            val intent = Intent(this@DoctorInformationActivity, ChatActivity::class.java)
            intent.putExtra("id", doctorId)
            intent.putExtra("name", lastName)
            startActivity(intent)
        }

        bookDoctorButton.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog() {

        // Disable book and message buttons
        bookDoctorButton.isEnabled = false
        messageDoctorButton.isEnabled = false

        if (adapter != null) {
            appointmentTimes.clear()
            adapter!!.notifyDataSetChanged()
        }

        // Init dialog
        val dialog =
            Tools.initDialog(
                this,
                layout = R.layout.dialog_appointment_layout,
                changeStatus = false
            )

        dialog.closeDialogButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.applyDialogButton.setOnClickListener {
            if (adapter != null && adapter!!.click != -1) {
                dialog.closeDialogButton.isEnabled = false
                dialog.applyDialogButton.isEnabled = false

                CoroutineScope(Dispatchers.IO).launch {
                    val data = App.dbDoctors.document(doctorId).collection("patients")
                        .document(App.auth.uid!!).get().await().data
                    if (data == null || data["status"] == "Cancelled") {
                        App.dbDoctors.document(doctorId).collection("patients")
                            .document(App.auth.uid!!).set(
                                hashMapOf(
                                    "status" to "Unconfirmed",
                                    "start_time" to appointmentTimes[adapter!!.click].start_time,
                                    "end_time" to appointmentTimes[adapter!!.click].end_time,
                                    "full_name" to App.dbUsers.document(App.auth.uid!!).get()
                                        .await()["full_name"] as String
                                )
                            ).await()
                        App.dbUsers.document(App.auth.uid!!).collection("doctors")
                            .document(doctorId).set(
                                hashMapOf(
                                    "status" to "Unconfirmed",
                                    "start_time" to appointmentTimes[adapter!!.click].start_time,
                                    "end_time" to appointmentTimes[adapter!!.click].end_time,
                                    "last_name" to lastName
                                )
                            ).await()
                        withContext(Dispatchers.Main) {
                            dialog.closeDialogButton.isEnabled = true
                            dialog.applyDialogButton.isEnabled = true
                            dialog.dismiss()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            dialog.closeDialogButton.isEnabled = true
                            dialog.applyDialogButton.isEnabled = true
                            Tools.showSnackbar(
                                this@DoctorInformationActivity,
                                doctorInformationRoot,
                                "You've already booked an appointment.",
                                false,
                                "Hide"
                            )
                            dialog.dismiss()
                        }
                    }
                }
            } else {
                dialog.dismiss()
            }
        }

        // Enable buttons
        bookDoctorButton.isEnabled = true
        messageDoctorButton.isEnabled = true

        adapter =
            AppointmentTimesAdapter(
                appointmentTimes
            )
        dialog.appointmentsRecyclerView.adapter = adapter
        dialog.appointmentsRecyclerView.layoutManager = LinearLayoutManager(
            applicationContext,
            LinearLayoutManager.VERTICAL,
            false
        )

        // Get appointment times
        CoroutineScope(Dispatchers.IO).launch {
            val size =
                App.dbDoctors.document(doctorId).collection("patients").get().await().size()
            getTimes(doctor.start_time, doctor.end_time, 30)

            if (size == 0) {
                withContext(Dispatchers.Main) {
                    dialog.show()
                    adapter!!.notifyDataSetChanged()
                }
            } else {
                val patients =
                    App.dbDoctors.document(doctorId).collection("patients").get().await()
                for (document in patients.documents) {
                    if (document["status"] != "Cancelled") {
                        val startTime = document["start_time"] as String
                        for (i in 0 until appointmentTimes.size) {
                            if (startTime == appointmentTimes[i].start_time) {
                                appointmentTimes[i].available = false
                                break
                            }
                        }
                    }
                }
                withContext(Dispatchers.Main) {
                    dialog.show()
                    adapter!!.notifyDataSetChanged()
                }
            }
        }

    }

    // Time formatting for appointment management
    @SuppressLint("SimpleDateFormat")
    private fun getTimes(start_time: String, end_time: String, increment: Int) {
        if (start_time == end_time)
            return
        val df = SimpleDateFormat("HH:mm")
        val d = df.parse(start_time)
        val cal = Calendar.getInstance()
        cal.time = d!!
        cal.add(Calendar.MINUTE, increment)
        val newTime = df.format(cal.time).toString()
        appointmentTimes.add(
            AppointmentTimeModel(
                start_time,
                newTime
            )
        )
        getTimes(newTime, end_time, increment)
    }

    @SuppressLint("SetTextI18n")
    private fun getDoctor(doctorId: String) {
        progressBar.visibility = View.VISIBLE
        scrollView.visibility = View.GONE
        CoroutineScope(Dispatchers.IO).launch {
            doctor =
                App.dbDoctors.document(doctorId).get().await()
                    .toObject(DoctorModel::class.java)!!
            val byteArray = App.storage.child("/doctor_photos/$doctorId.png")
                .getBytes(FileSizeConstants.THREE_MEGABYTES).await()
            val bitmap = Utils.byteArrayToBitmap(byteArray)
            doctor.bitmap = bitmap

            withContext(Dispatchers.Main) {
                progressBar.visibility = View.GONE
                scrollView.visibility = View.VISIBLE
                doctorProfileImageView.setImageBitmap(doctor.bitmap)
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
            }
        }

    }
}