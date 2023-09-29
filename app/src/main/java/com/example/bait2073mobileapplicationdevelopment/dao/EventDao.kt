package com.example.bait2073mobileapplicationdevelopment.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bait2073mobileapplicationdevelopment.entities.Disease_Symptom
import com.example.bait2073mobileapplicationdevelopment.entities.Event
import com.example.bait2073mobileapplicationdevelopment.entities.User


@Dao
interface EventDao {
//    @Query("SELECT * FROM event ORDER BY id DESC")
//    fun getAllUsers() : LiveData<List<User>>
//
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertUser(event: Event)
//
//
//    @Query("DELETE FROM event")
//    fun clearDb()

//    @Query("SELECT * FROM event")
//    fun getAllEvents(): Flow<List<Event>>
    //Flow - used to fetch data from a database and provides a reactive
    //way to observe and respond to changes in the data as they occur.

    @Query("SELECT * FROM event ORDER BY id DESC")
    fun getAllEvents() : LiveData<List<Event>>

    @Query("SELECT * FROM event WHERE id = :eventId")
    fun getEventById(eventId: Int): Event

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertEvents(event: Event)

    @Query("DELETE FROM event")
     fun clearAllEvents()


}
