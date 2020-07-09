package dev.kacebi.hospitalapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.kacebi.hospitalapp.R
import kotlinx.android.synthetic.main.item_doctor_appointment_layout.view.*

class DoctorsAppointmentsAdapter(private val appointments: MutableList<DoctorAppointmentModel>) :
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
            itemView.doctorImage.setImageDrawable(appointment.drawable)
            itemView.doctorLastName.text = "Dr. " + appointment.last_name
            itemView.doctorTime.text = appointment.time

            itemView.cancelAppointment.setOnClickListener {

            }
            itemView.message.setOnClickListener {

            }
        }
    }
}