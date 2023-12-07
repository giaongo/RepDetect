package com.example.poseexercise.data.plan

import androidx.annotation.DrawableRes

/**
 * Data class representing an exercise
 */
data class Exercise(
    val id: Long,
    val name: String,
    @DrawableRes
    val image: Int?,
    val calorie: Double,
    val level: String
)

/**
 * Data class representing exercise data for a workout
 */
data class ExerciseData(
    val planId: Int? = null,
    val exerciseName: String,
    val repetitions: Int,
    val confidence: Float,
    val isComplete: Boolean
)

/**
 * Class representing the log of exercises performed in a workout
 */
class ExerciseLog {
    // Mutable map to store exercise data with exercise name as the key
    private val exerciseMap = mutableMapOf<String, ExerciseData>()

    // Add exercise data to the log
    fun addExercise(
        planId: Int?,
        exerciseName: String,
        repetitions: Int,
        confidence: Float,
        isComplete: Boolean
    ) {
        val exerciseData = ExerciseData(planId, exerciseName, repetitions, confidence, isComplete)
        exerciseMap[exerciseName] = exerciseData
    }

    // Get exercise data for a specific exercise name
    fun getExerciseData(exerciseName: String): ExerciseData? {
        return exerciseMap[exerciseName]
    }

    // Get a list of all exercise data in the log
    fun getExerciseDataList(): List<ExerciseData> {
        return exerciseMap.values.toList()
    }

    // Check if all exercises in the provided exercise plan are completed
    fun areAllExercisesCompleted(databaseExercisePlan: List<ExercisePlan>): Boolean {
        return databaseExercisePlan.all { exercisePlan ->
            val exerciseData = exerciseMap[exercisePlan.exerciseName]
            exerciseData?.isComplete ?: false
        }
    }
}

/**
 * Data class representing an exercise plan
 */
data class ExercisePlan(val planId: Int?, val exerciseName: String, var repetitions: Int)
