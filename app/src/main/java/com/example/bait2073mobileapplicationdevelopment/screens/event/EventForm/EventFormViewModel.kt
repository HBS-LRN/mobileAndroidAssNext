package com.example.bait2073mobileapplicationdevelopment.screens.event.EventForm

import EventParticipantsRepository
import android.app.Application
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bait2073mobileapplicationdevelopment.database.HealthyLifeDatabase
import com.example.bait2073mobileapplicationdevelopment.entities.Event
import com.example.bait2073mobileapplicationdevelopment.entities.EventParticipants
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetEventDataService
import com.example.bait2073mobileapplicationdevelopment.repository.EventRepository
import com.example.bait2073mobileapplicationdevelopment.retrofitclient.RetrofitClientInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

class EventFormViewModel (application: Application): ViewModel() {
    private val repository : EventRepository

    private val repositoryEP : EventParticipantsRepository
    val recyclerListDataDao: LiveData<List<EventParticipants>>

    lateinit var createNewEventLiveData: MutableLiveData<Event?>
    lateinit var loadEventData: MutableLiveData<Event?>

    lateinit var eventDataDao: Event
    private val _countdownLiveData = MutableLiveData<String>()

    private var countdownTimer: CountDownTimer? = null


    init {
        val dao = HealthyLifeDatabase.getDatabase(application).eventDao()
        repository = EventRepository(dao)
        createNewEventLiveData = MutableLiveData()
        loadEventData = MutableLiveData()


        val daoEventPart = HealthyLifeDatabase.getDatabase(application).eventPartDao()
        repositoryEP = EventParticipantsRepository(daoEventPart)
        recyclerListDataDao = repositoryEP.retrieve()

    }
    fun startCountdown(targetDateStr: String) {
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

            // Parse the string into a Date object
            val targetDate = dateFormat.parse(targetDateStr)

            // Get the timestamp in milliseconds
            val targetDateMillis = targetDate.time

            countdownTimer?.cancel()

            countdownTimer = object : CountDownTimer(targetDateMillis - System.currentTimeMillis(), 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val seconds = (millisUntilFinished / 1000) % 60
                    val minutes = ((millisUntilFinished / (1000 * 60)) % 60)
                    val hours = ((millisUntilFinished / (1000 * 60 * 60)) % 24)

                    val formattedTime = String.format("%02d : %02d : %02d", hours, minutes, seconds)

                    _countdownLiveData.postValue(formattedTime)
                }

                override fun onFinish() {
                    _countdownLiveData.postValue("STARTING")
                }
            }

            countdownTimer?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun observeCountdown(owner: LifecycleOwner, observer: Observer<String>) {
        _countdownLiveData.observe(owner, observer)
    }



    fun getCreateNewEventObservable(): MutableLiveData<Event?> {
        return createNewEventLiveData
    }


    fun getLoadEventObservable(): MutableLiveData<Event?> {
        return loadEventData
    }

    fun createEvent(event: Event) {
        val service = RetrofitClientInstance.retrofitInstance!!.create(GetEventDataService::class.java)
        val call = service.addEvent(event)
        call.enqueue(object : Callback<Event?> {
            override fun onFailure(call: Call<Event?>, t: Throwable) {
                Log.e("createEvent onFailure", "createEvent onFailure ${t}")
                createNewEventLiveData.postValue(null)
            }

            override fun onResponse(call: Call<Event?>, response: Response<Event?>) {
                if (response.isSuccessful) {
                    val resposne = response.body()
                    Log.i("createEvent onResponse isSuccessful", "$resposne")
                    createNewEventLiveData.postValue(response.body())
                } else {
                    val resposne = response.body()
                    val errorBody = response.errorBody()?.string()
                    val responseCode = response.code()
                    val responseMessage = response.message()
                    Log.e("createEvent onResponse not successful", "Response is Notsuccessful. Code: $responseCode, Message: $responseMessage, Error Body: $errorBody")
                    createNewEventLiveData.postValue(null)
                }
            }
        })
    }





    fun updateEvent(event_id: Int, event: Event) {
        val service = RetrofitClientInstance.retrofitInstance!!.create(GetEventDataService::class.java)
        val call = service.updateEvent(event_id, event)
        call.enqueue(object : Callback<Event?> {
            override fun onFailure(call: Call<Event?>, t: Throwable) {
                Log.e("updateEvent onFailure", "failure")
                createNewEventLiveData.postValue(null)
            }

            override fun onResponse(call: Call<Event?>, response: Response<Event?>) {
                if (response.isSuccessful) {
                    createNewEventLiveData.postValue(response.body())
                } else {

                    val resposne = response.body()
                    val errorBody = response.errorBody()?.string()
                    val responseCode = response.code()
                    val responseMessage = response.message()
                    Log.e("updateEvent onResponse not successful", "Response is Notsuccessful. Code: $responseCode, Message: $responseMessage, Error Body: $errorBody")
                    createNewEventLiveData.postValue(null)
                }
            }
        })
    }

    fun updateStatusEvent(event_id: Int, event: Event) {
        val service = RetrofitClientInstance.retrofitInstance!!.create(GetEventDataService::class.java)
        val call = service.updateEvent(event_id, event)
        call.enqueue(object : Callback<Event?> {
            override fun onFailure(call: Call<Event?>, t: Throwable) {
                Log.e("updateEvent onFailure", "failure")
                createNewEventLiveData.postValue(null)
            }

            override fun onResponse(call: Call<Event?>, response: Response<Event?>) {
                if (response.isSuccessful) {
                    createNewEventLiveData.postValue(response.body())
                } else {

                    val resposne = response.body()
                    val errorBody = response.errorBody()?.string()
                    val responseCode = response.code()
                    val responseMessage = response.message()
                    Log.e("updateEvent onResponse not successful", "Response is Notsuccessful. Code: $responseCode, Message: $responseMessage, Error Body: $errorBody")
                    createNewEventLiveData.postValue(null)
                }
            }
        })
    }


    fun getEventData(event_id: Int?) {
        val service = RetrofitClientInstance.retrofitInstance!!.create(GetEventDataService::class.java)
        val call = service.getEvent(event_id!!)
        call.enqueue(object : Callback<Event?> {

            override fun onFailure(call: Call<Event?>, t: Throwable) {
                Log.e("getEventData onFailure", "wandan")

                if(retrieveEventByIdLocal(event_id)){
                    Log.e("getEventDataById ", "Local envet Id")
                }else{
                    Log.e("getEventDataById ", "Unsuccessful Local envet Id")
                    loadEventData.postValue(null)
                }
            }

            override fun onResponse(call: Call<Event?>, response: Response<Event?>) {
                if (response.isSuccessful) {
                    val resposne = response.body()
                    Log.i("getEventData onResponse isSuccessful", "$resposne")
                    loadEventData.postValue(response.body())
                } else {
                    Log.i("getEventData onResponse notSuccessful", "ggla")
                    loadEventData.postValue(null)
                }
            }
        })
    }

    fun retrieveEventByIdLocal(event_id : Int) : Boolean {
        var success = false
        viewModelScope.launch(Dispatchers.IO) {
            val localData = repository.retrieveId(event_id)
            val localEPData = repositoryEP.retrieveId(event_id)
            // Check if localData is not null and post it to the LiveData
            if (localData != null) {
                loadEventData.postValue(localData)
                eventDataDao = localData
                success = true
            } else if(localEPData != null){
                loadEventData.postValue(localEPData)
                eventDataDao = localEPData
                success = true
            }
            else {
                Log.e("getEventData onFailure", "Local data retrieval failed")
                loadEventData.postValue(null)
                success = false
            }
        }
        return success
    }

}