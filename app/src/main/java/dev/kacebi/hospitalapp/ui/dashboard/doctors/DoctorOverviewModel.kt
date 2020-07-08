package dev.kacebi.hospitalapp.ui.dashboard.doctors

import android.graphics.drawable.Drawable

data class DoctorOverviewModel(
    var drawable: Drawable? = null,
    val full_name: String = "",
    val specialty: String = ""
) {
    override fun toString(): String {
        return "DoctorOverviewModel(drawable=$drawable, full_name='$full_name', specialty='$specialty')"
    }
}