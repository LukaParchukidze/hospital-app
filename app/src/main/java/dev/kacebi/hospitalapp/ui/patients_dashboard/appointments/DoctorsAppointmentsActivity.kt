package dev.kacebi.hospitalapp.ui.patients_dashboard.appointments

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import dev.kacebi.hospitalapp.App
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.file_size_constants.FileSizeConstants
import dev.kacebi.hospitalapp.tools.Tools
import dev.kacebi.hospitalapp.tools.Utils
import dev.kacebi.hospitalapp.ui.ItemOnClickListener
import kotlinx.android.synthetic.main.activity_doctors_appointments.*
import kotlinx.android.synthetic.main.dialog_cancel_appointment_layout.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DoctorsAppointmentsActivity : AppCompatActivity() {

    private lateinit var adapter: DoctorsAppointmentsAdapter
    private val appointments = mutableListOf<DoctorAppointmentModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctors_appointments)

        Tools.setSupportActionBar(
            this,
            getString(R.string.appointments),
            isLastName = false,
            backEnabled = true
        )
        toolbar!!.setNavigationOnClickListener {
            onBackPressed()
        }
        init()
    }

    private fun initCancelDialog(adapterPosition: Int) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_cancel_appointment_layout)

        val params: ViewGroup.LayoutParams = dialog.window!!.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = params as WindowManager.LayoutParams

        dialog.cancelButton.setOnClickListener {
            val appointment = appointments[adapterPosition]
            dialog.dismiss()

            CoroutineScope(Dispatchers.IO).launch {
                App.dbDoctors.document(appointment.doctorId).collection("patients").document(App.auth.uid!!).update("status", "Cancelled").await()
                App.dbUsers.document(App.auth.uid!!).collection("doctors").document(appointment.doctorId).update("status", "Cancelled").await()
            }
        }
        dialog.noCancelButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun init() {
        doctorsAppointmentsRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter =
            DoctorsAppointmentsAdapter(
                appointments,
                object : ItemOnClickListener {
                    override fun onClick(adapterPosition: Int) {
                        initCancelDialog(adapterPosition)
                    }

                })
        doctorsAppointmentsRecyclerView.adapter = adapter
        App.dbUsers.document(App.auth.uid!!).collection("doctors")
            .addSnapshotListener { querySnapshot, _ ->
                if (querySnapshot == null || querySnapshot.documents.size == 0) {
                    appointments.clear()
                    adapter.notifyDataSetChanged()
                } else {
                    appointments.clear()
                    CoroutineScope(Dispatchers.IO).launch {
                        for (document in querySnapshot.documents) {
                            val appointment =
                                DoctorAppointmentModel(
                                    doctorId = document.id,
                                    status = document["status"] as String,
                                    last_name = document["last_name"] as String,
                                    start_time = document["start_time"] as String,
                                    end_time = document["end_time"] as String,
                                    time = (document["start_time"] as String + " - " + document["end_time"])
                                )
                            val byteArray =
                                App.storage.child("/doctor_photos/${document.id}.png").getBytes(FileSizeConstants.THREE_MEGABYTES).await()
                            val bitmap = Utils.byteArrayToBitmap(byteArray)
                            appointment.bitmap = bitmap
                            appointments.add(appointment)
                        }
                        withContext(Dispatchers.Main) {
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }
    }
}