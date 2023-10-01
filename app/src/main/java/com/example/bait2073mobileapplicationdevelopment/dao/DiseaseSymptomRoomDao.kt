package com.example.bait2073mobileapplicationdevelopment.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bait2073mobileapplicationdevelopment.entities.DiseaseSymptom_Room

@Dao
interface DiseaseSymptomRoomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDiseaseSymptomRoom(diseasesymptomRoom: DiseaseSymptom_Room)

    @Query("SELECT * from diseasesymptom_room WHERE id = :diseaseId")
    fun getDiseaseSymptomRoomById(diseaseId: Int): LiveData<List<DiseaseSymptom_Room>>

    @Query("SELECT * FROM diseasesymptom_room")
    fun getAllDiseasesSymptomRoom(): LiveData<List<DiseaseSymptom_Room>>

    @Query("DELETE FROM diseasesymptom_room")
    fun deleteDiseaseSymptomRoom()

    @Query("DELETE FROM diseasesymptom_room WHERE id =:id")
    fun deleteOneDiseaseSymptomRoom(id: Int)


}