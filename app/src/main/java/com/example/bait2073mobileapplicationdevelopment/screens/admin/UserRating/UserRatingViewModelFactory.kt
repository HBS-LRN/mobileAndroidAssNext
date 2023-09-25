package com.example.bait2073mobileapplicationdevelopment.screens.admin.UserRating

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bait2073mobileapplicationdevelopment.screens.admin.UserList.UserRatingViewModel

class UserRatingViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserRatingViewModel::class.java)) {
            // Create and return an instance of your UserRatingViewModel
            return UserRatingViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}