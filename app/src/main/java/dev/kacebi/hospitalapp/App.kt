package dev.kacebi.hospitalapp

import android.app.Application
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class App: Application() {

    companion object {
        val auth by lazy {
            FirebaseAuth.getInstance()
        }
        val dbUsers by lazy {
            Firebase.firestore.collection("users")
        }
        val dbDoctors by lazy {
            Firebase.firestore.collection("doctors")
        }
        val dbSpecialties by lazy {
            Firebase.firestore.collection("specialties")
        }
        val dbNews by lazy {
            Firebase.firestore.collection("news")
        }
        val storage by lazy {
            FirebaseStorage.getInstance().reference
        }
        lateinit var instance: App
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        context = applicationContext
    }

}