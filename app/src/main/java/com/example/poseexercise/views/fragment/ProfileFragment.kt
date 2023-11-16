package com.example.poseexercise.views.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.poseexercise.R
import com.example.poseexercise.util.MyApplication
import com.example.poseexercise.viewmodels.ResultViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileFragment : Fragment() {
    private lateinit var resultViewModel: ResultViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        resultViewModel = ResultViewModel(MyApplication.getInstance())
        lifecycleScope.launch(Dispatchers.IO) {
            val workoutResults = resultViewModel.getAllResult()

            // Calculate and print the sum of calories for each day
            val dailyCaloriesList = workoutResults
                ?.groupBy { result -> result.timestamp.toDateString() } // Group by date
                ?.map { (date, results) ->
                    DailyCalories(date, results.sumOf { it.calorie })
                }

            dailyCaloriesList?.forEach {
                Log.d(
                    "Daily Calories",
                    "Date: ${it.date}, Total Calories: %.2f".format(it.totalCalories)
                )
            }
        }
    }

    data class DailyCalories(val date: String, val totalCalories: Double)

    // Extension function to convert timestamp to date string
    private fun Long.toDateString(): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return simpleDateFormat.format(Date(this))
    }
}
