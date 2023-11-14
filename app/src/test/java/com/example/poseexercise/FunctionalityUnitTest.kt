package com.example.poseexercise

import com.example.poseexercise.views.fragment.WorkoutFragment
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class FunctionalityUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testWorkoutResult_lowercaseText_distanceby_underscore() {
        val completedFragment = WorkoutFragment()

        val inputText = "neutral_standing"
        val expectedOutput = "Neutral standing"

        val actualOutput = completedFragment.transformText(inputText)

        assertEquals(expectedOutput, actualOutput)
    }

    @Test
    fun testWorkoutResult_capitalizeText_distanceby_underscore() {
        val completedFragment = WorkoutFragment()

        val inputText = "Pushups_down"
        val expectedOutput = "Pushups down"

        val actualOutput = completedFragment.transformText(inputText)

        assertEquals(expectedOutput, actualOutput)
    }

    @Test
    fun testWorkoutResult_singleLowerCaseText() {
        val completedFragment = WorkoutFragment()
        val inputText = "lunges"
        val expectedOutput = "Lunges"
        val actualOutput = completedFragment.transformText(inputText)

        assertEquals(expectedOutput, actualOutput)
    }
}