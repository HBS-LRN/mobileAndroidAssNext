package com.example.bait2073mobileapplicationdevelopment.repository

import androidx.lifecycle.LiveData
import com.example.bait2073mobileapplicationdevelopment.dao.DiseaseRecipeRoomDao
import com.example.bait2073mobileapplicationdevelopment.entities.DiseaseRecipe_Room

class DiseaseRecipeRoomRepository (private val diseaseRecipeRoomDao: DiseaseRecipeRoomDao) {

    var allDiseaseRecipesRoom: LiveData<List<DiseaseRecipe_Room>> = diseaseRecipeRoomDao.getAllDiseasesRecipeRoom()

    fun retrieve(): LiveData<List<DiseaseRecipe_Room>> {
        return diseaseRecipeRoomDao.getAllDiseasesRecipeRoom()
    }

    suspend fun insert(diseaseRecipeRoom: DiseaseRecipe_Room) {
        diseaseRecipeRoomDao.insertDiseaseRecipeRoom(diseaseRecipeRoom)
    }
    suspend fun getDiseaseRecipeRoomById(diseaseRecipeId: Int): LiveData<List<DiseaseRecipe_Room>> {
        return diseaseRecipeRoomDao.getDiseaseRecipeRoomById(diseaseRecipeId)
    }
    suspend fun deleteAll(){
        diseaseRecipeRoomDao.deleteDiseaseRecipeRoom()
    }
}