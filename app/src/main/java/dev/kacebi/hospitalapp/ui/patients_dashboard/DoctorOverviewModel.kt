package dev.kacebi.hospitalapp.ui.patients_dashboard

import android.graphics.Bitmap

data class DoctorOverviewModel(
    var doctorId: String = "",
    var bitmap: Bitmap? = null,
    val full_name: String = "",
    val last_name: String = "",
    val specialty: String = "",
    val working_experience: Long = -1
) {
    override fun toString(): String {
        return "DoctorOverviewModel(drawable=$bitmap, full_name='$full_name', specialty='$specialty')"
    }
}