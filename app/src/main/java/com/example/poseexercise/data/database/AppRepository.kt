package com.example.poseexercise.data.database

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.poseexercise.data.plan.Plan
import com.example.poseexercise.data.plan.PlanDataDao
import com.example.poseexercise.data.results.WorkoutResult
import com.example.poseexercise.data.results.WorkoutResultDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

/**
Repository class for handling data operations
 **/
class AppRepository(application: Application) : CoroutineScope {
    // Coroutine context for background tasks
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    // Data Access Objects for plans and workout results
    private var planDao: PlanDataDao?
    private var resultDao: WorkoutResultDao?

    // Initialize DAOs using the application's database
    init {
        val database = AppDatabase.getDatabase(application)
        planDao = database.planDao()
        resultDao = database.resultDao()
    }

    // LiveData containing all plans
    val allPlans: LiveData<List<Plan>> = planDao?.getAll()!!

    // Insert a plan into the database
    suspend fun insertPlan(plan: Plan) {
        planDao?.insert(plan)
    }

    // Update a plan in the database
    suspend fun updatePlan(plan: Plan) {
        planDao?.update(plan)
    }

    // Update the completion state and timestamp of a plan
    suspend fun updateCompleted(state: Boolean, timestamp: Long?, planId: Int) {
        planDao?.addCompletedTime(state, timestamp, planId)
    }

    // Delete a plan from the database
    suspend fun deletePlan(planId: Int) {
        planDao?.deletePlan(planId)
    }

    // Get all workout results
    suspend fun getAllResult(): List<WorkoutResult>? =
        resultDao?.getAll()

    // Get plans for a specific day
    fun getPlanByDay(day: String): List<Plan>? {
        return planDao?.getPlansByDay(day)
    }

    // Get incomplete plans for a specific day
    fun getNotCompletePlanByDay(day: String): MutableList<Plan>? {
        return planDao?.getNotCompletePlanByDay(day)
    }

    // Insert a workout result into the database
    suspend fun insertResult(result: WorkoutResult) {
        resultDao?.insert(result)
    }

    // Get the most recent workout results
    suspend fun getRecentWorkout(): List<WorkoutResult>? = resultDao?.getRecentWorkout()

}