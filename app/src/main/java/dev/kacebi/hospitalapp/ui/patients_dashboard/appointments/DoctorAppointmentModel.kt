package dev.kacebi.hospitalapp.ui.patients_dashboard.appointments

import android.graphics.Bitmap

data class DoctorAppointmentModel(
    var bitmap: Bitmap? = null,
    val doctorId: String = "",
    var status: String = "",
    val last_name: String = "",
    val start_time: String = "",
    val end_time: String = "",
    val time: String = ""
)