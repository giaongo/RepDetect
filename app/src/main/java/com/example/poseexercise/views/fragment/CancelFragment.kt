package com.example.poseexercise.views.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.poseexercise.R


class CancelFragment : Fragment() {

    private lateinit var navigateToHomeButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view: View = inflater.inflate(R.layout.fragment_cancel, container, false)

        navigateToHomeButton = view.findViewById(R.id.goToHomeFromCancel)

        navigateToHomeButton.setOnClickListener {

            Navigation.findNavController(view)
                .navigate(R.id.action_cancelFragment_to_homeFragment2)
        }

        return view
    }

}