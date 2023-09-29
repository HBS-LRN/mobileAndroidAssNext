package com.example.bait2073mobileapplicationdevelopment.screens.eventParticipants.EventParticipantsParticipants

import EventParticipantsRepository
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bait2073mobileapplicationdevelopment.database.HealthyLifeDatabase
import com.example.bait2073mobileapplicationdevelopment.entities.Event
import com.example.bait2073mobileapplicationdevelopment.entities.EventParticipants
import com.example.bait2073mobileapplicationdevelopment.entities.EventParticipantsTable
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetEventDataService
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetEventParticipantsDataService
import com.example.bait2073mobileapplicationdevelopment.repository.EventRepository
import com.example.bait2073mobileapplicationdevelopment.retrofitclient.RetrofitClientInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventParticipantsViewModel(application: Application) : ViewModel(){

    private val repository : EventParticipantsRepository
    val recyclerListDataDao: LiveData<List<EventParticipants>>
    val recyclerListEventHaventPartDataDao: LiveData<List<Event>>

    private val repositoryEvent : EventRepository

    lateinit var createNewEventParticipantsLiveData: MutableLiveData<EventParticipants?>
    lateinit var loadEventParticipantsData: MutableLiveData<EventParticipants?>

    var recyclerListData: MutableLiveData<List<EventParticipants?>?> = MutableLiveData()

    var recyclerListEventPartData: MutableLiveData<List<Event?>?> = MutableLiveData()

    var deleteEventLiveData: MutableLiveData<EventParticipants?> = MutableLiveData()

    val eventParticipantsCount = MutableLiveData<Int?>()

    init {
        val daoEventPart = HealthyLifeDatabase.getDatabase(application).eventPartDao()
        repository = EventParticipantsRepository(daoEventPart)
        val daoEvent = HealthyLifeDatabase.getDatabase(application).eventDao()
        repositoryEvent = EventRepository(daoEvent)
        createNewEventParticipantsLiveData = MutableLiveData()
        loadEventParticipantsData = MutableLiveData()
        recyclerListDataDao = repository.retrieve()
        recyclerListEventHaventPartDataDao = repositoryEvent.retrieve()
    }



    fun getCreateNewEventParticipantsObservable(): MutableLiveData<EventParticipants?> {
        return createNewEventParticipantsLiveData
    }


    fun getLoadEventParticipantsObservable(): MutableLiveData<EventParticipants?> {
        return loadEventParticipantsData
    }

    fun getEventListObserverable(): MutableLiveData<List<EventParticipants?>?> {
        return recyclerListData
    }
    fun getEventListEventPartObserverable(): MutableLiveData<List<Event?>?> {
        return recyclerListEventPartData
    }


    fun getEventPartSizeObserverable(): MutableLiveData<Int?> {
        return eventParticipantsCount
    }

    fun insertEvent(event : EventParticipants)= viewModelScope.launch(Dispatchers.IO){
        repository.insert(event)
    }

    fun insertEventDataIntoRoomDb(events: List<EventParticipants>) {
        viewModelScope.launch {
            this.let {

                try {
                    for (event in events) {
                        Log.d("InsertEventDataIntoRoomDb", "Inserting event with ID: ${event.id}")

                        insertEvent(
                            EventParticipants(
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

    fun insertEventHaventPart(event : Event)= viewModelScope.launch(Dispatchers.IO){
        repositoryEvent.insert(event)
    }

    fun insertEventHaventPartDataIntoRoomDb(events: List<Event>) {


        viewModelScope.launch {
            this.let {

                try {
                    for (event in events) {
                        Log.d("InsertEventDataIntoRoomDb", "Inserting event with ID: ${event.id}")

                        insertEventHaventPart(
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


    fun createEventParticipants(eventParticipants: EventParticipantsTable, callback: (Boolean) -> Unit) {
        val service = RetrofitClientInstance.retrofitInstance!!.create(GetEventParticipantsDataService::class.java)
        val call = service.addEventParticipants(eventParticipants)
        call.enqueue(object : Callback<EventParticipantsTable?> {
            override fun onFailure(call: Call<EventParticipantsTable?>, t: Throwable) {
                Log.e("createEventParticipants onFailure", "createEventParticipants onFailure ${t}")
                callback(false)
            }

            override fun onResponse(call: Call<EventParticipantsTable?>, response: Response<EventParticipantsTable?>) {
                if (response.isSuccessful) {
                    val resposne = response.body()
                    Log.i("createEventParticipants onResponse isSuccessful", "$resposne")
                    callback(true)
                } else {
                    val resposne = response.body()
                    val errorBody = response.errorBody()?.string()
                    val responseCode = response.code()
                    val responseMessage = response.message()
                    Log.e("createEventParticipants onResponse not successful", "Response is Notsuccessful. Code: $responseCode, Message: $responseMessage, Error Body: $errorBody")
                    callback(false)
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
                getLocalEventHavetPartDao()
            }

            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                if (response.isSuccessful) {
                    val eventList = response.body()
                    Log.e("Event onResponse", "Response successful, code: ${eventList}")

                    if (eventList != null && eventList.isNotEmpty()) {
                        recyclerListEventPartData.postValue(response.body())
                        insertEventHaventPartDataIntoRoomDb(eventList)

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

    fun getEventsPart(user_id : Int?) {
        val service = RetrofitClientInstance.retrofitInstance!!.create(GetEventParticipantsDataService::class.java)
        val call = service.getEventUserParticipantsList(user_id)
        call.enqueue(object : Callback<List<EventParticipants>> {
            override fun onFailure(call: Call<List<EventParticipants>>, t: Throwable) {
                Log.e("EventParticipants API Error", "API call failed: ${t.message}")
//                recyclerListData.postValue(null)
                getLocalDao()
            }

            override fun onResponse(
                call: Call<List<EventParticipants>>,
                response: Response<List<EventParticipants>>
            ) {

                if (response.isSuccessful) {
                    val resposne = response.body()

                    recyclerListData.postValue(response.body())
                    if (resposne != null) {
                        insertEventDataIntoRoomDb(resposne)
                    }
                } else {
                    Log.i("getEventData onResponse notSuccessful", "ggla")
                    recyclerListData.postValue(null)
                }
            }
        })

    }

    fun getLocalDao(){
        recyclerListDataDao.observeForever { newData ->
            val sortedData = newData.sortedBy { it.id }
            recyclerListData.postValue(sortedData)
            Log.e("Error EventParticipants API onResponse", "Local Database ${newData}")
        }
    }

    fun getLocalEventHavetPartDao(){
        recyclerListEventHaventPartDataDao.observeForever { newData ->
            val sortedData = newData.sortedBy { it.id }
            recyclerListEventPartData.postValue(sortedData)
            Log.e("Error EventParticipants API onResponse", "Local Database ${newData}")
        }
    }


    fun getEventsPartSize(event_id: Int) {
        val service = RetrofitClientInstance.retrofitInstance!!.create(GetEventParticipantsDataService::class.java)
        val call = service.getEventParticipantsCount(event_id)
        call.enqueue(object : Callback<Int> {
            override fun onFailure(call: Call<Int>, t: Throwable) {
                Log.e("EventParticipants API Error", "API call failed: ${t.message}")

                var count =0
                viewModelScope.launch {
                    try {
                         count = repository.retrieveCount(event_id)
                        eventParticipantsCount.postValue(count)
                    } catch (e: Exception) {
                        Log.e("EventParticipants", "${e.message}")
                    }
                }

            }
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if (response.isSuccessful) {
                    val count = response.body()
                    Log.e("AA","${count}")
                    eventParticipantsCount.postValue(count)
                } else {
                    Log.e("EventParticipants API Error", "API response not successful, code: ${response.code()}")
                    eventParticipantsCount.postValue(null)
                }
            }
        })
    }




    fun deleteEventPart(event_id : Int, user_id : Int){
        val service = RetrofitClientInstance.retrofitInstance!!.create(GetEventParticipantsDataService::class.java)
        val call = service.deleteEventParticipants(event_id,user_id)
        call.enqueue(object : Callback<EventParticipants?> {

            override fun onFailure(call: Call<EventParticipants?>, t: Throwable) {
                Log.e("deleteEvent function", "onFailure function")
                deleteEventLiveData.postValue(null)
            }

            override fun onResponse(call: Call<EventParticipants?>, response: Response<EventParticipants?>) {
                if(response.isSuccessful) {

                    deleteEventLiveData.postValue(response.body())
//                    removeEventFromLocalDatabase()
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





