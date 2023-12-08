package com.example.poseexercise.views.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.poseexercise.R

/**
 * CancelFragment: A fragment displayed when the user cancels an operation.
 *
 * This fragment provides a button to navigate back to the home screen using the Navigation component.
 */
class CancelFragment : Fragment() {
    private lateinit var navigateToHomeButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cancel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigateToHomeButton = view.findViewById(R.id.goToHomeFromCancel)

        // Set up click listener to navigate to the home screen
        navigateToHomeButton.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_cancelFragment_to_homeFragment2)
        }
    }
}