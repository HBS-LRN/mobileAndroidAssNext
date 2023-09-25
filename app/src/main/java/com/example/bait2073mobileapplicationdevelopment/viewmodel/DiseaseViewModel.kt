package com.example.bait2073mobileapplicationdevelopment.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.bait2073mobileapplicationdevelopment.entities.Disease
import com.example.bait2073mobileapplicationdevelopment.entities.Disease_Hospital
import com.example.bait2073mobileapplicationdevelopment.entities.Disease_Recipe
import com.example.bait2073mobileapplicationdevelopment.entities.Disease_Symptom
import com.example.bait2073mobileapplicationdevelopment.database.HealthyLifeDatabase
import com.example.bait2073mobileapplicationdevelopment.repository.DiseaseHospitalRepository
import com.example.bait2073mobileapplicationdevelopment.repository.DiseaseRecipeRepository
import com.example.bait2073mobileapplicationdevelopment.repository.DiseaseRepository
import com.example.bait2073mobileapplicationdevelopment.repository.DiseaseSymptomRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DiseaseViewModel (application: Application) : AndroidViewModel(application) {
    private val repository: DiseaseRepository
    private val diseaseSymptomRepository: DiseaseSymptomRepository
    private val diseaseHospitalRepository: DiseaseHospitalRepository
    private val diseaseRecipeRepository: DiseaseRecipeRepository

    val allDiseases: LiveData<List<Disease>>
    val allDiseaseSymptoms: LiveData<List<Disease_Symptom>>
    val allDiseaseHospitals: LiveData<List<Disease_Hospital>>
    val allDiseaseRecipes: LiveData<List<Disease_Recipe>>

    init {
        val dao = HealthyLifeDatabase.getDatabase(application).diseaseDao()
        val dsdao = HealthyLifeDatabase.getDatabase(application).diseaseSymptomDao()
        val dhdao = HealthyLifeDatabase.getDatabase(application).diseaseHospitalDao()
        val drdao = HealthyLifeDatabase.getDatabase(application).diseaseRecipeDao()
        repository = DiseaseRepository(dao)
        diseaseSymptomRepository = DiseaseSymptomRepository(dsdao)
        diseaseHospitalRepository = DiseaseHospitalRepository(dhdao)
        diseaseRecipeRepository = DiseaseRecipeRepository(drdao)

        allDiseases = repository.allDiseases
        allDiseaseSymptoms = diseaseSymptomRepository.allDiseaseSymptoms
        allDiseaseHospitals = diseaseHospitalRepository.allDiseaseHospitals
        allDiseaseRecipes = diseaseRecipeRepository.allDiseaseRecipes

    }

    fun insertDisease(disease: Disease) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(disease)
    }

    suspend fun getDiseaseById(diseaseId: Int): LiveData<Disease> {
        return repository.getDiseaseById(diseaseId)
    }

    fun insertDiseaseSymptom(diseaseSymptom: Disease_Symptom) = viewModelScope.launch(Dispatchers.IO) {
        diseaseSymptomRepository.insert(diseaseSymptom)
    }

    suspend fun getDiseaseSymptomById(diseaseSymptomId: Int): LiveData<Disease_Symptom> {
        return diseaseSymptomRepository.getDiseaseSymptomById(diseaseSymptomId)
    }

    fun insertDiseaseHospital(diseaseHospital: Disease_Hospital) = viewModelScope.launch(Dispatchers.IO) {
        diseaseHospitalRepository.insert(diseaseHospital)
    }

    suspend fun getDiseaseHospitalById(diseaseHospitalId: Int): LiveData<Disease_Hospital> {
        return diseaseHospitalRepository.getDiseaseHospitalById(diseaseHospitalId)
    }

    fun insertDiseaseRecipe(diseaseRecipe: Disease_Recipe) = viewModelScope.launch(Dispatchers.IO) {
        diseaseRecipeRepository.insert(diseaseRecipe)
    }

    suspend fun getDiseaseRecipeById(diseaseRecipeId: Int): LiveData<Disease_Recipe> {
        return diseaseRecipeRepository.getDiseaseRecipeById(diseaseRecipeId)
    }
}

