package com.example.bait2073mobileapplicationdevelopment.screens.workout
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bait2073mobileapplicationdevelopment.screens.workout.userrPlanWorkoutViewModel
class userPlanWorkoutViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(userrPlanWorkoutViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return userrPlanWorkoutViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
