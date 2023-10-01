package com.example.bait2073mobileapplicationdevelopment.screens.workout

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MyTrainViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyTrainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyTrainViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}