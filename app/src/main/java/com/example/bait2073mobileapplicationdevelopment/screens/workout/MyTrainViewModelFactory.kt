package com.example.bait2073mobileapplicationdevelopment.screens.workout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MyTrainViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyTrainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyTrainViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}