package dev.kacebi.hospitalapp.ui.patients_dashboard

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import dev.kacebi.hospitalapp.App
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.file_size_constants.FileSizeConstants
import dev.kacebi.hospitalapp.tools.Tools
import dev.kacebi.hospitalapp.ui.authentication.LoginActivity
import dev.kacebi.hospitalapp.ui.chat.activities.ChatsListActivity
import dev.kacebi.hospitalapp.ui.patients_dashboard.appointments.DoctorsAppointmentsActivity
import dev.kacebi.hospitalapp.ui.patients_dashboard.doctors.list.DoctorsFragment
import dev.kacebi.hospitalapp.ui.patients_dashboard.home.HomeFragment
import dev.kacebi.hospitalapp.ui.patients_dashboard.search.SearchDoctorsFragment
import dev.kacebi.hospitalapp.ui.profile.ProfileActivity
import kotlinx.android.synthetic.main.activity_patient_dashboard.*
import kotlinx.android.synthetic.main.nav_drawer_header.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PatientDashboardActivity : AppCompatActivity() {

    private lateinit var toggle: ActionBarDrawerToggle

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

        //get user icon
        CoroutineScope(Dispatchers.IO).launch {
            val byteArray = App.storage.child("/patient_photos/${App.auth.uid!!}.png")
                .getBytes(FileSizeConstants.THREE_MEGABYTES).await()
            val bitmapDrawable = BitmapDrawable(
                resources,
                BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            )
            val fullName = App.dbUsers.document(App.auth.uid!!).get().await()["full_name"] as String
            withContext(Dispatchers.Main) {
                drawerPictureImageView.setImageDrawable(bitmapDrawable)
                drawerFullNameTextView.text = "Hi, $fullName"
            }
        }
    }

    private fun addFragments() {
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
        toggle = ActionBarDrawerToggle(
            this, drawerLayout,
            toolbar, R.string.open_drawer, R.string.close_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun setUpNavigationView() {
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
//        if (toggle.onOptionsItemSelected(item))
//            return true

        return super.onOptionsItemSelected(item)
    }

    fun goToFragment(fragment1: Fragment, fragment2: Fragment, fragment3: Fragment) {
        supportFragmentManager.beginTransaction().show(fragment1).hide(fragment2).hide(fragment3)
            .commit()
    }
}