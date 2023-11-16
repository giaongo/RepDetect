package com.example.poseexercise.util

class MyUtils {

    companion object {
        fun exerciseNameToDisplay(variableName: String): String {
            return when (variableName) {
                "squats" -> "Squat"
                "pushups_down" -> "Pushup"
                "lunges" -> "Lunge"
                // Add more cases as needed
                else -> variableName // Default to the original name if not matched
            }
        }
    }
}