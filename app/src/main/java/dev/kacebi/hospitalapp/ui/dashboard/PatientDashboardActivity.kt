package dev.kacebi.hospitalapp.ui.dashboard

import android.os.Bundle
import android.util.Log.d
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.kacebi.hospitalapp.App
import dev.kacebi.hospitalapp.R
import kotlinx.android.synthetic.main.activity_patient_dashboard.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PatientDashboardActivity : AppCompatActivity() {

    private lateinit var toggle: ActionBarDrawerToggle
//    private val storageRef = FirebaseStorage.getInstance().reference
//    private val specialties = mutableListOf<SpecialtyModel>()
//    private lateinit var adapter: SpecialtiesAdapter

    private val homeFragment = HomeFragment()
    private val doctorsFragment = DoctorsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_dashboard)

        setUpToolbar()
        setUpDrawerMenu()
//        setUpViewPager()
        setUpBottomNavigation()

        supportFragmentManager.beginTransaction()
            .add(R.id.dashboardFragmentContainer, doctorsFragment).hide(doctorsFragment).commit()
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
                R.id.miHome -> goToFragment(homeFragment, doctorsFragment)
                R.id.miDoctors -> goToFragment(doctorsFragment, homeFragment)
            }
            true
        }
    }

    private fun goToFragment(fragment1: Fragment, fragment2: Fragment) {
        supportFragmentManager.beginTransaction().show(fragment1).hide(fragment2).commit()
    }

}