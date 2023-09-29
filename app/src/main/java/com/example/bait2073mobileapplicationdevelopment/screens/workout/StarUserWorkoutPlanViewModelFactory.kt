package com.example.bait2073mobileapplicationdevelopment.screens.workout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class StartUserWorkoutPlanViewModelFactory(private val workoutList: StartPlan) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StartUserWorkoutPlanViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StartUserWorkoutPlanViewModel(workoutList) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
