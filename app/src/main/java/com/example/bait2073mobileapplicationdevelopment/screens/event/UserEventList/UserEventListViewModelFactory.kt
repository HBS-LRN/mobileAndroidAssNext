package com.example.bait2073mobileapplicationdevelopment.screens.event.UserEventList

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class UserEventListViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserEventListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserEventListViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}