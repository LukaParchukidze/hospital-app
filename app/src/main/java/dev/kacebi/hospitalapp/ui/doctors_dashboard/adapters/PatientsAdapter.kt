package dev.kacebi.hospitalapp.ui.doctors_dashboard.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.kacebi.hospitalapp.App
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.extensions.setNewColor
import dev.kacebi.hospitalapp.ui.ItemOnClickListener
import dev.kacebi.hospitalapp.ui.chat.activities.ChatActivity
import dev.kacebi.hospitalapp.ui.doctors_dashboard.PatientAppointmentModel
import kotlinx.android.synthetic.main.item_patients_layout.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PatientsAdapter(
    private val appointments: MutableList<PatientAppointmentModel>,
    private val itemClick: ItemOnClickListener
) :
    RecyclerView.Adapter<PatientsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_patients_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind()
    }

    override fun getItemCount(): Int {
        return appointments.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var appointment: PatientAppointmentModel

        @SuppressLint("SetTextI18n")
        fun onBind() {
            appointment = appointments[adapterPosition]

            itemView.patientProfileImageView.setImageBitmap(appointment.bitmap)
            itemView.patientFullName.text = appointment.full_name
            itemView.appointmentTimeTextView.text =
                appointment.start_time + " - " + appointment.end_time
            itemView.appointmentStatusTextView.text = appointment.status

            when (appointment.status) {
                "Unconfirmed" -> {
                    itemView.messagePatientButton.visibility = View.GONE
                    itemView.confirmPatientButton.visibility = View.VISIBLE
                    itemView.appointmentStatusTextView.setNewColor(
                        itemView.context,
                        R.color.statusUnconfirmed
                    )
                }
                "Cancelled" -> {
                    itemView.cancelAppointmentButton.visibility = View.GONE
                    itemView.appointmentStatusTextView.setNewColor(
                        itemView.context,
                        R.color.statusCancelled
                    )
                }
                "Confirmed" -> itemView.appointmentStatusTextView.setNewColor(
                    itemView.context,
                    R.color.statusConfirmed
                )
            }

            itemView.messagePatientButton.setOnClickListener {
                val intent = Intent(itemView.context, ChatActivity::class.java).apply {
                    putExtra("id", appointment.patientId)
                    putExtra("name", appointment.full_name)
                }
                itemView.context.startActivity(intent)
            }
            itemView.confirmPatientButton.setOnClickListener {
                itemClick.onClickChangeStatus(adapterPosition, "Confirmed")
            }
            itemView.cancelAppointmentButton.setOnClickListener {
                itemClick.onClickChangeStatus(adapterPosition, "Cancelled")
            }
        }
    }
}