package com.example.bait2073mobileapplicationdevelopment.screens.password.ForgetPassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ForgetPasswordViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForgetPasswordViewModel::class.java)) {
            // Replace with the actual constructor parameters for ForgetPasswordViewModel
            return ForgetPasswordViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}