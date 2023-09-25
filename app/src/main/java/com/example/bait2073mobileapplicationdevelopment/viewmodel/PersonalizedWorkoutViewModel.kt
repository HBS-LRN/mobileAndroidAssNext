package com.example.bait2073mobileapplicationdevelopment.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.bait2073mobileapplicationdevelopment.database.HealthyLifeDatabase
import com.example.bait2073mobileapplicationdevelopment.entities.PersonalizedWorkout
import com.example.bait2073mobileapplicationdevelopment.entities.User
import com.example.bait2073mobileapplicationdevelopment.repository.PersonalizedWorkoutRepository
import com.example.bait2073mobileapplicationdevelopment.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PersonalizedWorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PersonalizedWorkoutRepository

    val allPersonalizedWorkout: LiveData<List<PersonalizedWorkout>>

    init {
        val dao = HealthyLifeDatabase.getDatabase(application).personalizedWorkoutDao()
        repository = PersonalizedWorkoutRepository(dao)
        allPersonalizedWorkout = repository.allWorkout

    }

    // Define the function to retrieve a specific user

    // Define the function to insert a user
    fun insertWorkout(personalizedWorkout: PersonalizedWorkout) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(personalizedWorkout)
    }

    fun clearWorkout() = viewModelScope.launch(Dispatchers.IO) {
        repository.clearWorkout()
    }
//    suspend fun getWorkoutByid(workoutId: Int): LiveData<PersonalizedWorkout> {
//        return repository.getWorkoutById(workoutId)
//    }


}