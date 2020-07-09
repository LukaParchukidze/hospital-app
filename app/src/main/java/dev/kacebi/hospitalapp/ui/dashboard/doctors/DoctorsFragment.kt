package dev.kacebi.hospitalapp.ui.dashboard.doctors

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import dev.kacebi.hospitalapp.App
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.ui.dashboard.PatientDashboardActivity
import dev.kacebi.hospitalapp.ui.dashboard.home.specialties.SpecialtyModel
import dev.kacebi.hospitalapp.ui.dashboard.home.specialties.SpecialtyOnClick
import kotlinx.android.synthetic.main.fragment_doctors.view.*
import kotlinx.android.synthetic.main.fragment_home.view.specialtiesRecyclerView
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class DoctorsFragment : Fragment() {

    var itemView: View? = null
    private val specialties = mutableListOf<SpecialtyModel>()
    lateinit var adapter: SpecialtiesAdapter
    private var jobDoctors: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

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

            jobDoctors = scope.launch {
                val querySnapshot =
                    App.dbDoctors.whereEqualTo("specialty", specialty).get().await()
                for (document in querySnapshot.documents) {
                    val doctorOverview =
                        DoctorOverviewModel(
                            doctorId = document.id,
                            full_name = document["full_name"] as String,
                            last_name = document["last_name"] as String,
                            specialty = document["specialty"] as String
                        )
//                    val byteArray =
//                        App.storage.child(document.id + ".png").getBytes(1024 * 1024L).await()
//                    val bitmapDrawable = BitmapDrawable(
//                        resources,
//                        BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
//                    )
//                    doctorOverview.drawable = bitmapDrawable
                    doctorsOverviews[specialty]!!.add(doctorOverview)
                }
                doctorsOverviewsAdapter =
                    DoctorsOverviewsAdapter(
                        doctorsOverviews[specialty]!!,
                        object : DoctorsOverviewsOnClick {
                            override fun onClick(adapterPosition: Int) {
                                val intent = Intent((activity as PatientDashboardActivity), DoctorInformationActivity::class.java).apply {
                                    putExtra("doctorId", doctorsOverviews[specialty]!![adapterPosition].doctorId)
                                    putExtra("lastName", doctorsOverviews[specialty]!![adapterPosition].last_name)
                                }
                                activity!!.startActivity(intent)
                            }
                        }
                    )
                withContext(Dispatchers.Main) {
                    itemView.doctorsOverviewsRecyclerView.adapter = doctorsOverviewsAdapter
                    doctorsOverviewsAdapter.notifyDataSetChanged()
                    itemView.doctorsOverviewsProgressBar.visibility = View.GONE
                }
            }
        } else {
            doctorsOverviewsAdapter =
                DoctorsOverviewsAdapter(
                    doctorsOverviews[specialty]!!,
                    object : DoctorsOverviewsOnClick {
                        override fun onClick(adapterPosition: Int) {
                            val intent = Intent((activity as PatientDashboardActivity), DoctorInformationActivity::class.java).apply {
                                putExtra("documentId", doctorsOverviews[specialty]!![adapterPosition].doctorId)
                                putExtra("lastName", doctorsOverviews[specialty]!![adapterPosition].last_name)
                            }
                            activity!!.startActivity(intent)
                        }
                    }
                )
            itemView.doctorsOverviewsRecyclerView.adapter = doctorsOverviewsAdapter

        }
    }
}