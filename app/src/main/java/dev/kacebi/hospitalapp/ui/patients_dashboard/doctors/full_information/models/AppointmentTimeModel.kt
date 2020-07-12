package dev.kacebi.hospitalapp.ui.patients_dashboard.doctors.full_information.models

data class AppointmentTimeModel(
    val start_time: String = "",
    val end_time: String = "",
    var available: Boolean = true
)