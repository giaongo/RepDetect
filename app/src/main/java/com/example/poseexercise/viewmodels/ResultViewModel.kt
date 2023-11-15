package com.example.poseexercise.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.poseexercise.data.database.AppRepository
import com.example.poseexercise.data.results.WorkoutResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ResultViewModel(application: Application): AndroidViewModel(application) {
    // Create an instance of the AppRepository, which handles data operations
    private val repository: AppRepository = AppRepository(application)

    // Coroutine function to insert a workout result into the repository
    suspend fun insert(result: WorkoutResult) = withContext(Dispatchers.IO){
        repository.insertResult(result)
    }

    // Coroutine function to retrieve all workout results from the repository
    suspend fun getAllResult() : List<WorkoutResult>? =
        repository.getAllResult()
}