package dev.kacebi.hospitalapp.ui.patients_dashboard

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import dev.kacebi.hospitalapp.App
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.tools.Tools
import dev.kacebi.hospitalapp.ui.authentication.LoginActivity
import dev.kacebi.hospitalapp.ui.chat.activities.ChatsListActivity
import dev.kacebi.hospitalapp.ui.patients_dashboard.appointments.DoctorsAppointmentsActivity
import dev.kacebi.hospitalapp.ui.patients_dashboard.doctors.list.fragments.DoctorsFragment
import dev.kacebi.hospitalapp.ui.patients_dashboard.home.HomeFragment
import dev.kacebi.hospitalapp.ui.patients_dashboard.search.SearchDoctorsFragment
import dev.kacebi.hospitalapp.ui.profile.ProfileActivity
import kotlinx.android.synthetic.main.activity_patient_dashboard.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class PatientDashboardActivity : AppCompatActivity() {

    val homeFragment = HomeFragment()
    val doctorsFragment =
        DoctorsFragment()
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

    override fun onStart() {
        super.onStart()

        // Get user icon
        Tools.getUserIcon("/patient_photos/", App.dbUsers, this)
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
        Tools.setSupportActionBar(
            this,
            getString(R.string.dashboard),
            isLastName = false,
            backEnabled = false
        )
    }

    private fun setUpDrawerMenu() {
        Tools.toggleDrawer(this, drawerLayout, toolbar, this)
    }

    private fun setUpNavigationView(){
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.miUserProfile -> {
                    closeDrawer(navigationView, it)
                    Tools.startActivity(this, ProfileActivity(), false)
                }
                R.id.miAppointments -> {
                    closeDrawer(navigationView, it)
                    Tools.startActivity(
                        this,
                        DoctorsAppointmentsActivity(), false
                    )

                }
                R.id.miMessages -> {
                    closeDrawer(navigationView, it)
                    Tools.startActivity(
                        this,
                        ChatsListActivity(),
                        false
                    )
                }
                R.id.miSignOut -> {
                    closeDrawer(navigationView, it)
                    App.auth.signOut()
                    Tools.startActivity(this, LoginActivity(), true)
                }
            }
            true
        }
    }

    private fun closeDrawer(navigationView: NavigationView, it: MenuItem) {
        navigationView.setCheckedItem(it)
        navigationView.checkedItem!!.isChecked = false
        drawerLayout.closeDrawer(GravityCompat.START)
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
        supportFragmentManager.beginTransaction().show(fragment1).hide(fragment2).hide(fragment3)
            .commit()
    }

    override fun onBackPressed() {
        when (bottomNavigation.selectedItemId) {
            R.id.miHome -> super.onBackPressed()
            R.id.miDoctors -> {
                goToFragment(homeFragment, doctorsFragment, searchDoctorsFragment)
                bottomNavigation.menu.getItem(0).isChecked = true
            }
            R.id.miSearchDoctors -> {
                goToFragment(doctorsFragment, homeFragment, searchDoctorsFragment)
                bottomNavigation.menu.getItem(1).isChecked = true
            }
        }
    }
}