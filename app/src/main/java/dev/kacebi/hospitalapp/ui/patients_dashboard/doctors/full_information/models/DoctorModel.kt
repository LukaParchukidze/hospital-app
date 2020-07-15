package dev.kacebi.hospitalapp.ui.patients_dashboard.doctors.full_information.models

import android.graphics.Bitmap

data class DoctorModel(
    val full_name: String = "",
    val email: String = "",
    val age: Int = -1,
    val specialty: String = "",
    val start_time: String = "",
    val end_time: String = "",
    val about: String = "",
    val working_experience: Int = -1,
    var phone_numbers: List<String>? = null,
    var bitmap: Bitmap? = null
) {
    override fun toString(): String {
        return "DoctorModel(full_name='$full_name', email='$email', age=$age, specialty='$specialty', start_time='$start_time', end_time='$end_time', about='$about', working_experience=$working_experience, phone_numbers=$phone_numbers, bitmap=$bitmap)"
    }
}