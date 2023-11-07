package com.example.poseexercise.data

import androidx.lifecycle.ViewModel


class AIClassificationViewModel : ViewModel() {
    var exerciseClass: String? = null
    var reps = 0
    var confidence = 0.0
}