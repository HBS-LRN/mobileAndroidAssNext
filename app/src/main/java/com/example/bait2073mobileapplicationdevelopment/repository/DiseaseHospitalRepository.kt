package com.example.bait2073mobileapplicationdevelopment.repository

import androidx.lifecycle.LiveData
import com.example.bait2073mobileapplicationdevelopment.dao.DiseaseHospitalDao
import com.example.bait2073mobileapplicationdevelopment.entities.Disease_Hospital


class DiseaseHospitalRepository (private val diseaseHospitalDao: DiseaseHospitalDao) {

    var allDiseaseHospitals: LiveData<List<Disease_Hospital>> = diseaseHospitalDao.getAllDiseasesHospital()

    fun retrieve(): LiveData<List<Disease_Hospital>> {
        return diseaseHospitalDao.getAllDiseasesHospital()
    }

    suspend fun insert(diseaseHospital: Disease_Hospital) {
        diseaseHospitalDao.insertDiseaseHospital(diseaseHospital)
    }
    suspend fun getDiseaseHospitalById(diseaseHospitalId: Int): LiveData<Disease_Hospital> {
        return diseaseHospitalDao.getDiseaseHospitalById(diseaseHospitalId)
    }
    suspend fun deleteAll(){
        diseaseHospitalDao.deleteDiseaseHospital()
    }
}