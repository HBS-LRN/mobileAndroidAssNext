package com.example.bait2073mobileapplicationdevelopment.dao

import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bait2073mobileapplicationdevelopment.entities.Disease_Symptom
import com.example.bait2073mobileapplicationdevelopment.entities.Event

interface EventParticipantsDao {
    @Query("SELECT * FROM event ORDER BY id DESC")
    fun getAllEventsParticipants() : LiveData<List<Event>>

//    @Query("SELECT  from event WHERE id = :diseaseSymptomId")
//    fun getEById(userId: Int): LiveData<Event>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEventsParticipants(event: Event)

    @Query("DELETE FROM event")
    fun clearAllEventsParticipants()

}


