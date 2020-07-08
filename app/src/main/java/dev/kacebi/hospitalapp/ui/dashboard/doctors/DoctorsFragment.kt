package dev.kacebi.hospitalapp.ui.dashboard.doctors

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import dev.kacebi.hospitalapp.App
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.ui.dashboard.DoctorOverviewModel
import dev.kacebi.hospitalapp.ui.dashboard.DoctorsOverviewsAdapter
import dev.kacebi.hospitalapp.ui.dashboard.SpecialtyModel
import dev.kacebi.hospitalapp.ui.dashboard.SpecialtyOnClick
import kotlinx.android.synthetic.main.fragment_doctors.view.*
import kotlinx.android.synthetic.main.fragment_home.view.specialtiesRecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DoctorsFragment : Fragment() {

    var itemView: View? = null
    private val specialties = mutableListOf<SpecialtyModel>()
    lateinit var adapter: SpecialtiesAdapter

    private val doctorsOverviews = mutableMapOf<String, MutableList<DoctorOverviewModel>>()
    private lateinit var doctorsOverviewsAdapter: DoctorsOverviewsAdapter

    var getDoctorsIndex = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (itemView == null) {
            itemView = inflater.inflate(R.layout.fragment_doctors, container, false)

            doctorsOverviews["Audiologist"] = mutableListOf()
            doctorsOverviews["Cardiologist"] = mutableListOf()
            doctorsOverviews["Dermatologist"] = mutableListOf()
            doctorsOverviews["Gastroenterologist"] = mutableListOf()
            doctorsOverviews["Immunologist"] = mutableListOf()
            doctorsOverviews["Neurosurgeon"] = mutableListOf()

            itemView!!.doctorsOverviewsRecyclerView.layoutManager = LinearLayoutManager(context)
            setUpSpecialtiesRecyclerView(itemView!!)
            setUpDoctorsRecyclerview(itemView!!, "Audiologist")
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
            adapter =
                SpecialtiesAdapter(
                    specialties,
                    object :
                        SpecialtyOnClick {
                        override fun onClick(adapterPosition: Int) {
                            setUpDoctorsRecyclerview(
                                itemView,
                                specialties[adapterPosition].specialty
                            )
                        }
                    }
                )
            withContext(Dispatchers.Main) {
                itemView.specialtiesRecyclerView.adapter = adapter
            }
        }
    }

    fun setUpDoctorsRecyclerview(itemView: View, specialty: String) {
        if (doctorsOverviews[specialty]!!.size == 0) {
            itemView.doctorsOverviewsProgressBar.visibility = View.VISIBLE
            itemView.doctorsOverviewsRecyclerView.swapAdapter(null, true)
            CoroutineScope(Dispatchers.IO).launch {
                val querySnapshot =
                    App.dbDoctors.whereEqualTo("specialty", specialty).get().await()
                for (document in querySnapshot.documents) {
                    val doctorOverview =
                        DoctorOverviewModel(
                            full_name = document["full_name"] as String,
                            specialty = document["specialty"] as String
                        )
                    val byteArray =
                        App.storage.child(document.id + ".png").getBytes(1024 * 1024L).await()
                    val bitmapDrawable = BitmapDrawable(
                        resources,
                        BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                    )
                    doctorOverview.drawable = bitmapDrawable
                    doctorsOverviews[specialty]!!.add(doctorOverview)
                }
                doctorsOverviewsAdapter =
                    DoctorsOverviewsAdapter(
                        doctorsOverviews[specialty]!!
                    )
                withContext(Dispatchers.Main) {
                    itemView.doctorsOverviewsRecyclerView.adapter = doctorsOverviewsAdapter
                    itemView.doctorsOverviewsProgressBar.visibility = View.GONE
                }
            }
        } else {
            doctorsOverviewsAdapter =
                DoctorsOverviewsAdapter(
                    doctorsOverviews[specialty]!!
                )
            itemView.doctorsOverviewsRecyclerView.adapter = doctorsOverviewsAdapter
        }
    }
}