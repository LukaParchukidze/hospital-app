package dev.kacebi.hospitalapp.ui.chat.models

import android.graphics.Bitmap

data class ChatsListItemModel(
    val id: String = "",
    var bitmap: Bitmap? = null,
    val name: String = "",
    var latestMessage: String = ""
)