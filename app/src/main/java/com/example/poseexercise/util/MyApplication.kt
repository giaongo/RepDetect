package com.example.poseexercise.util

import android.app.Application

class MyApplication : Application() {
    // Using the companion object to create a singleton instance of MyApplication
    companion object {
        private lateinit var instance: MyApplication

        fun getInstance(): MyApplication {
            return instance
        }
    }

    // Override the onCreate method to initialize the instance variable
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}