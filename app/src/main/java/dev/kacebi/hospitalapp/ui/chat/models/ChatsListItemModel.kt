package dev.kacebi.hospitalapp.ui.chat.models

import android.graphics.drawable.Drawable

data class ChatsListItemModel(
    val id: String = "",
    var drawable: Drawable? = null,
    val name: String = "",
    var latestMessage: String = ""
)