package dev.kacebi.hospitalapp.ui.patients_dashboard.home

import android.graphics.drawable.Drawable

data class SpecialtyModel(
    val specialty: String = "",
    val uri: String = "",
    var drawable: Drawable? = null
)