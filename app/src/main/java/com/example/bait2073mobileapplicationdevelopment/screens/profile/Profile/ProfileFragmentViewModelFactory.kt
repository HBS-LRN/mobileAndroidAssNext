package com.example.bait2073mobileapplicationdevelopment.screens.profile.Profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bait2073mobileapplicationdevelopment.screens.profile.Profile.ProfileFragmentViewModel

class ProfileFragmentViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileFragmentViewModel::class.java)) {
            // Replace with the actual constructor parameters for ProfileFragmentViewModel
            return ProfileFragmentViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}