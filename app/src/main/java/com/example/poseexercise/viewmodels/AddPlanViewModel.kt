package com.example.poseexercise.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.poseexercise.data.database.AppRepository
import com.example.poseexercise.data.plan.Plan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AddPlanViewModel(application: Application): AndroidViewModel(application) {
    private val repository: AppRepository = AppRepository(application)
    suspend fun insert(plan: Plan) = withContext(Dispatchers.IO){
        repository.insertPlan(plan)
    }
}