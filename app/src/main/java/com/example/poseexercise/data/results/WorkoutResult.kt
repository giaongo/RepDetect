package com.example.poseexercise.data.results

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_results")
data class WorkoutResult(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val exerciseName: String,
    val repeatedCount: Int,
    var confidence: Float
)