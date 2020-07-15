package dev.kacebi.hospitalapp.ui.patients_dashboard.doctors.full_information

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.ui.patients_dashboard.doctors.full_information.models.AppointmentTimeModel
import kotlinx.android.synthetic.main.item_appointments_layout.view.*

class AppointmentTimesAdapter(private val appointmentTimeModels: MutableList<AppointmentTimeModel>) :
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
        return appointmentTimeModels.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var appointmentTimeModel: AppointmentTimeModel

        @SuppressLint("SetTextI18n")
        fun onBind() {
            appointmentTimeModel = appointmentTimeModels[adapterPosition]

            itemView.appointmentTimeTextView.text = appointmentTimeModel.start_time + " - " + appointmentTimeModel.end_time
            if (appointmentTimeModel.available) {
                itemView.appointmentTimeTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.statusCancelled))
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, android.R.color.white))
                itemView.setOnClickListener {
                    click = adapterPosition
                    notifyDataSetChanged()
                }
                if (click == adapterPosition) {
                    itemView.appointmentTimeTextView.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.white))
                    itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.statusCancelled))
                }
            } else {
                itemView.appointmentTimeTextView.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.white))
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.dialogCancelGray))
            }
        }
    }
}