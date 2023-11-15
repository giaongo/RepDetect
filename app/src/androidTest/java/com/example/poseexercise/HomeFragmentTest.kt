package com.example.poseexercise

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.poseexercise.views.activity.MainActivity
import com.example.poseexercise.views.fragment.HomeFragment
import org.hamcrest.CoreMatchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeFragmentTest {
    @get:Rule
    val mActivityRule = ActivityScenarioRule(MainActivity::class.java)
    @Test
    fun homeFragmentTest() {
        val scenario = launchFragmentInContainer<HomeFragment>(initialState = Lifecycle.State.INITIALIZED)
        scenario.moveToState(Lifecycle.State.RESUMED)
        onView(withId(R.id.progress_layout)).check(matches(isDisplayed()))
        onView(withId(R.id.today_plans)).check(matches(not(isDisplayed())))
        onView(withId(R.id.recentActivityRecyclerView)).check(matches(not(isDisplayed())))
    }
}