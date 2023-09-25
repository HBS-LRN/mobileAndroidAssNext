package com.example.bait2073mobileapplicationdevelopment.screens.admin.UserList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bait2073mobileapplicationdevelopment.screens.admin.UserList.UserListViewModel

class UserListViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserListViewModel::class.java)) {
            return UserListViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}