package com.example.bait2073mobileapplicationdevelopment.repository

import androidx.lifecycle.LiveData
import com.example.bait2073mobileapplicationdevelopment.dao.EventDao
import com.example.bait2073mobileapplicationdevelopment.entities.Event


class EventRepository (private val eventDao: EventDao){

    var allEvents : LiveData<List<Event>> = eventDao.getAllEvents()
    suspend fun insert(event: Event){
        eventDao.insertEvents(event)
    }

    suspend fun retrieveId(eventId: Int) :Event{
        return eventDao.getEventById(eventId)
    }

    fun retrieve(): LiveData<List<Event>> {
        return eventDao.getAllEvents()
    }

    suspend fun deleteAll(){
        eventDao.clearAllEvents()
    }



}
//class EventRepository @Inject constructor(
//    private val api: GetEventDataService,
//    private val db: EventDatabase
//) {
//    private val restaurantDao = db.eventDao()
//
//    fun getEvents() = networkBoundResource(
//        query = {
//            restaurantDao.getAllEvents()
//        },
//        fetch = {
//            delay(2000)
//            api.getEventList()
//        },
//        saveFetchResult = { eventCall ->
//            try {
//                val response = eventCall.execute()
//                if (response.isSuccessful) {
//                    val eventList = response.body()
//                    if (eventList != null) {
//                        db.withTransaction {
//                            restaurantDao.deleteAllEvents()
//                            restaurantDao.insertEvents(eventList)
//                        }
//                    } else {
//                        // Handle the case where the API response contains a null list.
//                    }
//                } else {
//                    // Handle the case where the API call was not successful.
//                }
//            } catch (e: Exception) {
//                // Handle exceptions that may occur during the API call.
//            }
//        }
//    )
//}