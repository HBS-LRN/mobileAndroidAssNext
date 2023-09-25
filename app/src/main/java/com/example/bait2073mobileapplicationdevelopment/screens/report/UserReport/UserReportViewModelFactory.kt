package com.example.bait2073mobileapplicationdevelopment.screens.report.UserReport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class UserReportViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserReportViewModel::class.java)) {
            // Replace with the actual constructor parameters for UserReportViewModel
            return UserReportViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}