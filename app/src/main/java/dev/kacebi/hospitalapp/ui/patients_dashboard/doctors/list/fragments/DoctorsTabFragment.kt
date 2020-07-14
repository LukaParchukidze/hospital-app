package dev.kacebi.hospitalapp.ui.patients_dashboard.doctors.list.fragments

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import dev.kacebi.hospitalapp.App
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.file_size_constants.FileSizeConstants
import dev.kacebi.hospitalapp.ui.ItemOnClickListener
import dev.kacebi.hospitalapp.ui.patients_dashboard.DoctorOverviewModel
import dev.kacebi.hospitalapp.ui.patients_dashboard.PatientDashboardActivity
import dev.kacebi.hospitalapp.ui.patients_dashboard.doctors.full_information.DoctorInformationActivity
import dev.kacebi.hospitalapp.ui.patients_dashboard.doctors.list.adapters.DoctorsOverviewsAdapter
import dev.kacebi.hospitalapp.ui.patients_dashboard.home.SpecialtiesWithIconsAdapter
import kotlinx.android.synthetic.main.fragment_doctors_tab.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DoctorsTabFragment(private val specialty: String) : Fragment() {

    private var itemView: View? = null
    private val doctorsOverviews = mutableListOf<DoctorOverviewModel>()
    private lateinit var adapter: DoctorsOverviewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (itemView == null) {
            itemView = inflater.inflate(R.layout.fragment_doctors_tab, container, false)
            setUpDoctorsRecyclerView(itemView!!)
        }
        return itemView
    }

    private fun setUpDoctorsRecyclerView(itemView: View) {
        itemView.doctorsOverviewsRecyclerView.layoutManager = LinearLayoutManager(context)
        CoroutineScope(Dispatchers.IO).launch {
            val querySnapshot =
                App.dbDoctors.whereEqualTo("specialty", specialty).get().await()
            for (document in querySnapshot.documents) {
                val doctorOverview =
                    DoctorOverviewModel(
                        doctorId = document.id,
                        full_name = document["full_name"] as String,
                        last_name = document["last_name"] as String,
                        specialty = document["specialty"] as String,
                        working_experience = document["working_experience"] as Long
                    )
//                val byteArray =
//                    App.storage.child("/doctor_photos/${document.id}.png").getBytes(
//                        FileSizeConstants.THREE_MEGABYTES
//                    ).await()
//                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
//                doctorOverview.bitmap = bitmap
                doctorsOverviews.add(doctorOverview)
            }
            withContext(Dispatchers.Main) {
                adapter = DoctorsOverviewsAdapter(doctorsOverviews, object: ItemOnClickListener {
                    override fun onClick(adapterPosition: Int) {
                        val intent = Intent(
                            (activity as PatientDashboardActivity),
                            DoctorInformationActivity::class.java
                        ).apply {
                            putExtra(
                                "doctorId",
                                doctorsOverviews[adapterPosition].doctorId
                            )
                            putExtra("lastName", doctorsOverviews[adapterPosition].last_name)
                        }
                        activity!!.startActivity(intent)
                    }
                })
                itemView.doctorsOverviewsRecyclerView.adapter = adapter
            }
        }
    }
}