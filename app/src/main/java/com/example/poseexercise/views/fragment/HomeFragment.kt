package com.example.poseexercise.views.fragment

import android.content.Context
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
import com.example.poseexercise.data.plan.Plan
import com.example.poseexercise.data.results.RecentActivityItem
import com.example.poseexercise.util.MyApplication
import com.example.poseexercise.viewmodels.HomeViewModel
import com.example.poseexercise.viewmodels.ResultViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.coroutines.CoroutineContext


class HomeFragment : Fragment(), CoroutineScope {
    val TAG = "RepDetect Home Fragment"
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var resultViewModel: ResultViewModel
    private lateinit var recentActivityRecyclerView: RecyclerView
    private lateinit var recentActivityAdapter: RecentActivityAdapter
    private var planList: List<Plan>? = emptyList()
    private var notCompletePlanList: List<Plan>? = emptyList()
    private var today : String = DateFormat.format("EEEE" , Date()) as String
    private var percentage: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val activity = activity as Context
        Log.d(TAG, "TODAY IS $today")
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize RecyclerView and its adapter for recent activity
        recentActivityRecyclerView = view.findViewById(R.id.recentActivityRecyclerView)
        recentActivityAdapter = RecentActivityAdapter(emptyList())
        recentActivityRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        recentActivityRecyclerView.adapter = recentActivityAdapter

        // Initialize ViewModel
        resultViewModel = ResultViewModel(MyApplication.getInstance())
        lifecycleScope.launch {
            val workoutResults = resultViewModel.getAllResult()

            // Transform WorkoutResult objects into RecentActivityItem objects
            val imageResources = arrayOf(R.drawable.blue, R.drawable.green, R.drawable.orange)

            // Transform WorkoutResult objects into RecentActivityItem objects
            val recentActivityItems = workoutResults?.mapIndexed { index, it ->
                RecentActivityItem(
                    imageResId = imageResources[index % imageResources.size],
                    exerciseType = it.exerciseName,
                    reps = "${it.repeatedCount} reps"
                )
            }

            // Update the adapter with the transformed data
            recentActivityAdapter.updateData(recentActivityItems ?: emptyList())
        }

        // Initialize home view model, RecyclerView and its adapter for today's plans
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        val progressText = view.findViewById<TextView>(R.id.exercise_left)
        val recyclerView= view.findViewById<RecyclerView>(R.id.today_plans)
        val noPlanTV = view.findViewById<TextView>(R.id.no_plan)
        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
        val progressPercentage = view.findViewById<TextView>(R.id.progress_text)
        val adapter = PlanAdapter(activity)
        recyclerView.adapter = adapter

        // get the list of plans from database]
        launch{
            planList = homeViewModel.getPlanByDay(today)
            notCompletePlanList = homeViewModel.getNotCompletePlans(today)
            planList?.map {
                Log.d(TAG, "Exercise is ${it.exercise}")
            }
            planList?.let { adapter.setPlans(it) }
            progressText.text = "${notCompletePlanList?.size} exercise left"
            if(planList?.size == 0){
                recyclerView.isVisible = false
                noPlanTV.text = "There is no plan set at the moment"
            }
            if(planList?.size!! > 0  && notCompletePlanList != null){
                percentage = ((planList!!.size.minus(notCompletePlanList!!.size))/ planList!!.size)*100
            }
        }
        progressBar.progress = percentage
        progressPercentage.text = percentage.toString()
        Log.d(TAG, "Progress is $percentage")
        // Return the inflated view
        return view
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO
}
