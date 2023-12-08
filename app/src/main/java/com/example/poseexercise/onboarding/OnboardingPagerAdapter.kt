package com.example.poseexercise.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Define the OnboardingPagerAdapter class, responsible for managing onboarding fragments
 */
class OnboardingPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    // List of fragments to be displayed in the view pager
    private val fragments = listOf(
        FirstOnboardingFragment(),
        SecondOnboardingFragment(),
        ThirdOnboardingFragment()
    )

    // Return the number of fragments in the list
    override fun getItemCount(): Int = fragments.size

    // Create and return a fragment based on its position
    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}