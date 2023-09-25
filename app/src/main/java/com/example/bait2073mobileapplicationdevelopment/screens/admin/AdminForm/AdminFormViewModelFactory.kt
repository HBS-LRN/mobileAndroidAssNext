package com.example.bait2073mobileapplicationdevelopment.screens.admin.AdminForm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AdminFormViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminFormViewModel::class.java)) {
            // Create and return an instance of your AdminFormViewModel
            return AdminFormViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}