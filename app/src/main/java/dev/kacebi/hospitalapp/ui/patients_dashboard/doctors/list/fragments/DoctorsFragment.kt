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
}