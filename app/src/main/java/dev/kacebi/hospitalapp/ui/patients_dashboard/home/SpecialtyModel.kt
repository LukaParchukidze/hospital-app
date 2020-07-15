package dev.kacebi.hospitalapp.ui.patients_dashboard.home

import android.graphics.Bitmap

data class SpecialtyModel(
    val specialty: String = "",
    val uri: String = "",
    var bitmap: Bitmap? = null
)