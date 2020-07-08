package dev.kacebi.hospitalapp.ui.dashboard.doctors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.kacebi.hospitalapp.R
import kotlinx.android.synthetic.main.item_doctor_overview_layout.view.*

class DoctorsOverviewsAdapter(private val doctorsOverviews: MutableList<DoctorOverviewModel>) :
    RecyclerView.Adapter<DoctorsOverviewsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_doctor_overview_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind()
    }

    override fun getItemCount(): Int {
        return doctorsOverviews.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var doctorOverview: DoctorOverviewModel

        fun onBind() {
            doctorOverview = doctorsOverviews[adapterPosition]

            itemView.doctorOverviewImage.setImageDrawable(doctorOverview.drawable)
            itemView.doctorOverviewFullName.text = doctorOverview.full_name
            itemView.specialtyOverview.text = doctorOverview.specialty
        }
    }
}