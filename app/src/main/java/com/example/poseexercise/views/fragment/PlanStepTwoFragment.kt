package com.example.poseexercise.views.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.poseexercise.R
import com.example.poseexercise.data.plan.Plan
import com.example.poseexercise.viewmodels.AddPlanViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class PlanStepTwoFragment: Fragment(), CoroutineScope {
    val TAG = "RepDetect Debug"
    private lateinit var addPlanViewModel: AddPlanViewModel
    private var mExerciseName : String? = null
    private var mKcal: Int = 0
    private val selectedDays = mutableListOf<String>()
    private lateinit var days: Array<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        days = resources.getStringArray(R.array.days)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_plan_step_two, container, false)
        // Set the values
        arguments?.let {
            if (it["exerciseName"] != null) {
                mExerciseName = it.getString("exerciseName")
            }
            if (it["caloriesPerRep"] != null) {
                mKcal = it.getInt("caloriesPerRep")
            }
        }
//        val listView = view.findViewById<ListView>(R.id.days_list)
//        val listAdapter = ArrayAdapter(this.requireContext(),android.R.layout.simple_list_item_multiple_choice, days)
//        listView.adapter = listAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "selectedExercise is ${mExerciseName}")
        Log.d(TAG, "calories for each repeat is ${mKcal}")
        // Get all the layout info
        val exerciseEditText = view.findViewById<EditText>(R.id.exercise_name)
        val repeatEditText = view.findViewById<EditText>(R.id.repeat_count)
        val addPlanButton = view.findViewById<Button>(R.id.button)
        val listOfDays = view.findViewById<ListView>(R.id.days_list)

        // Set the default value for the exercise
        exerciseEditText.setText(mExerciseName)
        // Option list for days in week
        val listAdapter = ArrayAdapter(this.requireContext(),android.R.layout.simple_list_item_multiple_choice, days)
        listOfDays.adapter = listAdapter
        // Get the selected days
        listOfDays.setOnItemClickListener { parent, view, position, id ->
            val element = listAdapter.getItem(position)// The item that was clicked
            Log.d(TAG, "click is ${element}")
            if (element != null) {
                if (element in selectedDays) {
                    selectedDays.remove(element)
                    Log.d(TAG, "Removed $element from selectedDays, ${selectedDays}")
                } else {
                    selectedDays.add(element)
                    Log.d(TAG, "Added $element to selectedDays, ${selectedDays}")
                }
            }
        }
        // Saving plan
        addPlanButton.setOnClickListener {
            if(repeatEditText.text.isEmpty() ){
                showErrorMessage()
            }else {
                addPlanViewModel = ViewModelProvider(this).get(AddPlanViewModel::class.java)
                var days = ""
                for(i in selectedDays){
                    days += "$i "
                }
                val repeatCount = repeatEditText.text.toString()
                val newPlan = Plan(0,mExerciseName!!,mKcal,repeatCount.toInt() ,days)
                launch{
                    addPlanViewModel.insert(newPlan)
                }
                view.findNavController().navigate(R.id.action_planStepTwoFragment_to_homeFragment)
            }
        }
    }

    private fun showErrorMessage(){
        Toast.makeText(
            activity,
            "Please fill the form",
            Toast.LENGTH_SHORT
        ).show()
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO
}