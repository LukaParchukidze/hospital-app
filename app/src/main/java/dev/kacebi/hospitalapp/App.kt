package dev.kacebi.hospitalapp

import android.app.Application
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class App: Application() {

    companion object {
        val firebase by lazy {
            FirebaseAuth.getInstance()
        }
        val dbRef by lazy {
            Firebase.firestore.collection("users")
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