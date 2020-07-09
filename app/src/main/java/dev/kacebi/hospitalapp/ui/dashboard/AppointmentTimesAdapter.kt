package dev.kacebi.hospitalapp.ui.dashboard

import android.graphics.Color.red
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import dev.kacebi.hospitalapp.R
import kotlinx.android.synthetic.main.item_appointments_layout.view.*

class AppointmentTimesAdapter(private val appointmentTimes: MutableList<AppointmentTime>) :
    RecyclerView.Adapter<AppointmentTimesAdapter.ViewHolder>() {

    var click = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_appointments_layout, parent, false)
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind()
    }

    override fun getItemCount(): Int {
        return appointmentTimes.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var appointmentTime: AppointmentTime

        fun onBind() {
            appointmentTime = appointmentTimes[adapterPosition]

            itemView.appointmentTimeTextView.text = appointmentTime.start_time + " - " + appointmentTime.end_time
            if (appointmentTime.available) {
                itemView.appointmentTimeTextView.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.holo_red_dark))
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, android.R.color.white))
                itemView.setOnClickListener {
                    click = adapterPosition
                    notifyDataSetChanged()
                }
                if (click == adapterPosition) {
                    itemView.appointmentTimeTextView.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.white))
                    itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, android.R.color.holo_red_dark))
                }
            } else {
                itemView.appointmentTimeTextView.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.white))
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, android.R.color.darker_gray))
            }
        }
    }
}