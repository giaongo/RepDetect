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