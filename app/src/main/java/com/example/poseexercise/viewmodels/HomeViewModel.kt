package com.example.poseexercise.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.poseexercise.data.database.AppRepository
import com.example.poseexercise.data.plan.Plan

/**
 * ViewModel for the Home screen.
 * @param application The application context.
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AppRepository = AppRepository(application)

    // LiveData to observe the list of plans
    val plans: LiveData<List<Plan>> = repository.allPlans

    /**
     * Get plans for a specific day.
     * @param day The day for which plans are requested.
     * @return List of plans for the specified day.
     */
    fun getPlanByDay(day: String): List<Plan>? {
        return repository.getPlanByDay(day)
    }

    /**
     * Get incomplete plans for a specific day.
     * @param day The day for which incomplete plans are requested.
     * @return List of incomplete plans for the specified day.
     */
    fun getNotCompletePlans(day: String): MutableList<Plan>? {
        return repository.getNotCompletePlanByDay(day)
    }
}