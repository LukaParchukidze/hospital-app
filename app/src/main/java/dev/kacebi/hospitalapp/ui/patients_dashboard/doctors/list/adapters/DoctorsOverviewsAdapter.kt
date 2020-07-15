package dev.kacebi.hospitalapp.ui.patients_dashboard.doctors.list.adapters

import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.ui.ItemOnClickListener
import dev.kacebi.hospitalapp.ui.patients_dashboard.DoctorOverviewModel
import kotlinx.android.synthetic.main.item_doctor_overview_layout.view.*

class DoctorsOverviewsAdapter(
    private val doctorsOverviews: MutableList<DoctorOverviewModel>,
    private val itemClick: ItemOnClickListener
) :
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
        private lateinit var doctorOverview: DoctorOverviewModel

        fun onBind() {
            doctorOverview = doctorsOverviews[adapterPosition]

//            itemView.doctorOverviewImage.setImageBitmap(doctorOverview.bitmap)
            itemView.doctorOverviewFullName.text = doctorOverview.full_name
            itemView.doctorOverviewSpecialty.text = doctorOverview.specialty
            itemView.doctorOverviewWorkingExperience.text = "Working Years: ${doctorOverview.working_experience}"

            itemView.setOnClickListener {
                itemClick.onClick(adapterPosition)
            }
        }
    }
}