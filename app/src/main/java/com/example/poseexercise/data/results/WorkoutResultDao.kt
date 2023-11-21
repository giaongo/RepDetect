package com.example.poseexercise.data.results

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WorkoutResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: WorkoutResult)

    @Query("SELECT * FROM workout_results")
    suspend fun getAll(): List<WorkoutResult>
}