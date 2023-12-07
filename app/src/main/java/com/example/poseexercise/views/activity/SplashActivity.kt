package com.example.poseexercise.views.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.poseexercise.R

/**
 * SplashActivity: A simple splash screen that appears when the app is launched.
 *
 * This activity displays a splash screen for a specified duration and then navigates
 * to the MainActivity.
 */
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Use a Handler to delay the intent navigation
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)

            // Start the MainActivity and finish this SplashActivity
            startActivity(intent)
            finish()
        }, 1000)// Delay for 1000 milliseconds (1 seconds)
    }
}
