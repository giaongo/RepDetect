package com.example.poseexercise.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.poseexercise.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ThirdOnboardingFragment : Fragment() {
    // Declare variables to hold fragment arguments (if needed)
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve fragment arguments and initialize variables
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.third_fragment_onboarding, container, false)
    }
}