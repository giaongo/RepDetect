package com.example.poseexercise.data.results

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.Date

@Entity(tableName = "workout_results")
data class WorkoutResult(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val exerciseName: String,
    val repeatedCount: Int,
    var confidence: Float,
    @TypeConverters(DateConverters::class)
    val timestamp: Long
    )


// To convert Long value to Date and vice versa
class DateConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let {
            Date(it)
        }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}