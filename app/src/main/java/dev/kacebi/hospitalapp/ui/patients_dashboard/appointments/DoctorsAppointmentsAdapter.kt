package dev.kacebi.hospitalapp.ui.patients_dashboard.appointments

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.ui.ItemOnClickListener
import dev.kacebi.hospitalapp.ui.chat.activities.ChatActivity
import kotlinx.android.synthetic.main.item_doctor_appointment_layout.view.*

class DoctorsAppointmentsAdapter(
    private val appointments: MutableList<DoctorAppointmentModel>,
    private val itemClick: ItemOnClickListener
) :
    RecyclerView.Adapter<DoctorsAppointmentsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_doctor_appointment_layout, parent, false)
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind()
    }

    override fun getItemCount(): Int {
        return appointments.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var appointment: DoctorAppointmentModel

        fun onBind() {
            appointment = appointments[adapterPosition]
            itemView.doctorProfileImageView.setImageDrawable(appointment.drawable)
            itemView.appointmentStatusTextView.text = appointment.status
            itemView.doctorLastName.text = "Dr. " + appointment.last_name
            itemView.appointmentTimeTextView.text = appointment.time

            if (appointment.status == "Cancelled") {
                itemView.message.visibility = View.VISIBLE
                itemView.cancelAppointmentButton.visibility = View.GONE
            }
            else if (appointment.status == "Unconfirmed") {
                itemView.message.visibility = View.GONE
                itemView.cancelAppointmentButton.visibility = View.VISIBLE
            }
            else if (appointment.status == "Confirmed") {
                itemView.message.visibility = View.VISIBLE
                itemView.cancelAppointmentButton.visibility = View.VISIBLE
            }

            itemView.cancelAppointmentButton.setOnClickListener {
                itemClick.onClick(adapterPosition)
            }
            itemView.message.setOnClickListener {
                val intent = Intent(itemView.context, ChatActivity::class.java).apply {
                    putExtra("id", appointment.doctorId)
                    putExtra("name", appointment.last_name)
                }
                itemView.context.startActivity(intent)
            }
        }
    }
}