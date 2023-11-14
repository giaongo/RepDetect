package com.example.poseexercise.data.database

import android.app.Application
import com.example.poseexercise.data.plan.PlanDataDao
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

}