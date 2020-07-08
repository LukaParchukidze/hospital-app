package dev.kacebi.hospitalapp.ui.dashboard.home.specialties

import android.graphics.drawable.Drawable

data class SpecialtyModel(
    val specialty: String = "",
    val uri: String = "",
    var drawable: Drawable? = null
)