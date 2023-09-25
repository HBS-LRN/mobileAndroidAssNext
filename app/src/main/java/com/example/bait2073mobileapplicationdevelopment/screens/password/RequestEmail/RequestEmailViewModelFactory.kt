package com.example.bait2073mobileapplicationdevelopment.screens.password.RequestEmail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RequestEmailViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RequestEmailViewModel::class.java)) {
            // Replace with the actual constructor parameters for RequestEmailViewModel
            return RequestEmailViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}