package dev.kacebi.hospitalapp.ui.dashboard.doctors

import kotlinx.android.synthetic.main.item_specialties_layout.view.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.ui.dashboard.SpecialtyModel
import dev.kacebi.hospitalapp.ui.dashboard.SpecialtyOnClick

class SpecialtiesAdapter(
    private val specialties: MutableList<SpecialtyModel>,
    private val specialtyOnClick: SpecialtyOnClick
) :
    RecyclerView.Adapter<SpecialtiesAdapter.ViewHolder>() {

    companion object {
        var click = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_specialties_layout, parent, false)
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

            itemView.specialtyTextView.text = specialty.specialty
            itemView.specialtyTextView.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.black))
            itemView.specialtyTextView.setOnClickListener {
                click = adapterPosition
                specialtyOnClick.onClick(adapterPosition)
                notifyDataSetChanged()
            }

            if (click == adapterPosition)
                itemView.specialtyTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorPrimary))
        }
    }
}