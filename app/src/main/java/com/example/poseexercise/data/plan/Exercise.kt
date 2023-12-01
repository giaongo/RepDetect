package com.example.poseexercise.data.plan

import androidx.annotation.DrawableRes

data class Exercise(
    val id: Long,
    val name: String,
    @DrawableRes
    val image: Int?,
    val calorie: Double,
    val level: String
)

data class ExerciseData(val planId: Int? = null, val exerciseName: String, val repetitions: Int, val confidence: Float, val isComplete: Boolean)

class ExerciseLog {
    private val exerciseMap = mutableMapOf<String, ExerciseData>()

    fun addExercise(planId: Int?, exerciseName: String, repetitions: Int, confidence: Float, isComplete: Boolean) {
        val exerciseData = ExerciseData(planId,exerciseName, repetitions, confidence, isComplete)
        exerciseMap[exerciseName] = exerciseData
    }

    fun getExerciseData(exerciseName: String): ExerciseData? {
        return exerciseMap[exerciseName]
    }

    fun getExerciseDataList(): List<ExerciseData> {
        return exerciseMap.values.toList()
    }

    fun areAllExercisesCompleted(databaseExercisePlan: List<ExercisePlan>): Boolean {
        return databaseExercisePlan.all { exercisePlan ->
            val exerciseData = exerciseMap[exercisePlan.exerciseName]
            exerciseData?.isComplete ?: false
        }
    }
}

data class ExercisePlan(val planId: Int?, val exerciseName: String, var repetitions: Int)
