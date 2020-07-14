package dev.kacebi.hospitalapp.ui.patients_dashboard.doctors.list.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.kacebi.hospitalapp.App
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.ui.patients_dashboard.DoctorOverviewModel
import dev.kacebi.hospitalapp.ui.patients_dashboard.doctors.list.adapters.DoctorsOverviewsAdapter
import dev.kacebi.hospitalapp.ui.patients_dashboard.doctors.list.adapters.DoctorsTabPagerAdapter
import dev.kacebi.hospitalapp.ui.patients_dashboard.home.SpecialtyModel
import kotlinx.android.synthetic.main.fragment_doctors.*
import kotlinx.android.synthetic.main.fragment_doctors.view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class DoctorsFragment : Fragment() {

    var itemView: View? = null

    private val specialties = mutableListOf<SpecialtyModel>()
    private lateinit var adapter: DoctorsTabPagerAdapter
    private val doctorsTabFragments = mutableListOf<DoctorsTabFragment>()

    var getDoctorsIndex = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (itemView == null) {
            itemView = inflater.inflate(R.layout.fragment_doctors, container, false)
            setUpSpecialtiesTabLayout(itemView!!)
        }
        return itemView
    }

    private fun setUpSpecialtiesTabLayout(itemView: View) {
        CoroutineScope(Dispatchers.IO).launch {
            val querySnapshot = App.dbSpecialties.get().await()
            for (document in querySnapshot.documents) {
                val specialty = document.toObject(SpecialtyModel::class.java)!!
                specialties.add(specialty)
                doctorsTabFragments.add(DoctorsTabFragment(specialty.specialty))
            }
            withContext(Dispatchers.Main) {
                itemView.specialtiesTabLayout.setupWithViewPager(doctorsOverviewsViewPager)
                adapter = DoctorsTabPagerAdapter(fragmentManager!!, doctorsTabFragments, specialties)
                itemView.doctorsOverviewsViewPager.adapter = adapter
            }
        }
    }

//    fun setUpDoctorsRecyclerview(itemView: View, specialty: String) {
//        if (doctorsOverviews[specialty]!!.size == 0) {
//            itemView.doctorsOverviewsProgressBar.visibility = View.VISIBLE
//            itemView.doctorsOverviewsRecyclerView.swapAdapter(null, true)
//
//            jobDoctors = scope.launch {
//                val querySnapshot =
//                    App.dbDoctors.whereEqualTo("specialty", specialty).get().await()
//                for (document in querySnapshot.documents) {
//                    val doctorOverview =
//                        DoctorOverviewModel(
//                            doctorId = document.id,
//                            full_name = document["full_name"] as String,
//                            last_name = document["last_name"] as String,
//                            specialty = document["specialty"] as String
//                        )
////                    val byteArray =
////                        App.storage.child("/doctor_photos/${document.id}.png").getBytes(FileSizeConstants.THREE_MEGABYTES).await()
////                    val bitmapDrawable = BitmapDrawable(
////                        resources,
////                        BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
////                    )
////                    doctorOverview.drawable = bitmapDrawable
//                    doctorsOverviews[specialty]!!.add(doctorOverview)
//                }
//
//                doctorsOverviewsAdapter =
//                    DoctorsOverviewsAdapter(
//                        doctorsOverviews[specialty]!!,
//                        object : ItemOnClickListener {
//                            override fun onClick(adapterPosition: Int) {
//                                val intent = Intent(
//                                    (activity as PatientDashboardActivity),
//                                    DoctorInformationActivity::class.java
//                                ).apply {
//                                    putExtra(
//                                        "doctorId",
//                                        doctorsOverviews[specialty]!![adapterPosition].doctorId
//                                    )
//                                    putExtra(
//                                        "lastName",
//                                        doctorsOverviews[specialty]!![adapterPosition].last_name
//                                    )
//                                }
//                                activity!!.startActivity(intent)
//                            }
//                        }
//                    )
//                withContext(Dispatchers.Main) {
//                    itemView.doctorsOverviewsRecyclerView.adapter = doctorsOverviewsAdapter
//                    doctorsOverviewsAdapter.notifyDataSetChanged()
//                    itemView.doctorsOverviewsProgressBar.visibility = View.GONE
//                }
//            }
//        } else {
//            doctorsOverviewsAdapter =
//                DoctorsOverviewsAdapter(
//                    doctorsOverviews[specialty]!!,
//                    object : ItemOnClickListener {
//                        override fun onClick(adapterPosition: Int) {
//                            val intent = Intent(
//                                (activity as PatientDashboardActivity),
//                                DoctorInformationActivity::class.java
//                            ).apply {
//                                putExtra(
//                                    "doctorId",
//                                    doctorsOverviews[specialty]!![adapterPosition].doctorId
//                                )
//                                putExtra(
//                                    "lastName",
//                                    doctorsOverviews[specialty]!![adapterPosition].last_name
//                                )
//                            }
//                            activity!!.startActivity(intent)
//                        }
//                    }
//                )
//            itemView.doctorsOverviewsRecyclerView.adapter = doctorsOverviewsAdapter
//
//        }
//    }
}