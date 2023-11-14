package com.example.poseexercise.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.poseexercise.data.database.AppRepository
import com.example.poseexercise.data.plan.Plan


class HomeViewModel(application: Application): AndroidViewModel(application) {
    private val repository: AppRepository = AppRepository(application)
    val plans: LiveData<List<Plan>> = repository.allPlans
    fun getPlanByDay(day: String): List<Plan>? {
        return repository.getPlanByDay(day)
    }

    fun getNotCompletePlans(day: String): List<Plan>? {
        return repository.getNotCompletePlanByDay(day)
    }
}