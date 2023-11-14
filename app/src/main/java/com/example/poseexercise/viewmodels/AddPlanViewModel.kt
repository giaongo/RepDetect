package com.example.poseexercise.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.logging.Filter

class AddPlanViewModel: ViewModel() {
    val filters = MutableLiveData<Set<Filter>>()


}