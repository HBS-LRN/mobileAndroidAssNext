package com.example.bait2073mobileapplicationdevelopment.repository

import androidx.lifecycle.LiveData
import com.example.bait2073mobileapplicationdevelopment.dao.DiseaseHospitalRoomDao
import com.example.bait2073mobileapplicationdevelopment.entities.DiseaseHospital_Room

class DiseaseHospitalRoomRepository (private val diseaseHospitalRoomDao: DiseaseHospitalRoomDao) {

    var allDiseaseHospitalsRoom: LiveData<List<DiseaseHospital_Room>> = diseaseHospitalRoomDao.getAllDiseasesHospitalRoom()

    fun retrieve(): LiveData<List<DiseaseHospital_Room>> {
        return diseaseHospitalRoomDao.getAllDiseasesHospitalRoom()
    }

    suspend fun insert(diseaseHospitalRoom: DiseaseHospital_Room) {
        diseaseHospitalRoomDao.insertDiseaseHospitalRoom(diseaseHospitalRoom)
    }
    suspend fun getDiseaseHospitalRoomById(diseaseHospitalId: Int): LiveData<List<DiseaseHospital_Room>> {
        return diseaseHospitalRoomDao.getDiseaseHospitalRoomById(diseaseHospitalId)
    }
    suspend fun deleteAll(){
        diseaseHospitalRoomDao.deleteDiseaseHospitalRoom()
    }

    suspend fun deleteOne(id: Int){
        diseaseHospitalRoomDao.deleteOneDiseaseHospitalRoom(id)
    }
}