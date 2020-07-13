package dev.kacebi.hospitalapp.ui.doctors_dashboard.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import dev.kacebi.hospitalapp.ui.doctors_dashboard.PatientsListFragment

class PatientsPagerAdapter(fm: FragmentManager, private val fragments: MutableList<PatientsListFragment>): FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

}