package com.example.poseexercise.views.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.poseexercise.R

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
        }, 3000)// Delay for 3000 milliseconds (3 seconds)
    }
}
