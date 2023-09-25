package com.example.bait2073mobileapplicationdevelopment.screens.profile.BMI

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RequestBmiActivityViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RequestBmiActivityViewModel::class.java)) {
            // Replace with the actual constructor parameters for RequestBmiActivityViewModel
            return RequestBmiActivityViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}