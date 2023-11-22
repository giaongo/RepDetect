package com.example.poseexercise.data.plan

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.poseexercise.data.results.DateConverters

@Entity(tableName = "plan_items")
data class Plan(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val exercise: String,
    val calories: Int,
    val repeatCount: Int,
    val selectedDays: String,
    var completed: Boolean = false,
    @TypeConverters(DateConverters::class)
    val timeCompleted: Long? = null
)




