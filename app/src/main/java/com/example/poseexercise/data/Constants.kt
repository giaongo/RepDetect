package com.example.poseexercise.data

import com.example.poseexercise.R

object Constants {
    fun getExerciseList(): List<Exercise> {
        return listOf(
            Exercise(
                id = 1,
                name = "Push up",
                image = R.drawable.push_up,
                calorie = 300,
                level = "Intermediate"
            )
        )
    }
}