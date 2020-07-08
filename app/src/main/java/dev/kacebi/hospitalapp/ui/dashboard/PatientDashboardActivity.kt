package dev.kacebi.hospitalapp.ui.dashboard

import android.os.Bundle
import android.util.Log.d
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.ui.dashboard.doctors.DoctorsFragment
import dev.kacebi.hospitalapp.ui.dashboard.home.HomeFragment
import dev.kacebi.hospitalapp.ui.dashboard.search.SearchDoctorsFragment
import kotlinx.android.synthetic.main.activity_patient_dashboard.*

class PatientDashboardActivity : AppCompatActivity() {

    private lateinit var toggle: ActionBarDrawerToggle

    val homeFragment =
        HomeFragment()
    val doctorsFragment =
        DoctorsFragment()
    val searchDoctorsFragment =
        SearchDoctorsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_dashboard)

        setUpToolbar()
        setUpDrawerMenu()
        setUpBottomNavigation()

        supportFragmentManager.beginTransaction()
            .add(R.id.dashboardFragmentContainer, doctorsFragment).hide(doctorsFragment).commit()
        supportFragmentManager.beginTransaction().add(R.id.dashboardFragmentContainer, searchDoctorsFragment).hide(searchDoctorsFragment).commit()
        supportFragmentManager.beginTransaction().add(R.id.dashboardFragmentContainer, homeFragment)
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar!!.title = "Dashboard"
    }

    private fun setUpDrawerMenu() {
        toggle = ActionBarDrawerToggle(
            this, drawerLayout,
            toolbar as Toolbar?, R.string.open_drawer, R.string.close_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun setUpBottomNavigation() {
        bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.miHome -> goToFragment(homeFragment, doctorsFragment, searchDoctorsFragment)
                R.id.miDoctors -> goToFragment(doctorsFragment, homeFragment, searchDoctorsFragment)
                R.id.miSearchDoctors -> goToFragment(searchDoctorsFragment, homeFragment, doctorsFragment)
            }
            true
        }
    }

    fun goToFragment(fragment1: Fragment, fragment2: Fragment, fragment3: Fragment) {
        supportFragmentManager.beginTransaction().show(fragment1).hide(fragment2).hide(fragment3).commit()
    }

}