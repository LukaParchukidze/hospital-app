package dev.kacebi.hospitalapp.extensions

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.core.content.ContextCompat

fun TextView.setSpannableText(text: String, color: Int) {
    val spannableString = SpannableString(text)
    spannableString.setSpan(
        ForegroundColorSpan(color),
        0,
        spannableString.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    append(spannableString)
}

fun TextView.setNewColor(context: Context, color: Int){
    setTextColor(ContextCompat.getColor(context, color))
}