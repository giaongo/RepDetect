package com.example.poseexercise.data.plan

import com.example.poseexercise.R

object Constants {
    fun getExerciseList(): List<Exercise> {
        return listOf(
            Exercise(
                id = 1,
                name = "Push up",
                image = R.drawable.push_up,
                calorie = 3.2,
                level = "Intermediate"
            ),
            Exercise(
                id = 2,
                name = "Lunges",
                image = R.drawable.reverse_lunges,
                calorie = 3.0,
                level = "Beginner"
            ),
            Exercise(
                id = 3,
                name = "Squats",
                image = R.drawable.squats,
                calorie = 3.8,
                level = "Beginner"
            ),
            Exercise(
                id = 4,
                name = "Sit Up",
                image = R.drawable.sit_ups,
                calorie = 5.0,
                level = "Advance"
            ),
        )
    }
}