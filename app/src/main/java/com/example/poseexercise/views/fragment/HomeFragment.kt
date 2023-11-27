package com.example.poseexercise.views.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.poseexercise.R
import com.example.poseexercise.adapters.PlanAdapter
import com.example.poseexercise.adapters.RecentActivityAdapter
import com.example.poseexercise.data.database.AppRepository
import com.example.poseexercise.data.plan.Plan
import com.example.poseexercise.data.results.RecentActivityItem
import com.example.poseexercise.data.results.WorkoutResult
import com.example.poseexercise.util.MyApplication
import com.example.poseexercise.util.MyUtils
import com.example.poseexercise.viewmodels.AddPlanViewModel
import com.example.poseexercise.viewmodels.HomeViewModel
import com.example.poseexercise.viewmodels.ResultViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Date
import kotlin.math.min

class HomeFragment : Fragment(), PlanAdapter.ItemListener {
    val TAG = "RepDetect Home Fragment"
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var resultViewModel: ResultViewModel
    private lateinit var recentActivityRecyclerView: RecyclerView
    private lateinit var recentActivityAdapter: RecentActivityAdapter
    private var planList: List<Plan>? = emptyList()
    private var notCompletePlanList: MutableList<Plan>? = Collections.emptyList()
    private var today: String = DateFormat.format("EEEE", Date()) as String
    private var percentage: Int = 0
    private lateinit var progressText: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var noPlanTV: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var progressPercentage: TextView
    private var workoutResults: List<WorkoutResult>? = null
    private lateinit var appRepository: AppRepository
    private lateinit var addPlanViewModel: AddPlanViewModel
    private lateinit var adapter: PlanAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "TODAY IS $today")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize RecyclerView and its adapter for recent activity
        progressText = view.findViewById(R.id.exercise_left)
        recyclerView = view.findViewById(R.id.today_plans)
        recentActivityRecyclerView = view.findViewById(R.id.recentActivityRecyclerView)
        recentActivityAdapter = RecentActivityAdapter(emptyList())
        recentActivityRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        recentActivityRecyclerView.adapter = recentActivityAdapter
        noPlanTV = view.findViewById(R.id.no_plan)
        progressBar = view.findViewById(R.id.progress_bar)
        progressPercentage = view.findViewById(R.id.progress_text)
        appRepository = AppRepository(requireActivity().application)
        // Initialize ViewModel
        resultViewModel = ResultViewModel(MyApplication.getInstance())
        addPlanViewModel = AddPlanViewModel(MyApplication.getInstance())
        lifecycleScope.launch {
            val workoutResults = resultViewModel.getAllResult()
            // Call the function to load data and set up the chart
            loadDataAndSetupChart()
            // Sort WorkoutResult objects by timestamp in descending order
            val sortedWorkoutResults = workoutResults?.sortedByDescending { it.timestamp }
            // Transform WorkoutResult objects into RecentActivityItem objects
            val imageResources = arrayOf(R.drawable.blue, R.drawable.green, R.drawable.orange)
            // Transform WorkoutResult objects into RecentActivityItem objects
            val recentActivityItems = sortedWorkoutResults?.mapIndexed { index, it ->
                RecentActivityItem(
                    imageResId = imageResources[index % imageResources.size],
                    exerciseType = MyUtils.exerciseNameToDisplay(it.exerciseName),
                    reps = "${it.repeatedCount} reps"
                )
            }
            // Update the adapter with the transformed data
            recentActivityAdapter.updateData(recentActivityItems ?: emptyList())
            // Check if the recentActivityItems list is empty
            if (recentActivityItems.isNullOrEmpty()) {
                recentActivityRecyclerView.isVisible = false
                // Show a message or handle the empty case as per your UI requirements
                val noActivityMessage = view.findViewById<TextView>(R.id.no_activity_message)
                noActivityMessage.text = getString(R.string.no_activities_yet)
                noActivityMessage.isVisible = true
            } else {
                recentActivityRecyclerView.isVisible = true
            }
        }
        // Initialize home view model, RecyclerView and its adapter for today's plans
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        // get the list of plans from database
        lifecycleScope.launch(Dispatchers.IO) {
            val result1 = withContext(Dispatchers.IO) { homeViewModel.getPlanByDay(today) }
            val result2 = withContext(Dispatchers.IO) { homeViewModel.getNotCompletePlans(today) }
            withContext(Dispatchers.Main) {
                updateResultFromDatabase(result1, result2)
            }
        }
    }

    private fun updateResultFromDatabase(plan: List<Plan>?, notCompleted: MutableList<Plan>?) {
        planList = plan
        notCompletePlanList = notCompleted
        adapter = PlanAdapter(requireContext())
        planList?.let {
            if (it.isNotEmpty()) {
                it.map { plan ->
                    if (plan.timeCompleted?.let { it1 -> getDayCompleted(it1) } != today) {
                        lifecycleScope.launch {
                            addPlanViewModel.updateComplete(false, null, plan.id)
                            Log.d(TAG, "update complete status for plan")
                        }
                    }
                    Log.d(TAG, "Exercise is ${plan.exercise}")
                    Log.d(
                        TAG,
                        "plan completed on ${plan.timeCompleted?.let { it1 -> getDayCompleted(it1) }}"
                    )
                }
            } else {
                Log.d(TAG, "plan list is empty")
            }
        }

//        Display the not completed plans
        progressText.text = "${notCompletePlanList?.size} exercise left"
        if (notCompletePlanList?.isEmpty() == true) {
            noPlanTV.text = getString(R.string.there_is_no_plan_set_at_the_moment)
        }
        recyclerView.adapter = adapter
        adapter.setListener(this)
        notCompletePlanList?.let { adapter.setPlans(it) }
        recyclerView.visibility = if (notCompletePlanList?.isNotEmpty() == true) View.VISIBLE else View.GONE
        progressBar.progress = percentage
        progressPercentage.text = percentage.toString()
        Log.d(TAG, "Progress is $percentage")
    }


    private fun loadDataAndSetupChart() {
        lifecycleScope.launch(Dispatchers.IO) {
            // Fetch workout results asynchronously
            workoutResults = resultViewModel.getAllResult()
            // Filter workout results for today
            val todayWorkoutResults = workoutResults?.filter {
                isToday()
            }
            // Observe exercise plans from the database
            withContext(Dispatchers.Main) {
                appRepository.allPlans.observe(viewLifecycleOwner) { exercisePlans ->
                    // Filter exercise plans for today
                    val todayExercisePlans = exercisePlans?.filter { isTodayPlan(it.selectedDays) }
                    // Calculate progress and update UI
                    val totalPlannedRepetitions = todayExercisePlans?.sumOf { it.repeatCount } ?: 0
                    val totalCompletedRepetitions =
                        todayWorkoutResults?.sumOf { it.repeatedCount } ?: 0
                    val progressPercentage = if (totalPlannedRepetitions != 0) {
                        ((totalCompletedRepetitions.toDouble() / totalPlannedRepetitions) * 100).toInt()
                    } else {
                        0
                    }
                    // Update the ProgressBar and TextView with the progress percentage
                    updateProgressViews(progressPercentage)
                }
            }
        }
    }

    private fun isToday(): Boolean {
        val todayCalendar = Calendar.getInstance()
        // Compare the current day with the day of the plans
        return isDaySelected(todayCalendar.get(Calendar.DAY_OF_WEEK).toString())
    }

    private fun isTodayPlan(selectedDays: String): Boolean {
        return isDaySelected(Calendar.getInstance().get(Calendar.DAY_OF_WEEK).toString())
    }

    private fun isDaySelected(day: String): Boolean {
        val todayCalendar = Calendar.getInstance()
        // Check if the selected day matches the current day of the week
        return day.contains(todayCalendar.get(Calendar.DAY_OF_WEEK).toString())
    }

    // Function to update progress views (ProgressBar and TextView)
    private fun updateProgressViews(progressPercentage: Int) {
        // Check if progressPercentage is greater than 0
        if (progressPercentage > 0) {
            // Update progress views (ProgressBar and TextView)
            val cappedProgress = min(progressPercentage, 100)
            val progressBar = view?.findViewById<ProgressBar>(R.id.progress_bar)
            val progressTextView = view?.findViewById<TextView>(R.id.progress_text)
            progressBar?.progress = cappedProgress
            progressTextView?.text = String.format("%d%%", cappedProgress)
        } else {
            // If progressPercentage is 0 or less, hide the progress views
            val progressBar = view?.findViewById<ProgressBar>(R.id.progress_bar)
            val progressTextView = view?.findViewById<TextView>(R.id.progress_text)
            progressBar?.visibility = View.GONE
            progressTextView?.visibility = View.GONE
        }
    }

    // Get the day from which the plan was marked as complete
    private fun getDayCompleted(time: Long): String? {
        return try {
            val sdf = SimpleDateFormat("EEEE")
            val netDate = Date(time)
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }

    // Delete the plan when user click on delete icon
    override fun onItemClicked(planId: Int, position: Int) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        // Show a dialog for user to confirm the choice
        builder
            .setMessage("Are you sure you want to delete the plan?")
            .setTitle("Delete plan")
            .setPositiveButton("Delete") { dialog, which ->
                // Delete the plan from database
                lifecycleScope.launch {
                    addPlanViewModel.deletePlan(planId)
                }
                Log.d(TAG,"Delete plan $planId" )
//                adapter.notifyItemRemoved(position)
                Log.d(TAG, "Adapter position is $position")
                notCompletePlanList?.removeAt(position)
                adapter.notifyItemRemoved(position)
                adapter.notifyDataSetChanged()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                // Cancel the action
                dialog.dismiss()
            }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}
