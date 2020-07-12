package dev.kacebi.hospitalapp.ui.doctors_dashboard

import android.graphics.drawable.Drawable

data class PatientAppointmentModel(
    var drawable: Drawable? = null,
    val patientId: String = "",
    val full_name: String = "",
    val start_time: String = "",
    val end_time: String = "",
    val status: String = ""
)