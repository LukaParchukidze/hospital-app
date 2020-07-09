package dev.kacebi.hospitalapp.ui.dashboard

import android.os.Bundle
import android.util.Log.d
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import dev.kacebi.hospitalapp.App
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.tools.Tools
import dev.kacebi.hospitalapp.ui.authentication.LoginActivity
import dev.kacebi.hospitalapp.ui.dashboard.doctors.DoctorsFragment
import dev.kacebi.hospitalapp.ui.dashboard.home.HomeFragment
import dev.kacebi.hospitalapp.ui.dashboard.search.SearchDoctorsFragment
import kotlinx.android.synthetic.main.activity_patient_dashboard.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class PatientDashboardActivity : AppCompatActivity() {

    private lateinit var toggle: ActionBarDrawerToggle

    val homeFragment = HomeFragment()
    val doctorsFragment = DoctorsFragment()
    val searchDoctorsFragment = SearchDoctorsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_dashboard)

        setUpToolbar()
        setUpDrawerMenu()
        setUpBottomNavigation()
        setUpNavigationView()

        addFragments()
    }

    private fun addFragments(){
        supportFragmentManager.beginTransaction()
            .add(R.id.dashboardFragmentContainer, doctorsFragment).hide(doctorsFragment).commit()
        supportFragmentManager.beginTransaction()
            .add(R.id.dashboardFragmentContainer, searchDoctorsFragment).hide(searchDoctorsFragment)
            .commit()
        supportFragmentManager.beginTransaction().add(R.id.dashboardFragmentContainer, homeFragment)
            .commit()
    }

    private fun setUpToolbar() {
        Tools.setSupportActionBar(this,getString(R.string.dashboard))
    }

    private fun setUpDrawerMenu() {
        toggle = ActionBarDrawerToggle(
            this, drawerLayout,
            toolbar, R.string.open_drawer, R.string.close_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun setUpNavigationView(){
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.miAppointments -> {
                    Tools.startActivity(this, DoctorsAppointmentsActivity(), false)
                }
                R.id.miSignOut -> {
                    App.auth.signOut()
                    Tools.startActivity(this, LoginActivity(), true)
                }
            }
            true
        }
    }

    private fun setUpBottomNavigation() {
        bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.miHome -> goToFragment(homeFragment, doctorsFragment, searchDoctorsFragment)
                R.id.miDoctors -> goToFragment(doctorsFragment, homeFragment, searchDoctorsFragment)
                R.id.miSearchDoctors -> goToFragment(
                    searchDoctorsFragment,
                    homeFragment,
                    doctorsFragment
                )
            }
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
            return true

        return super.onOptionsItemSelected(item)
    }

    fun goToFragment(fragment1: Fragment, fragment2: Fragment, fragment3: Fragment) {
        supportFragmentManager.beginTransaction().show(fragment1).hide(fragment2).hide(fragment3)
            .commit()
    }
}