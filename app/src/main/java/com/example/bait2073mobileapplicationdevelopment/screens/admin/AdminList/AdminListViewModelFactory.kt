package com.example.bait2073mobileapplicationdevelopment.screens.admin.AdminList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bait2073mobileapplicationdevelopment.screens.admin.UserList.AdminListViewModel

class AdminListViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminListViewModel::class.java)) {
            // Create and return an instance of your AdminListViewModel
            return AdminListViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}