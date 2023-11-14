package com.example.poseexercise.data.plan

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plan_items")
data class Plan(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val exercise: String,
    val calories: Int,
    val repeatCount: Int,
    val selectedDays: String,
    var completed: Boolean = false
)




