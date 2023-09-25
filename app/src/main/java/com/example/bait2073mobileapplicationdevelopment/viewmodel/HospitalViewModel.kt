package com.example.bait2073mobileapplicationdevelopment.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.bait2073mobileapplicationdevelopment.database.HealthyLifeDatabase
import com.example.bait2073mobileapplicationdevelopment.entities.Hospital
import com.example.bait2073mobileapplicationdevelopment.repository.HospitalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HospitalViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: HospitalRepository

    val allHospitals: LiveData<List<Hospital>>

    init {
        val dao = HealthyLifeDatabase.getDatabase(application).hospitalDao()
        repository = HospitalRepository(dao)
        allHospitals = repository.allHospitals

    }

    // Define the function to insert hospital
    fun insertHospital(hospital: Hospital) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(hospital)
    }

    // Define the function to retrieve a specific hospital
    suspend fun getHospitalById(hospitalId: Int): LiveData<Hospital> {
        return repository.getHospitalById(hospitalId)
    }


}