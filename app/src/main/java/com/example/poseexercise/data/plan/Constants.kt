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
                name = "Lunge",
                image = R.drawable.reverse_lunges,
                calorie = 3.0,
                level = "Beginner"
            ),
            Exercise(
                id = 3,
                name = "Squat",
                image = R.drawable.squat,
                calorie = 3.8,
                level = "Beginner"
            ),
            Exercise(
                id = 4,
                name = "Sit up",
                image = R.drawable.sit_ups,
                calorie = 5.0,
                level = "Advance"
            ),
        )
    }
}