package com.example.poseexercise.views.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.poseexercise.R
import com.example.poseexercise.viewmodels.AddPlanViewModel

class PlanStepTwoFragment: Fragment() {
    val TAG = "RepDetect Debug"
    private lateinit var addPlanViewModel: AddPlanViewModel
    private var mExerciseName : String? = null
    private var mKcal: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Set the values
        arguments?.let{
            if(it["exerciseName"] != null){
                mExerciseName = it.getString("exerciseName")
            }
            if(it["caloriesPerRep"] != null){
                mKcal = it.getInt("caloriesPerRep")
            }
        }
        val view = inflater.inflate(R.layout.fragment_plan_step_two, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "selectedExercise is ${mExerciseName}")
        // Get all the layout info
        val exerciseEditText = view.findViewById<EditText>(R.id.exercise_name)
        val repeatEditText = view.findViewById<EditText>(R.id.repeat_count)
        val addPlanButton = view.findViewById<Button>(R.id.button)
        val monCheckBox = view.findViewById<CheckBox>(R.id.checkbox_child_1)
        val tueCheckBox = view.findViewById<CheckBox>(R.id.checkbox_child_2)
        val wedCheckBox = view.findViewById<CheckBox>(R.id.checkbox_child_3)
        val thuCheckBox = view.findViewById<CheckBox>(R.id.checkbox_child_4)
        val friCheckBox = view.findViewById<CheckBox>(R.id.checkbox_child_5)
        val satCheckBox = view.findViewById<CheckBox>(R.id.checkbox_child_6)
        val sunCheckBox = view.findViewById<CheckBox>(R.id.checkbox_child_7)
        // Set the default value for the exercise
        exerciseEditText.setText(mExerciseName)

    }

}