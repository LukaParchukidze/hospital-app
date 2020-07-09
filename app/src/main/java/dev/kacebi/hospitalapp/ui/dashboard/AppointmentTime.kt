package dev.kacebi.hospitalapp.ui.dashboard

data class AppointmentTime(
    val start_time: String = "",
    val end_time: String = "",
    var available: Boolean = true
)