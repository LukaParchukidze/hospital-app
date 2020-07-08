package dev.kacebi.hospitalapp.ui.dashboard.home.news

import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import androidx.core.graphics.drawable.toBitmap

class NewsModel(){
    val author: String = ""
    val date: String = ""
    val description: String = ""
    val image_uri:String = ""
    val title: String = ""

    var drawable: Drawable? = null

    override fun toString(): String {
        return "NewsModel(author='$author', date='$date', description='$description', image_uri='$image_uri', title='$title', drawable=$drawable)"
    }
}
