package com.example.bait2073mobileapplicationdevelopment.screens.dashboard.AdminDashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bait2073mobileapplicationdevelopment.screens.dashboard.AdminDashboard.AdminDashboardViewModel

class AdminDashboardViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminDashboardViewModel::class.java)) {
            // Replace with the actual constructor parameters for AdminDashboardViewModel
            return AdminDashboardViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}