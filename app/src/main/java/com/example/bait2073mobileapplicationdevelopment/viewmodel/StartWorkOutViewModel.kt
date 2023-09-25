package com.example.bait2073mobileapplicationdevelopment.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.bait2073mobileapplicationdevelopment.database.HealthyLifeDatabase
import com.example.bait2073mobileapplicationdevelopment.entities.PersonalizedWorkout
import com.example.bait2073mobileapplicationdevelopment.entities.StartWorkout
import com.example.bait2073mobileapplicationdevelopment.repository.PersonalizedWorkoutRepository
import com.example.bait2073mobileapplicationdevelopment.repository.StartWorkoutRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StartWorkOutViewModel (application: Application) : AndroidViewModel(application) {
    private val repository: StartWorkoutRepository

    val allStartWorkout: LiveData<List<StartWorkout>>


    init {
        val dao = HealthyLifeDatabase.getDatabase(application).startWorkoutDao()
        repository = StartWorkoutRepository(dao)
        allStartWorkout = repository.allWorkout

    }

    // Define the function to retrieve a specific user


    // Define the function to insert a user
    fun insertWorkout(startWorkout: StartWorkout) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(startWorkout)
    }


    fun clearWorkout() = viewModelScope.launch(Dispatchers.IO) {
        repository.clearWorkout()
    }

}
//    suspend fun getWorkoutByid(workoutId: Int): LiveData<PersonalizedWorkout> {
//        return repository.getWorkoutById(workoutId)
//    }
