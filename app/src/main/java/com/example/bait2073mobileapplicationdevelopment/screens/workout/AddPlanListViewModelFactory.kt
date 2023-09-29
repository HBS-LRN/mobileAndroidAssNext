package com.example.bait2073mobileapplicationdevelopment.screens.workout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bait2073mobileapplicationdevelopment.screens.workout.AddPlanListViewModel

class AddPlanListViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddPlanListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddPlanListViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}