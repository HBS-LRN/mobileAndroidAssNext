package com.example.bait2073mobileapplicationdevelopment.screens.password.ChangePassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ChangePasswordViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChangePasswordViewModel::class.java)) {
            // Replace with the actual constructor parameters for ChangePasswordViewModel
            return ChangePasswordViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}