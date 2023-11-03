package com.example.poseexercise.views.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.poseexercise.R
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var prefManager: PrefManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefManager = PrefManager(this)

        if (prefManager.isFirstTimeLaunch()) {
            // Show the onboarding screen
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()  // Close the main activity
        }

        val cameraButton = findViewById<Button>(R.id.Camera)

        cameraButton.setOnClickListener{

            startActivity(Intent(this, CameraXLivePreviewActivity::class.java))
        }

    }


}