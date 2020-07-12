package dev.kacebi.hospitalapp.ui.patients_dashboard

import android.graphics.drawable.Drawable

data class DoctorOverviewModel(
    var doctorId: String = "",
    var drawable: Drawable? = null,
    val full_name: String = "",
    val last_name: String = "",
    val specialty: String = ""
) {
    override fun toString(): String {
        return "DoctorOverviewModel(drawable=$drawable, full_name='$full_name', specialty='$specialty')"
    }
}