package com.example.bait2073mobileapplicationdevelopment.screens.symptom.symptomList

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bait2073mobileapplicationdevelopment.entities.Symptom
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetSymptomDataService
import com.example.bait2073mobileapplicationdevelopment.database.HealthyLifeDatabase
import com.example.bait2073mobileapplicationdevelopment.repository.SymptomRepository
import com.example.bait2073mobileapplicationdevelopment.retrofitclient.RetrofitClientInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback

class SymptomListViewModel(application: Application) :AndroidViewModel(application){

        private val apiService = RetrofitClientInstance.retrofitInstance!!.create(GetSymptomDataService::class.java)
        var symptomListMut = MutableLiveData<List<Symptom?>>()
        var deleteSymptomLiveData: MutableLiveData<Symptom?> = MutableLiveData()

        val allSymptom : LiveData <List<Symptom>>
        val symptomListDataDao : LiveData<List<Symptom>>
        private val repository : SymptomRepository
    init{
        val dao = HealthyLifeDatabase.getDatabase(application).symptomDao()
        repository = SymptomRepository(dao)
        allSymptom = repository.allSymptoms
        symptomListDataDao = repository.retrieve()
    }


    fun getDeleteSymptomObservable(): MutableLiveData<Symptom?> {
        return deleteSymptomLiveData
    }

    fun getSymptomListObservable(): MutableLiveData<List<Symptom?>> {
        return symptomListMut
    }

    fun getSymptomList() {
            apiService.getSymptomList().enqueue(object : Callback<List<Symptom>> {

                override fun onResponse(call: Call<List<Symptom>>, response: Response<List<Symptom>>) {
                    if (response.isSuccessful) {
                        val symptomLists = response.body()
//                        Log.e("gg", "Response not successful, code: ${symptomLists}")
                        if (!symptomLists.isNullOrEmpty()) {
                            // Insert the symptom data into the Room Database
                            symptomListMut.postValue(response.body())
                            insertSymptomDataIntoRoomDb(symptomLists)
                        } else {
                            // Handle the case where the response is empty
                            Log.e("API Response", "Response body is empty")
                        }
                    } else {
                        // Handle the case where the API response is not successful
                        Log.e("API Response", "Response not successful, code: ${response.code()}")
                    }
            }
                override fun onFailure(call: Call<List<Symptom>>, t: Throwable) {
                    // Handle network or API errors
                    Log.e("API Error", "Failed to fetch data: ${t.message}", t)
                }

            })
    }
    fun insertSymptom(symptom : Symptom)= viewModelScope.launch(Dispatchers.IO){
        repository.insert(symptom)
    }

    fun insertSymptomDataIntoRoomDb(symptoms: List<Symptom>) {
        viewModelScope.launch {
            this.let {
                try {
                    for (symptom in symptoms) {
                        Log.d("InsertSymptomDataIntoRoomDb", "Inserting symptom with ID: ${symptom.id}")
                        insertSymptom(
                            Symptom(
                                id = symptom.id,
                                symptom_name = symptom.symptom_name,
                                symptom_image = symptom.symptom_image ?: "",
                                symptom_description = symptom.symptom_description ?: "",
                            )
                        )
                    }
                }catch (e: Exception) {
                    Log.e("InsertDataIntoRoomDb", "Error inserting data into Room Database: ${e.message}", e)
                }
            }
        }

    }
    private fun removeSymptomFromLocalDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }

    fun deleteSymptom(symptom: Symptom) {

        apiService.deleteSymptom(symptom.id?:0).enqueue(object : Callback<Symptom?> {
            override fun onFailure(call: Call<Symptom?>, t: Throwable) {
                Log.e("error", "?error")
                deleteSymptomLiveData.postValue(null)
            }

            override fun onResponse(call: Call<Symptom?>, response: Response<Symptom?>) {
                if(response.isSuccessful) {
                    Log.e("Response", "Response body empty")
                    deleteSymptomLiveData.postValue(response.body())
                    removeSymptomFromLocalDatabase()
                } else {
                    Log.e("Response", "Response body empty")
                    deleteSymptomLiveData.postValue(null)
                }
            }
        })
    }
}
