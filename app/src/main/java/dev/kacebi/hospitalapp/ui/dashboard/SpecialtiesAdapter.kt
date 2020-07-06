package dev.kacebi.hospitalapp.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import dev.kacebi.hospitalapp.R
import kotlinx.android.synthetic.main.item_specialties_layout.view.*
import kotlinx.android.synthetic.main.item_specialties_layout.view.specialtyTextView
import kotlinx.android.synthetic.main.item_specialties_list_layout.view.*

class SpecialtiesAdapter(
    private val specialties: MutableList<SpecialtyModel>,
    private val layout: Int
) :
    RecyclerView.Adapter<SpecialtiesAdapter.ViewHolder>() {

    private var clicked = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(layout, parent, false)
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind()
    }

    override fun getItemCount(): Int {
        return specialties.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var specialty: SpecialtyModel

        fun onBind() {
            specialty = specialties[adapterPosition]

            if (specialty.drawable != null) {
                itemView.specialtyImageView.setImageDrawable(specialty.drawable)
                itemView.specialtyTextView.text = specialty.specialty
            }
            else {
                itemView.specialtyDoctorTextView.text = specialty.specialty
                itemView.specialtyDoctorTextView.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.black))
                itemView.specialtyDoctorTextView.setOnClickListener {
                    clicked = adapterPosition
                    notifyDataSetChanged()
                }
                if (clicked == adapterPosition)
                    itemView.specialtyDoctorTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorPrimary))
            }
        }
    }
}