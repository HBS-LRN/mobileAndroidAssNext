package com.example.bait2073mobileapplicationdevelopment.screens.admin.WorkoutForm
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bait2073mobileapplicationdevelopment.screens.admin.WorkoutForm.WorkoutFormViewModel

class WorkoutFormViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkoutFormViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WorkoutFormViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
