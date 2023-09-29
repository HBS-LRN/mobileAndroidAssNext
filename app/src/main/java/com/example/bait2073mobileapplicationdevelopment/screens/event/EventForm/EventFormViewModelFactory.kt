package com.example.bait2073mobileapplicationdevelopment.screens.admin.EventForm

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bait2073mobileapplicationdevelopment.screens.event.EventForm.EventFormViewModel

class EventFormViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventFormViewModel::class.java)) {
            return EventFormViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}