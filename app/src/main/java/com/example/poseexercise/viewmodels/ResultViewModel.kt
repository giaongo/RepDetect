package com.example.poseexercise.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.poseexercise.data.database.AppRepository
import com.example.poseexercise.data.results.WorkoutResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ResultViewModel(application: Application): AndroidViewModel(application) {
    private val repository: AppRepository = AppRepository(application)
    suspend fun insert(result: WorkoutResult) = withContext(Dispatchers.IO){
        repository.insertResult(result)
    }

    suspend fun getAllResult() : List<WorkoutResult>? =
        repository.getAllResult()
}