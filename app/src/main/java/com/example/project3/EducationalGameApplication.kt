package com.example.project3

import android.app.Application
import com.example.project3.data.UserRepository

class EducationalGameApplication: Application() {
    // Needed to create ViewModels with the ViewModelProvider.Factory
    lateinit var userRepository: UserRepository

    // For onCreate() to run, android:name=".StudyHelperApplication" must
    // be added to <application> in AndroidManifest.xml
    override fun onCreate() {
        super.onCreate()
        userRepository = UserRepository(this.applicationContext)
    }
}