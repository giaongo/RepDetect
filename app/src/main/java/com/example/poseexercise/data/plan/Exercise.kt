package com.example.poseexercise.data.plan

import androidx.annotation.DrawableRes

data class Exercise (
    val id: Long,
    val name: String,
    @DrawableRes
    val image: Int?,
    val calorie: Int,
    val level: String
)

data class ExerciseData(val repetitions: Int, val confidence: Float, val isComplete: Boolean)

class ExerciseLog {
    private val exerciseMap = mutableMapOf<String, ExerciseData>()

    fun addExercise(exerciseName: String, repetitions: Int, confidence: Float, isComplete: Boolean) {
        val exerciseData = ExerciseData(repetitions, confidence, isComplete)
        exerciseMap[exerciseName] = exerciseData
    }

    fun getExerciseData(exerciseName: String): ExerciseData? {
        return exerciseMap[exerciseName]
    }
}

data class ExercisePlan(val exerciseName: String, val repetitions: Int)
