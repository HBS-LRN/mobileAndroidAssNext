package com.example.bait2073mobileapplicationdevelopment.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bait2073mobileapplicationdevelopment.entities.Disease_Symptom
import com.example.bait2073mobileapplicationdevelopment.entities.Event
import com.example.bait2073mobileapplicationdevelopment.entities.EventParticipants

@Dao
interface EventParticipantsDao {

    @Query("SELECT * FROM EventParticipants ORDER BY id DESC")
    fun getAllEventsParticipants() : LiveData<List<EventParticipants>>

    @Query("SELECT * FROM EventParticipants WHERE id = :eventId ORDER BY id DESC")
    fun getEventParticipantsById(eventId: Int): Event

    @Query("SELECT COUNT(*) FROM EventParticipants WHERE id = :eventId")
    fun getEventParticipantsCountByEventId(eventId: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEventsParticipants(event: EventParticipants)

    @Query("DELETE FROM EventParticipants")
    fun clearAllEventsParticipants()





//    @Query("SELECT * FROM event ORDER BY id DESC")
//    fun getAllEventsParticipants() : LiveData<List<Event>>
//
////    @Query("SELECT * from event WHERE id = :diseaseSymptomId")
////    fun getEById(userId: Int): LiveData<Event>
//
//    @Query("SELECT e.* FROM event e INNER JOIN eventParticipants ep ON e.id = ep.event_id WHERE ep.user_id = :userId ORDER BY e.id DESC")
//    fun getAllEventsForUser(userId: Int): LiveData<List<Event>>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertEventsParticipants(event: EventParticipants)
//
//    @Query("DELETE FROM EventParticipants")
//    fun clearAllEventsParticipants()

}


