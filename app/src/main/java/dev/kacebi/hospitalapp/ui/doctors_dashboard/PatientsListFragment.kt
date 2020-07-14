package dev.kacebi.hospitalapp.ui.doctors_dashboard

import android.app.Dialog
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import dev.kacebi.hospitalapp.App
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.file_size_constants.FileSizeConstants
import dev.kacebi.hospitalapp.ui.ItemOnClickListener
import dev.kacebi.hospitalapp.ui.doctors_dashboard.adapters.PatientsAdapter
import kotlinx.android.synthetic.main.dialog_cancel_appointment_layout.*
import kotlinx.android.synthetic.main.fragment_patients_list.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PatientsListFragment : Fragment() {

    private var itemView: View? = null
    private val appointments = mutableListOf<PatientAppointmentModel>()
    private lateinit var adapter: PatientsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (itemView == null) {
            itemView = inflater.inflate(R.layout.fragment_patients_list, container, false)
            when ((activity as DoctorDashboardActivity).currentItem) {
                0 -> {
                    getPatients("Confirmed")
                }
                1 -> {
                    getPatients("Unconfirmed")
                }
                2 -> {
                    getPatients("Cancelled")
                }
            }

            (activity as DoctorDashboardActivity).currentItem =
                (activity as DoctorDashboardActivity).currentItem + 1
        }
        return itemView
    }

    private fun initDialog(adapterPosition: Int, status: String) {
        val dialog = Dialog(activity as DoctorDashboardActivity)
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
                App.dbDoctors.document(App.auth.uid!!).collection("patients").document(appointment.patientId).update("status", status).await()
                App.dbUsers.document(appointment.patientId).collection("doctors").document(App.auth.uid!!).update("status", status).await()
            }
        }
        dialog.noCancelButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun getPatients(status: String) {
        itemView!!.patientsRecyclerView.layoutManager = LinearLayoutManager(context)
        adapter =
            PatientsAdapter(
                appointments,
                object: ItemOnClickListener {
                    override fun onClickChangeStatus(adapterPosition: Int, status: String) {
                        initDialog(adapterPosition, status)
                    }
                }
            )
        itemView!!.patientsRecyclerView.adapter = adapter

        App.dbDoctors.document(App.auth.uid!!).collection("patients").whereEqualTo("status", status)
            .addSnapshotListener { querySnapshot, _ ->
                if (querySnapshot == null || querySnapshot.documents.size == 0) {
                    appointments.clear()
                    adapter.notifyDataSetChanged()
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        for (document in querySnapshot.documents) {
                            val appointment = PatientAppointmentModel(
                                patientId = document.id,
                                full_name = document["full_name"] as String,
                                start_time = document["start_time"] as String,
                                end_time = document["end_time"] as String,
                                status = document["status"] as String
                            )
                            val byteArray =
                                App.storage.child("/patient_photos/${appointment.patientId}.png")
                                    .getBytes(FileSizeConstants.THREE_MEGABYTES)
                                    .await()
                            val bitmapDrawable = BitmapDrawable(
                                this@PatientsListFragment.resources,
                                BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                            )
                            appointment.drawable = bitmapDrawable
                            appointments.add(appointment)
                            withContext(Dispatchers.Main) {
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
    }
}