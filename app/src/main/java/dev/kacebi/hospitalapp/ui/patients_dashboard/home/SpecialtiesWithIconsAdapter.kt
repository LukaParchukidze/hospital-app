package dev.kacebi.hospitalapp.ui.patients_dashboard.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.ui.ItemOnClickListener
import kotlinx.android.synthetic.main.item_specialties_with_icons_layout.view.*

class SpecialtiesWithIconsAdapter(
    private val specialties: MutableList<SpecialtyModel>,
    private val itemClick: ItemOnClickListener
) :
    RecyclerView.Adapter<SpecialtiesWithIconsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_specialties_with_icons_layout, parent, false)
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind()
    }

    override fun getItemCount(): Int {
        return specialties.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var specialty: SpecialtyModel

        fun onBind() {
            specialty = specialties[adapterPosition]

            itemView.specialtyImageView.setImageBitmap(specialty.bitmap)
            itemView.specialtyTextView.text = specialty.specialty
            itemView.setOnClickListener {
                itemClick.onClick(adapterPosition)
            }
        }
    }
}