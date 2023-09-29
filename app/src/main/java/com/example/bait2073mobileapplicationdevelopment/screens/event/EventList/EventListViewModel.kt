package com.example.bait2073mobileapplicationdevelopment.screens.event.EventList

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.example.bait2073mobileapplicationdevelopment.database.HealthyLifeDatabase
import com.example.bait2073mobileapplicationdevelopment.BaseActivity
import com.example.bait2073mobileapplicationdevelopment.entities.Event
import com.example.bait2073mobileapplicationdevelopment.entities.EventParticipants
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetEventDataService
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetEventParticipantsDataService
import com.example.bait2073mobileapplicationdevelopment.repository.EventRepository
import com.example.bait2073mobileapplicationdevelopment.retrofitclient.RetrofitClientInstance
import com.example.bait2073mobileapplicationdevelopment.screens.event.EventForm.ManageEventFragmentDirections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventListViewModel (application: Application): AndroidViewModel(application) {

    private val repository : EventRepository
    val allEvent : LiveData<List<Event>>
    val recyclerListDataDao: LiveData<List<Event>>

    var recyclerListData: MutableLiveData<List<Event?>> = MutableLiveData()
    var deleteEventLiveData: MutableLiveData<Event?> = MutableLiveData()

    init{
        val dao = HealthyLifeDatabase.getDatabase(application).eventDao()
        repository = EventRepository(dao)
        allEvent = repository.allEvents
         recyclerListDataDao = repository.retrieve()
    }





    fun getDeleteEventObservable():MutableLiveData<Event?>{
        return deleteEventLiveData
    }

    fun getEventListObserverable(): MutableLiveData<List<Event?>>{
        return recyclerListData
    }


    fun insertEvent(event : Event)= viewModelScope.launch(Dispatchers.IO){
        repository.insert(event)
    }

    fun insertEventDataIntoRoomDb(events: List<Event>) {


        viewModelScope.launch {
            this.let {

                try {
                    for (event in events) {
                        Log.d("InsertEventDataIntoRoomDb", "Inserting event with ID: ${event.id}")

                        insertEvent(
                            Event(
                                id = event.id,
                                title = event.title ?: "",
                                details = event.details ?: "",
                                image = event.image ?: "",
                                date = event.date ?: "",
                                address = event.address ?: "",
                                status = "Active",
                                user_id = event.user_id ?: 0
                            )
                        )
                    }
                }catch (e: Exception) {
                    Log.e("InsertDataIntoRoomDb", "Error inserting data into Room Database: ${e.message}", e)
                }
            }
        }

    }



    fun getEvents() {
        val service = RetrofitClientInstance.retrofitInstance!!.create(GetEventDataService::class.java)
        val call = service.getEventList()
        call.enqueue(object : Callback<List<Event>> {
            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                Log.e("Event API Error", "API call failed: ${t.message}")
                getLocalDao()
            }

            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                if (response.isSuccessful) {
                    val eventList = response.body()
                    Log.e("Event onResponse", "Response successful, code: ${eventList}")

                    if (eventList != null && eventList.isNotEmpty()) {
                        recyclerListData.postValue(response.body())
                        insertEventDataIntoRoomDb(eventList)

                        Log.e("Event API Error", "LOCAL WORK Data retrieved from API")
                    } else {
                        Log.e("Error Event API onResponse", "API response body is empty")
                    }
                } else {
                    Log.e("Error Event API onResponse", "API response not successful, code: ${response.code()}")
                }
            }
        })
    }




    fun getEventsHaventPart(user_id : Int?) {
        val service = RetrofitClientInstance.retrofitInstance!!.create(GetEventParticipantsDataService::class.java)
        val call = service.getEventUserHaventParticipantsList(user_id)
        call.enqueue(object : Callback<List<Event>> {
            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                Log.e("Event API Error", "API call failed: ${t.message}")

            }

            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                if (response.isSuccessful) {
                    val eventList = response.body()
                    Log.e("Event onResponseHere", "Response successful, code: ${user_id} <- -> ${eventList}")

                    if (eventList != null && eventList.isNotEmpty()) {
                        recyclerListData.postValue(response.body())
                        insertEventDataIntoRoomDb(eventList)

                        Log.e("Event API Error", "LOCAL WORK Data retrieved from API")
                    } else {
                        Log.e("Error Event API onResponse", "API response body is empty")
                    }
                } else {
                    Log.e("Error Event API onResponse", "API response not successful, code: ${response.code()}")
                }
            }
        })
    }


    fun getLocalDao(){
        recyclerListDataDao.observeForever { newData ->
            val sortedData = newData.sortedBy { it.id }
            recyclerListData.postValue(sortedData)
            Log.e("Error Event API onResponse", "Local Database ${newData}")
        }
    }





    fun deleteEvent(event: Event){
        val service = RetrofitClientInstance.retrofitInstance!!.create(GetEventDataService::class.java)
        val call = service.deleteEvent(event.id?:0)
        call.enqueue(object : Callback<Event?> {

            override fun onFailure(call: Call<Event?>, t: Throwable) {
                Log.e("deleteEvent function", "onFailure function")
                deleteEventLiveData.postValue(null)
            }

            override fun onResponse(call: Call<Event?>, response: Response<Event?>) {
                if(response.isSuccessful) {

                    deleteEventLiveData.postValue(response.body())
                    removeEventFromLocalDatabase()
                } else {
                    Log.e("deleteEvent function", "Delete not successful")
                    deleteEventLiveData.postValue(response.body())

                }
            }
        })
    }




    private fun removeEventFromLocalDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }


}


