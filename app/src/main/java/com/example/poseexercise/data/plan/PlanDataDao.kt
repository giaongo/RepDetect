package com.example.poseexercise.data.plan

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlanDataDao {
     /* Insert a plan in the database. If the plan already exists, ignore it. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(plan: Plan)
    /* Get all plans in the database. */
    @Query("SELECT * FROM plan_items")
    fun getAll(): LiveData<List<Plan>>
    /* Get all plans from day of the week */
    @Query("SELECT * FROM plan_items WHERE selectedDays LIKE '%' || :day || '%'")
    fun getPlansByDay(day: String?): List<Plan>

    /* Get not completed plans from day of the week */
    @Query("SELECT * FROM plan_items WHERE selectedDays LIKE '%' || :day || '%' AND completed = 0")
    fun getNotCompletePlanByDay(day: String?): List<Plan>

}

