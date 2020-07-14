package dev.kacebi.hospitalapp.ui.patients_dashboard.doctors.list.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import dev.kacebi.hospitalapp.ui.patients_dashboard.doctors.list.fragments.DoctorsTabFragment
import dev.kacebi.hospitalapp.ui.patients_dashboard.home.SpecialtyModel

class DoctorsTabPagerAdapter(
    fm: FragmentManager,
    private val fragments: MutableList<DoctorsTabFragment>,
    private val specialties: MutableList<SpecialtyModel>
) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return specialties[position].specialty
    }

}