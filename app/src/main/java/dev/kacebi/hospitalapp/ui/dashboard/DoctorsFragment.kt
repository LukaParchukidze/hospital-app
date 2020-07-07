package dev.kacebi.hospitalapp.ui.dashboard

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import dev.kacebi.hospitalapp.App
import dev.kacebi.hospitalapp.R
import kotlinx.android.synthetic.main.fragment_doctors.view.*
import kotlinx.android.synthetic.main.fragment_doctors.view.doctorsOverviewsRecyclerView
import kotlinx.android.synthetic.main.fragment_home.view.specialtiesRecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DoctorsFragment : Fragment() {

    private var itemView: View? = null
    private val specialties = mutableListOf<SpecialtyModel>()
    private lateinit var specialtiesAdapter: SpecialtiesAdapter

    private val doctorsOverviews = mutableListOf<DoctorOverviewModel>()
    private lateinit var doctorsOverviewsAdapter: DoctorsOverviewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (itemView == null) {
            itemView = inflater.inflate(R.layout.fragment_doctors, container, false)
            setUpSpecialtiesRecyclerView(itemView!!)
            setUpDoctorsRecyclerview(itemView!!)
        }
        return itemView
    }

    private fun setUpSpecialtiesRecyclerView(itemView: View) {
        itemView.specialtiesRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        CoroutineScope(Dispatchers.IO).launch {
            val specialtiesQS = App.dbSpecialties.get().await()
            for (specialtyQDS in specialtiesQS) {
                val specialty = App.dbSpecialties.document(specialtyQDS.id).get().await()
                    .toObject(SpecialtyModel::class.java)!!
                specialties.add(specialty)
            }
            specialtiesAdapter =
                SpecialtiesAdapter(specialties, R.layout.item_specialties_list_layout)
            withContext(Dispatchers.Main) {
                itemView.specialtiesRecyclerView.adapter = specialtiesAdapter
            }
        }
    }

    private fun setUpDoctorsRecyclerview(itemView: View) {
        itemView.doctorsOverviewsRecyclerView.layoutManager = LinearLayoutManager(context)
        itemView.doctorsOverviewsProgressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            val querySnapshot =
                App.dbDoctors.whereEqualTo("specialty", "audiologist").get().await()
            for (document in querySnapshot.documents) {
                val doctorOverview = DoctorOverviewModel(
                    full_name = document["full_name"] as String,
                    specialty = document["specialty"] as String
                )
                val byteArray =
                    App.storage.child(document.id + ".png").getBytes(1024 * 1024L).await()
                val bitmapDrawable = BitmapDrawable(resources, BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size))
                doctorOverview.drawable = bitmapDrawable
                doctorsOverviews.add(doctorOverview)
            }
            doctorsOverviewsAdapter = DoctorsOverviewsAdapter(doctorsOverviews)
            withContext(Dispatchers.Main) {
                itemView.doctorsOverviewsRecyclerView.adapter = doctorsOverviewsAdapter
                itemView.doctorsOverviewsProgressBar.visibility = View.GONE
            }
        }
    }
}