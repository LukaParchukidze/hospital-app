package dev.kacebi.hospitalapp.ui.patients_dashboard.home.news

import android.graphics.Bitmap

class NewsModel(){
    val author: String = ""
    val date: String = ""
    val description: String = ""
    val image_uri:String = ""
    val title: String = ""
    var bitmap: Bitmap? = null

    override fun toString(): String {
        return "NewsModel(author='$author', date='$date', description='$description', image_uri='$image_uri', title='$title', bitmap=$bitmap)"
    }
}
