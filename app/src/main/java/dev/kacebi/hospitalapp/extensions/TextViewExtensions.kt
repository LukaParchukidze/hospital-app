package dev.kacebi.hospitalapp.extensions

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView

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