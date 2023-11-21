package com.example.poseexercise.views.activity

import com.example.poseexercise.onboarding.OnboardingPagerAdapter
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.poseexercise.onboarding.FirstOnboardingFragment
import com.example.poseexercise.R
import com.example.poseexercise.onboarding.SecondOnboardingFragment
import com.example.poseexercise.onboarding.ThirdOnboardingFragment
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class OnboardingActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var prefManager: PrefManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        // Find and initialize UI components
        val  nextButton = findViewById<Button>(R.id.nextButton)
        prefManager = PrefManager(this)
        // List of onboarding fragments
        val fragments = listOf(
            FirstOnboardingFragment(),
            SecondOnboardingFragment(),
            ThirdOnboardingFragment()
        )
        viewPager = findViewById(R.id.viewPager)

        // Set up the adapter for the ViewPager
        val onboardingAdapter = OnboardingPagerAdapter(this)
        viewPager.adapter = onboardingAdapter

        // Initialize and attach the DotsIndicator
        val indicator = findViewById<DotsIndicator>(R.id.dots_indicator)
        indicator.attachTo(viewPager)

        nextButton.setOnClickListener {
            // Handle "Next" button click
            if (viewPager.currentItem < fragments.size - 1) {
                viewPager.currentItem++
            } else {
                // Set the flag to indicate onboarding completion
                prefManager.setFirstTimeLaunch(false)

                // Navigate to MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            //Update the button text to "Get Started" on the last fragment
            if (viewPager.currentItem == fragments.size - 1) {
                nextButton.text = getString(R.string.get_started)
            }
        }
    }
}

class PrefManager(context: Context) {
    private val pref: SharedPreferences = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE)

    fun isFirstTimeLaunch(): Boolean {
        return pref.getBoolean("isFirstTimeLaunch", true)
    }

    fun setFirstTimeLaunch(isFirstTime: Boolean) {
        pref.edit().putBoolean("isFirstTimeLaunch", isFirstTime).apply()
    }
}