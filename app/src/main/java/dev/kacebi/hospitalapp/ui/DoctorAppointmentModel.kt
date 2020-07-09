package dev.kacebi.hospitalapp.ui

import android.graphics.drawable.Drawable

data class DoctorAppointmentModel(
    var drawable: Drawable? = null,
    val last_name: String = "",
    val time: String = ""
)