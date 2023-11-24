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

class AppRepository(application: Application): CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private var planDao: PlanDataDao?
    private var resultDao: WorkoutResultDao?

    init {
        val database = AppDatabase.getDatabase(application)
        planDao = database.planDao()
        resultDao = database.resultDao()
    }
    val allPlans: LiveData<List<Plan>> = planDao?.getAll()!!
    suspend fun insertPlan(plan: Plan){
        planDao?.insert(plan)
    }
    suspend fun updatePlan(plan: Plan){
        planDao?.update(plan)
    }
    suspend fun updateCompleted(state: Boolean, timestamp: Long?, planId: Int){
        planDao?.addCompletedTime(state,timestamp,planId)
    }
    suspend fun deletePlan(planId: Int){
        planDao?.deletePlan(planId)
    }
    suspend fun getAllResult(): List<WorkoutResult>? =
        resultDao?.getAll()
    fun getPlanByDay(day: String): List<Plan>? {
        return planDao?.getPlansByDay(day)
    }
    fun getNotCompletePlanByDay(day: String): MutableList<Plan>? {
        return planDao?.getNotCompletePlanByDay(day)
    }
    suspend fun insertResult(result: WorkoutResult){
        resultDao?.insert(result)
    }
}