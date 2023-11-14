package com.example.poseexercise.util

import android.app.Application

class MyApplication : Application() {
    companion object {
        private lateinit var instance: MyApplication

        fun getInstance(): MyApplication {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}