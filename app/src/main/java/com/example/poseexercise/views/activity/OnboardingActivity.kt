package com.example.poseexercise.views.activity

import OnboardingPagerAdapter
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.poseexercise.FirstOnboardingFragment
import com.example.poseexercise.R
import com.example.poseexercise.SecondOnboardingFragment
import com.example.poseexercise.ThirdOnboardingFragment

class OnboardingActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var prefManager: PrefManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        val  nextButton = findViewById<Button>(R.id.nextButton)
        prefManager = PrefManager(this)
        val fragments = listOf(
            FirstOnboardingFragment(),
            SecondOnboardingFragment(),
            ThirdOnboardingFragment()
        )
        viewPager = findViewById(R.id.viewPager)
        val onboardingAdapter = OnboardingPagerAdapter(this)
        viewPager.adapter = onboardingAdapter

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
                nextButton.text = "Get Started"
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