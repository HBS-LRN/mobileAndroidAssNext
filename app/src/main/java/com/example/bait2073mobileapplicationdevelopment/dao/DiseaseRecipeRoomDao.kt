package com.example.bait2073mobileapplicationdevelopment.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.bait2073mobileapplicationdevelopment.entities.DiseaseRecipe_Room

@Dao
interface DiseaseRecipeRoomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDiseaseRecipeRoom(diseaserecipeRoom: DiseaseRecipe_Room)

    @Query("SELECT * from diseaserecipe_room WHERE id = :diseaseId")
    fun getDiseaseRecipeRoomById(diseaseId: Int): LiveData<List<DiseaseRecipe_Room>>

    @Query("SELECT * FROM diseaserecipe_room")
    fun getAllDiseasesRecipeRoom(): LiveData<List<DiseaseRecipe_Room>>

    @Query("DELETE FROM diseaserecipe_room")
    fun deleteDiseaseRecipeRoom()

    @Query("DELETE FROM diseaserecipe_room WHERE id =:id")
    fun deleteOneDiseaseRecipeRoom(id: Int)

}