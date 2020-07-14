package dev.kacebi.hospitalapp.ui.doctors_dashboard

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.navigation.NavigationView
import dev.kacebi.hospitalapp.App
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.file_size_constants.FileSizeConstants
import dev.kacebi.hospitalapp.tools.Tools
import dev.kacebi.hospitalapp.tools.Utils
import dev.kacebi.hospitalapp.ui.authentication.LoginActivity
import dev.kacebi.hospitalapp.ui.chat.activities.ChatsListActivity
import dev.kacebi.hospitalapp.ui.doctors_dashboard.adapters.PatientsPagerAdapter
import dev.kacebi.hospitalapp.ui.profile.ProfileActivity
import kotlinx.android.synthetic.main.activity_doctor_dashboard.*
import kotlinx.android.synthetic.main.activity_doctor_dashboard.drawerLayout
import kotlinx.android.synthetic.main.nav_drawer_header.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DoctorDashboardActivity : AppCompatActivity() {

    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var adapter: PatientsPagerAdapter
    private val fragments = mutableListOf<PatientsListFragment>()
    var currentItem = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_dashboard)

        Tools.setSupportActionBar(this, "Dashboard", isLastName = false, backEnabled = false)
        setUpDrawerMenu()

        setUpPatientsViewPager()
        setUpBottomNavigation()
    }

    private fun setUpDrawerMenu() {
        toggle = ActionBarDrawerToggle(
            this, drawerLayout,
            toolbar, R.string.open_drawer, R.string.close_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        setUpNavigationView()

        //get user icon
        CoroutineScope(Dispatchers.IO).launch {
            val byteArray = App.storage.child("/doctor_photos/${App.auth.uid!!}.png").getBytes(
                FileSizeConstants.THREE_MEGABYTES).await()
            val bitmap = Utils.byteArrayToBitmap(byteArray)
            val fullName = App.dbDoctors.document(App.auth.uid!!).get().await()["full_name"] as String
            withContext(Dispatchers.Main) {

                drawerPictureImageView.setImageBitmap(bitmap)
                drawerFullNameTextView.text = fullName

                drawerPictureImageView.setImageBitmap(bitmap)
                drawerFullNameTextView.text = "Hi, $fullName"

            }
        }
    }

    private fun setUpNavigationView(){
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.menu.findItem(R.id.miAppointments).isVisible = false
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.miUserProfile -> {
                    closeDrawer(navigationView, it)
                    Tools.startActivity(this, ProfileActivity(), false)
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


    private fun setUpPatientsViewPager() {
        fragments.add(PatientsListFragment())
        fragments.add(PatientsListFragment())
        fragments.add(PatientsListFragment())
        adapter =
            PatientsPagerAdapter(
                supportFragmentManager, fragments
            )
        patientsViewPager.adapter = adapter
        patientsViewPager.offscreenPageLimit = 5

        patientsViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                patientsBottomNavigation.menu.getItem(position).isChecked = true
            }
        })
    }

    private fun setUpBottomNavigation() {
        patientsBottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.miConfirmed -> patientsViewPager.currentItem = 0
                R.id.miUnconfirmed -> {
                    patientsViewPager.currentItem = 1
                }
                R.id.miCancelled -> {
                    patientsViewPager.currentItem = 2
                }
            }
            true
        }
    }

}
