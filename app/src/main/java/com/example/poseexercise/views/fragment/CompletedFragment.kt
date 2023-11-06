package com.example.poseexercise.views.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.poseexercise.R

class CompletedFragment : Fragment() {

    private lateinit var testNavigation: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_completed, container, false)

        testNavigation = view.findViewById(R.id.goToHomeFromComplete)

        testNavigation.setOnClickListener {

            Navigation.findNavController(view)
                .navigate(R.id.action_completedFragment_to_homeFragment2)
        }

        return view
    }

}