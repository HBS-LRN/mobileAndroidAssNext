package com.example.bait2073mobileapplicationdevelopment.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.bait2073mobileapplicationdevelopment.database.HealthyLifeDatabase
import com.example.bait2073mobileapplicationdevelopment.entities.Symptom
import com.example.bait2073mobileapplicationdevelopment.repository.SymptomRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SymptomViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: SymptomRepository

    val allSymptoms: LiveData<List<Symptom>>

    init {
        val dao = HealthyLifeDatabase.getDatabase(application).symptomDao()
        repository = SymptomRepository(dao)
        allSymptoms = repository.allSymptoms

    }

    // Define the function to insert a symptom
    fun insertSymptom(symptom: Symptom) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(symptom)
    }

    // Define the function to retrieve a specific symptom
    suspend fun getSymptomById(symptomId: Int): LiveData<Symptom> {
        return repository.getSymptomById(symptomId)
    }


}