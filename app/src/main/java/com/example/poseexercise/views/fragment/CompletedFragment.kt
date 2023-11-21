package com.example.poseexercise.views.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.poseexercise.R
import com.example.poseexercise.views.activity.MainActivity

class CompletedFragment : Fragment() {

    private lateinit var navigateToHomeButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_completed, container, false)
        navigateToHomeButton = view.findViewById(R.id.goToHomeFromComplete)
        navigateToHomeButton.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_completedFragment_to_homeFragment2)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val workoutResultText: TextView = view.findViewById(R.id.workoutResult_textView)
        val workoutTimerText: TextView = view.findViewById(R.id.workoutTimer_textView)

        // Display the timer of the workout
        MainActivity.workoutTimer?.let {
            workoutTimerText.text = getString(R.string.workoutResultDisplay, it)
        }

        // Display the result of the workout
        MainActivity.workoutResultData?.let {
            workoutResultText.text = getString(R.string.workoutResultDisplay, it)
        } ?: run {
            workoutResultText.text = getString(R.string.noWorkoutResultDisplay)
        }

        MainActivity.apply {
            workoutResultData = null
            workoutResultData = null
        }

    }

}