package com.example.poseexercise.views.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.poseexercise.R
import com.example.poseexercise.adapters.PlanAdapter
import com.example.poseexercise.data.plan.TodaysPlan
import com.example.poseexercise.util.MyApplication
import com.example.poseexercise.viewmodels.ResultViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var resultViewModel: ResultViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var planAdapter: PlanAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)

        // Dummy data for testing
        val dummyExercises = listOf(
            TodaysPlan(1, R.drawable.ic_launcher_foreground, "Push-ups", 15),
            TodaysPlan(2, R.drawable.ic_launcher_foreground, "Squats", 12),
            TodaysPlan(3, R.drawable.ic_launcher_foreground, "Lunges", 10)
        )

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view)
        planAdapter = PlanAdapter(dummyExercises, NavHostFragment.findNavController(this))
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = planAdapter

        resultViewModel = ResultViewModel(MyApplication.getInstance())
        lifecycleScope.launch {
            resultViewModel.getAllResult()?.forEach {
                Log.d("exe-result", "${it.id} ${it.exerciseName} ${it.repeatedCount} ${it.confidence}")
            }
        }
        return view
    }
}
