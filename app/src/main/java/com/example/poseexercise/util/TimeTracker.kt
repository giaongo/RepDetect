package com.example.poseexercise.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TimeTracker(
    private val applicationScope: CoroutineScope,
    private val defaultDispatcher: CoroutineDispatcher
) {

    private var timeElapsedInMillis = 0L
    private var isCounting = false
    private var callback: ((timeInMillis: Long) -> Unit)? = null
    private var job: Job? = null

    // initiates the timer.
    private fun start() {
        if (job != null)
            return
        System.currentTimeMillis()
        this.job = applicationScope.launch(defaultDispatcher) {
            while (isCounting && isActive) {
                callback?.invoke(timeElapsedInMillis)
                delay(1000)
                timeElapsedInMillis += 1000
            }
        }
    }

    fun startResumeTimer(callback: (timeInMillis: Long) -> Unit) {
        if (isCounting)
            return
        this.callback = callback
        isCounting = true
        start()
    }

    fun stopTimer() {
        pauseTimer()
        timeElapsedInMillis = 0
    }

    fun pauseTimer() {
        isCounting = false
        job?.cancel()
        job = null
        callback = null
    }

}