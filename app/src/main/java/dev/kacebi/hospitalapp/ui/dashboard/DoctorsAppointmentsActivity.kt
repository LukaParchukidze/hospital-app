package dev.kacebi.hospitalapp.ui.dashboard

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import dev.kacebi.hospitalapp.App
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.tools.Tools
import dev.kacebi.hospitalapp.ui.DoctorAppointmentModel
import dev.kacebi.hospitalapp.ui.DoctorsAppointmentsAdapter
import kotlinx.android.synthetic.main.activity_doctors_appointments.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DoctorsAppointmentsActivity : AppCompatActivity() {

    private lateinit var adapter: DoctorsAppointmentsAdapter
    private val appointments = mutableListOf<DoctorAppointmentModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctors_appointments)

        Tools.setSupportActionBar(this, getString(R.string.appointments))
        init()
    }

    private fun init() {
        doctorsAppointmentsRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = DoctorsAppointmentsAdapter(appointments)
        doctorsAppointmentsRecyclerView.adapter = adapter
        CoroutineScope(Dispatchers.IO).launch {
            val querySnapshot =
                App.dbUsers.document(App.auth.uid!!).collection("doctors").get().await()
            if (querySnapshot != null && !querySnapshot.isEmpty) {
                for (document in querySnapshot.documents) {
                    val appointment = DoctorAppointmentModel(
                        last_name = document["last_name"] as String,
                        time = (document["start_time"] as String + " - " + document["end_time"])
                    )
                    val byteArray = App.storage.child(document.id + ".png").getBytes(1024 * 1024L).await()
                    val bitmapDrawable = BitmapDrawable(resources, BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size))
                    appointment.drawable = bitmapDrawable
                    appointments.add(appointment)
                }
                withContext(Dispatchers.Main) {
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }
}