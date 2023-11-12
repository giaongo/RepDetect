package com.example.poseexercise.data

import androidx.annotation.DrawableRes

data class Exercise (
    val id: Long,
    val name: String,
    @DrawableRes
    val image: Int?,
    val calorie: Int,
    val level: String
)

data class ExerciseData(val repetitions: Int, val confidence: Float)

class ExerciseLog {
    private val exerciseMap = mutableMapOf<String, ExerciseData>()

    fun addExercise(exerciseName: String, repetitions: Int, confidence: Float) {
        val exerciseData = ExerciseData(repetitions, confidence)
        exerciseMap[exerciseName] = exerciseData
    }

    fun getExerciseData(exerciseName: String): ExerciseData? {
        return exerciseMap[exerciseName]
    }
}