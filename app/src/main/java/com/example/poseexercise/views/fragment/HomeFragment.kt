package com.example.poseexercise.views.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.poseexercise.R
import com.example.poseexercise.data.results.WorkoutResult
import com.example.poseexercise.util.MyApplication
import com.example.poseexercise.viewmodels.ResultViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var resultViewModel: ResultViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        resultViewModel = ResultViewModel(MyApplication.getInstance())
        lifecycleScope.launch {
            resultViewModel.getAllResult()?.forEach {
                Log.d("exe-result", "${it.id} ${it.exerciseName} ${it.repeatedCount} ${it.confidence}")
            }
        }
        return  view
    }
}
