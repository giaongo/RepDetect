package com.example.poseexercise

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.hamcrest.CoreMatchers
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.junit.Before

/**
 * Instrumented test, which will execute on an Android device.
 *
 */
@RunWith(AndroidJUnit4::class)
class AutomatedUITest {
    private lateinit var device:UiDevice
    @Before
    fun setUp() {
        // Initialize UiDevice instance
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.pressHome()
        // Wait for the launcher
        val launcherPackage: String = device.currentPackageName
        ViewMatchers.assertThat(launcherPackage, CoreMatchers.notNullValue())
        device.wait(
            Until.hasObject(By.pkg(launcherPackage).depth(0)),
            5000
        )
        // Launch the app
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent =
            context.packageManager.getLaunchIntentForPackage("com.example.poseexercise")?.apply {
                // Clear out any previous instances
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
        context.startActivity(intent)

        // wait until the app'sUI displays
        device.wait(
            Until.hasObject(By.pkg("com.example.poseexercise").depth(0)),
            5000
        )
    }

    @Test
    fun onBoardingTest() {
        // Wait until the onboarding fragment displays or time out after 10s
        device.wait(Until.hasObject(By.res("com.example.poseexercise:id/onboardingLayout")),10000)
        // Click on the next button
        device.findObject(By.res("com.example.poseexercise:id/nextButton")).click()
        // Wait until the second onboarding fragment displays or time out after 10s
        onView(withText("Elevate Your Workout Experience")).check(matches(isDisplayed()))
        // Click on the next button
        device.findObject(By.res("com.example.poseexercise:id/nextButton")).click()
        // Wait until the third onboarding fragment displays or time out after 10s
        onView(withText("Hassle-Free Repetition Tracking")).check(matches(isDisplayed()))
    }

}