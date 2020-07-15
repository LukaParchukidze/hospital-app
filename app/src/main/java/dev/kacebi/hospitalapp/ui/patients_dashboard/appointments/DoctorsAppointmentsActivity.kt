package dev.kacebi.hospitalapp.ui.patients_dashboard.appointments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import dev.kacebi.hospitalapp.App
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.file_size_constants.FileSizeConstants
import dev.kacebi.hospitalapp.tools.Tools
import dev.kacebi.hospitalapp.utils.Utils
import dev.kacebi.hospitalapp.ui.ItemOnClickListener
import kotlinx.android.synthetic.main.activity_doctors_appointments.*
import kotlinx.android.synthetic.main.spinkit_loader_layout.*
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

    private fun init() {
        doctorsAppointmentsRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter =
            DoctorsAppointmentsAdapter(
                appointments,
                object : ItemOnClickListener {
                    override fun onClick(adapterPosition: Int) {
                        val dialog = Tools.initDialog(
                            this@DoctorsAppointmentsActivity,
                            App.auth.uid!!,
                            appointments[adapterPosition].doctorId,
                            R.layout.dialog_cancel_appointment_layout,
                            R.id.noCancelButton,
                            R.id.cancelButton,
                            "Cancelled", true
                        )
                        dialog.show()
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
                            spinKitContainerView.visibility = View.GONE
                        }
                    }
                }
            }
    }
}