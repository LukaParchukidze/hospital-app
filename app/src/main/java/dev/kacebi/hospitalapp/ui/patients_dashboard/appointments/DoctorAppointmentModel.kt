package dev.kacebi.hospitalapp.ui.patients_dashboard.appointments

import android.graphics.drawable.Drawable

data class DoctorAppointmentModel(
    var drawable: Drawable? = null,
    val doctorId: String = "",
    var status: String = "",
    val last_name: String = "",
    val start_time: String = "",
    val end_time: String = "",
    val time: String = ""
)