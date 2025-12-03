package com.example.project3

import android.app.Application
import com.example.project3.data.UserRepository

class EducationalGameApplication: Application() {
    lateinit var userRepository: UserRepository

    override fun onCreate() {
        super.onCreate()
        userRepository = UserRepository(this.applicationContext)
    }
}