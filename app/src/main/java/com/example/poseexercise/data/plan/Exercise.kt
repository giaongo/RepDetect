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