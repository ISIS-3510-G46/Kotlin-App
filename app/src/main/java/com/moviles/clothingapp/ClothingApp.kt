package com.moviles.clothingapp // <-- Make sure this package name matches your folders

import android.app.Application
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
// You will need to import BuildConfig. Make sure this path is correct
// It's usually your_package_name.BuildConfig
import com.moviles.clothingapp.BuildConfig

class ClothingApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // ONLY call useEmulator in debug builds!
        if (BuildConfig.DEBUG) {
            // Use the emulator host and port
            // "10.0.2.2" is for the Android emulator
            println("--- Configuring Firestore emulator ---") // Optional log
            //Firebase.firestore.useEmulator("http://34.121.10.209", 8080)
            println("--- Firestore emulator configured ---") // Optional log
        }
        // You can add other app-wide initializations here too
        // e.g., FirebaseApp.initializeApp(this) - though often not needed explicitly
    }
}
