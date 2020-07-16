package dev.kacebi.hospitalapp.ui.doctors_dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import dev.kacebi.hospitalapp.App
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.file_size_constants.FileSizeConstants
import dev.kacebi.hospitalapp.tools.Tools
import dev.kacebi.hospitalapp.utils.Utils
import dev.kacebi.hospitalapp.ui.ItemOnClickListener
import dev.kacebi.hospitalapp.ui.doctors_dashboard.adapters.PatientsAdapter
import kotlinx.android.synthetic.main.fragment_patients_list.*
import kotlinx.android.synthetic.main.fragment_patients_list.view.*
import kotlinx.android.synthetic.main.fragment_patients_list.view.isPatientsListEmptyTextView
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

    private fun getPatients(status: String) {
        itemView!!.patientsRecyclerView.layoutManager = LinearLayoutManager(context)
        adapter =
            PatientsAdapter(
                appointments,
                object : ItemOnClickListener {
                    override fun onClickChangeStatus(adapterPosition: Int, status: String) {
                        if (status == "Confirmed") {
                            val dialog = Tools.initDialog(
                                activity as DoctorDashboardActivity,
                                appointments[adapterPosition].patientId,
                                App.auth.uid!!,
                                R.layout.dialog_confirm_appointment_layout,
                                R.id.noConfirmButton,
                                R.id.confirmButton,
                                status,
                                appointments[adapterPosition].status,
                                true
                            )
                            dialog.show()
                        } else if (status == "Cancelled") {
                            val dialog = Tools.initDialog(
                                activity as DoctorDashboardActivity,
                                appointments[adapterPosition].patientId,
                                App.auth.uid!!,
                                R.layout.dialog_cancel_appointment_layout,
                                R.id.noCancelButton,
                                R.id.cancelButton,
                                status,
                                appointments[adapterPosition].status,
                                true
                            )
                            dialog.show()
                        }
                    }
                }
            )
        itemView!!.patientsRecyclerView.adapter = adapter

        App.dbDoctors.document(App.auth.uid!!).collection("patients").whereEqualTo("status", status)
            .addSnapshotListener { querySnapshot, _ ->
                if (querySnapshot == null || querySnapshot.documents.size == 0) {
                    appointments.clear()
                    adapter.notifyDataSetChanged()
                    itemView!!.isPatientsListEmptyTextView.visibility = View.VISIBLE
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
                            val bitmap = Utils.byteArrayToBitmap(byteArray)
                            appointment.bitmap = bitmap
                            appointments.add(appointment)
                            withContext(Dispatchers.Main) {
                                adapter.notifyDataSetChanged()
                                isPatientsListEmptyTextView.visibility = View.GONE
                            }
                        }
                    }
                }
            }
    }
}