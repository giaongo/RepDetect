package com.example.poseexercise.views.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.poseexercise.R
import com.example.poseexercise.views.fragment.WorkOutFragment
import com.example.poseexercise.views.fragment.HomeFragment
import com.example.poseexercise.views.fragment.PlanStepOneFragment
import com.example.poseexercise.views.fragment.ProfileFragment

class MainActivity : AppCompatActivity() {
    private lateinit var prefManager: PrefManager
    private lateinit var bottomNavigation: MeowBottomNavigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefManager = PrefManager(this)
        if (prefManager.isFirstTimeLaunch()) {
            // Show the onboarding screen
            startActivity(Intent(this, OnboardingActivity::class.java))
            // Close the main activity
            finish()
        }

        bottomNavigation = findViewById(R.id.bottomNavigation)
        switchFragment(HomeFragment())

        // Sets home page as default page and adds clicked animation to home button
        bottomNavigation.show(1, true)
        setupBottomNavigation()
        handleBottomNavigation()

        /*val cameraButton = findViewById<Button>(R.id.Camera)

        cameraButton.setOnClickListener{
            startActivity(Intent(this, CameraXLivePreviewActivity::class.java))
        }*/
    }

    private fun setupBottomNavigation() {
        bottomNavigation.add(MeowBottomNavigation.Model(1, R.drawable.home))
        bottomNavigation.add(MeowBottomNavigation.Model(2, R.drawable.workout))
        bottomNavigation.add(MeowBottomNavigation.Model(3, R.drawable.plan))
        bottomNavigation.add(MeowBottomNavigation.Model(4, R.drawable.profile))
    }

    private fun handleBottomNavigation(){
        bottomNavigation.setOnClickMenuListener { item ->
            when (item.id) {
                1 -> switchFragment(HomeFragment())
                2 -> switchFragment(WorkOutFragment())
                3 -> switchFragment(PlanStepOneFragment())
                4 -> switchFragment(ProfileFragment())
            }
        }
    }

    private fun switchFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.addToBackStack(null) // Optional: Add the transaction to the back stack
        fragmentTransaction.commit()
    }
}