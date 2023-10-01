package com.example.bait2073mobileapplicationdevelopment.repository

import androidx.lifecycle.LiveData
import com.example.bait2073mobileapplicationdevelopment.dao.DiseaseSymptomRoomDao
import com.example.bait2073mobileapplicationdevelopment.entities.DiseaseSymptom_Room

class DiseaseSymptomRoomRepository (private val diseaseSymptomRoomDao: DiseaseSymptomRoomDao) {

    var allDiseaseSymptomsRoom: LiveData<List<DiseaseSymptom_Room>> = diseaseSymptomRoomDao.getAllDiseasesSymptomRoom()

    fun retrieve(): LiveData<List<DiseaseSymptom_Room>> {
        return diseaseSymptomRoomDao.getAllDiseasesSymptomRoom()
    }

    suspend fun insert(diseaseSymptomRoom: DiseaseSymptom_Room) {
        diseaseSymptomRoomDao.insertDiseaseSymptomRoom(diseaseSymptomRoom)
    }
    suspend fun getDiseaseSymptomRoomById(diseaseSymptomId: Int): LiveData<List<DiseaseSymptom_Room>> {
        return diseaseSymptomRoomDao.getDiseaseSymptomRoomById(diseaseSymptomId)
    }
    suspend fun deleteAll(){
        diseaseSymptomRoomDao.deleteDiseaseSymptomRoom()
    }
}