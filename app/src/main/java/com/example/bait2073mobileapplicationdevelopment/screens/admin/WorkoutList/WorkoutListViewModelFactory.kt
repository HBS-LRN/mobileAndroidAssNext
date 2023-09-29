package com.example.bait2073mobileapplicationdevelopment.screens.admin.WorkoutList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bait2073mobileapplicationdevelopment.screens.admin.WorkoutList.WorkoutListViewModel

class WorkoutListViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkoutListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WorkoutListViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}