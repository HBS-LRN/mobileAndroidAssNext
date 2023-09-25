package com.example.bait2073mobileapplicationdevelopment.screens.profile.Gender

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RequestGenderViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RequestGenderViewModel::class.java)) {
            // Replace with the actual constructor parameters for RequestGenderViewModel
            return RequestGenderViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
