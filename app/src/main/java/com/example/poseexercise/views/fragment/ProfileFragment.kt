package com.example.poseexercise.views.fragment

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.poseexercise.R
import com.example.poseexercise.data.results.WorkoutResult
import com.example.poseexercise.util.MyApplication
import com.example.poseexercise.viewmodels.ResultViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ProfileFragment : Fragment() {
    private lateinit var resultViewModel: ResultViewModel
    private lateinit var chart: BarChart
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        resultViewModel = ResultViewModel(MyApplication.getInstance())
        chart = view.findViewById(R.id.chart)
        loadDataAndSetupChart()
    }

    private fun loadDataAndSetupChart() {
        lifecycleScope.launch(Dispatchers.IO) {
            val workoutResults = resultViewModel.getAllResult()
            // Use if needed to get precise date: To be removed later
            workoutResults?.forEach {
                val date = formattedDate(it.timestamp)
                //Log.d("my_date", date)
            }
            val totalCaloriesPerDay = workoutResults?.let { calculateTotalCaloriesPerDay(it) }
            if (totalCaloriesPerDay != null) {
                updateChart(totalCaloriesPerDay)
            }
        }
    }

    private fun calculateTotalCaloriesPerDay(workoutResults: List<WorkoutResult>): Map<String, Double> {
        val totalCaloriesPerDay = mutableMapOf<String, Double>()

        // Initialize entries for each day of the week
        val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        for (day in daysOfWeek) {
            totalCaloriesPerDay[day] = 0.0
        }

        for (result in workoutResults) {
            val startDate = getStartOfDay(result.timestamp)
            val key = formatDate(startDate)
            val totalCalories = totalCaloriesPerDay.getOrDefault(key, 0.0) + result.calorie
            totalCaloriesPerDay[key] = totalCalories
        }

        return totalCaloriesPerDay
    }

    // Function to get the start of the day
    private fun getStartOfDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp

        // Set the calendar to the start of the day
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.timeInMillis
    }


    // Returns starting day of the bar as Sun
    private fun formatDate(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("EEE", Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }

    // Format days in this sample format: 2023-11-16 15:51:54
    private fun formattedDate(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }

    private fun updateChart(totalCaloriesPerWeek: Map<String, Double>) {
        val totalCalories = totalCaloriesPerWeek.values.sum()

        // Update the total calories TextView
        val totalCaloriesTextView = view?.findViewById<TextView>(R.id.totalCaloriesTextView)
        totalCaloriesTextView?.text =
            String.format(Locale.getDefault(), getString(R.string.total_calories), totalCalories)

        val entries = totalCaloriesPerWeek.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat())
        }

        val labels = totalCaloriesPerWeek.keys.toList()
        val dataSet = BarDataSet(entries, "Total Calories per Week")
        dataSet.colors = getBarColors(totalCaloriesPerWeek)
        val data = BarData(dataSet)
        chart.data = data

        // Set custom labels for each bar
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)

        // Set X-axis position to the bottom
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM

        chart.legend.isEnabled = false

        // Set description to null
        chart.description = null

        // Align the starting point of the y-axis with the x-axis
        chart.axisLeft.axisMinimum = 0f

        // Set Y-Axis (Right) values
        chart.axisRight.apply {
            setDrawGridLines(false)
            setDrawLabels(false)
            setDrawAxisLine(false)
        }
        // Set Y-Axis (Left) values
        chart.axisLeft.apply {
            setDrawGridLines(false)
            setDrawAxisLine(false)
        }

        // Set X-Axis values
        chart.xAxis.apply {
            setDrawGridLines(false)
            setDrawAxisLine(false)
        }
        for (i in entries.indices) {
            val value = totalCaloriesPerWeek[labels[i]]
            if (value != null && value == 0.0) {
                // Set text size to 0 to hide the values
                dataSet.valueTextSize = 0f
                break
            }
        }
        chart.invalidate()
    }

    private fun getBarColors(totalCaloriesPerWeek: Map<String, Double>): List<Int> {
        val primaryColor = ContextCompat.getColor(requireContext(), R.color.primaryColor)

        return totalCaloriesPerWeek
            .map { (key, value) -> if (value > 0) primaryColor else Color.TRANSPARENT }
    }
}

