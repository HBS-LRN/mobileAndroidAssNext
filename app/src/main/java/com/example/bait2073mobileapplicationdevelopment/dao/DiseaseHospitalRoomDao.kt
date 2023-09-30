package com.example.bait2073mobileapplicationdevelopment.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bait2073mobileapplicationdevelopment.entities.DiseaseHospital_Room

@Dao
interface DiseaseHospitalRoomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDiseaseHospitalRoom(diseasehospitalRoom: DiseaseHospital_Room)

    @Query("SELECT * from diseasehospital_room WHERE id = :diseaseId")
    fun getDiseaseHospitalRoomById(diseaseId: Int): LiveData<List<DiseaseHospital_Room>>

    @Query("SELECT * FROM diseasehospital_room")
    fun getAllDiseasesHospitalRoom(): LiveData<List<DiseaseHospital_Room>>

    @Query("DELETE FROM diseasehospital_room")
    fun deleteDiseaseHospitalRoom()

    @Query("DELETE FROM diseasehospital_room WHERE id =:id")
    fun deleteOneDiseaseHospitalRoom(id: Int)

}