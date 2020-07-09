package dev.kacebi.hospitalapp.splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log.d
import dev.kacebi.hospitalapp.App
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.tools.Tools
import dev.kacebi.hospitalapp.ui.authentication.LoginActivity
import dev.kacebi.hospitalapp.ui.dashboard.DoctorDashboardActivity
import dev.kacebi.hospitalapp.ui.dashboard.PatientDashboardActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SplashActivity : AppCompatActivity() {

    private var handler = Handler()
    private val runnable = Runnable { init() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable, 5000)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    private fun init() {

        val currentUser = App.auth.currentUser

        if (currentUser != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val uid = currentUser.uid
                val userType = App.dbUsers.document(uid).get().await()["user_type"]

                withContext(Dispatchers.Main) {
                    if (userType != null) {
                        Tools.startActivity(this@SplashActivity, PatientDashboardActivity(),true)
                    } else {
                        Tools.startActivity(this@SplashActivity, DoctorDashboardActivity(),true)
                    }
                }
            }
        } else {
            Tools.startActivity(this, LoginActivity(),true)
        }

    }
}
