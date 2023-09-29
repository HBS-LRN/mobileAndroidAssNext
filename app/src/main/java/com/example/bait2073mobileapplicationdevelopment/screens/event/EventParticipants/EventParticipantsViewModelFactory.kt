package com.example.bait2073mobileapplicationdevelopment.screens.event.EventParticipants

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bait2073mobileapplicationdevelopment.screens.event.EventList.EventListViewModel
import com.example.bait2073mobileapplicationdevelopment.screens.eventParticipants.EventParticipantsParticipants.EventParticipantsViewModel

class EventParticipantsViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventParticipantsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EventParticipantsViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}