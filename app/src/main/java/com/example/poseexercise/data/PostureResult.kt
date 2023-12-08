package com.example.poseexercise.data

/**
 * Data class representing the result of a posture detection
 */
data class PostureResult(
    var repetition: Int = 0,
    var confidence: Float = 0f,
    val postureType: String = "",
)


