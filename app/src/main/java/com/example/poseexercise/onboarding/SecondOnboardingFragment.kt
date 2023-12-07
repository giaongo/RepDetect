package com.example.poseexercise.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.poseexercise.R

/**
 * Fragment class for the second onboarding screen
 */
class SecondOnboardingFragment : Fragment() {

    // Called to create the view for this fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.second_fragment_onboarding, container, false)
    }
}