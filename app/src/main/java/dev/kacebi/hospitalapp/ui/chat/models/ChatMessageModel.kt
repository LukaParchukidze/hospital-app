package dev.kacebi.hospitalapp.ui.chat.models

import java.text.SimpleDateFormat
import java.util.*

data class ChatMessageModel(
    val fromId: String = "",
    val toId: String = "",
    val message: String = "",
    val imageUri: String = "",
    val date: String = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Date())
) {
    override fun toString(): String {
        return "ChatMessageModel(fromId='$fromId', toId='$toId', message='$message', imageUri='$imageUri', date='$date')"
    }
}