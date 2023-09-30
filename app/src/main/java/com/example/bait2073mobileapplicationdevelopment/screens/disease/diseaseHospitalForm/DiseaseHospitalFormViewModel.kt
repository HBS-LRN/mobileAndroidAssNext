package com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseHospitalForm

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bait2073mobileapplicationdevelopment.database.HealthyLifeDatabase
import com.example.bait2073mobileapplicationdevelopment.entities.DiseaseHospital_Room
import com.example.bait2073mobileapplicationdevelopment.entities.DiseaseSymptom_Room
import com.example.bait2073mobileapplicationdevelopment.entities.Disease_Hospital
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetDiseaseHospitalDataService
import com.example.bait2073mobileapplicationdevelopment.repository.DiseaseHospitalRepository
import com.example.bait2073mobileapplicationdevelopment.repository.DiseaseHospitalRoomRepository
import com.example.bait2073mobileapplicationdevelopment.repository.DiseaseRepository
import com.example.bait2073mobileapplicationdevelopment.retrofitclient.RetrofitClientInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiseaseHospitalFormViewModel  (application: Application) : AndroidViewModel(application){
    private val apiService = RetrofitClientInstance.retrofitInstance!!.create(
        GetDiseaseHospitalDataService::class.java)
    var createDiseaseHospitalLiveData: MutableLiveData<Disease_Hospital?> = MutableLiveData()
    private val roomRepository : DiseaseHospitalRoomRepository
    init{
        val dao = HealthyLifeDatabase.getDatabase(application).diseaseHospitalRoomDao()
        roomRepository = DiseaseHospitalRoomRepository(dao)
    }
    fun getCreateDiseaseHospitalObservable(): MutableLiveData<Disease_Hospital?> {
        return createDiseaseHospitalLiveData
    }

    fun createDiseaseHospital(diseaseHospital: Disease_Hospital) {
        Log.i("creatingdiseaseHospital","{$diseaseHospital}")
        apiService.createDiseaseHospital(diseaseHospital).enqueue(object :
            Callback<Disease_Hospital?> {
            override fun onResponse(call: Call<Disease_Hospital?>, response: Response<Disease_Hospital?>) {
                if (response.isSuccessful) {
                    val res = response.body()
                    Log.i("res","{$res}")
                    createDiseaseHospitalLiveData.postValue(response.body())
                } else {
                    val res = response.body()
                    val errorBody = response.errorBody()?.string()
                    val responseCode = response.code()
                    val responseMessage = response.message()
                    Log.e("unsuccessful", "Response is not successful. Code: $responseCode, Message: $responseMessage, Error Body: $errorBody")
                    createDiseaseHospitalLiveData.postValue(null)
                }
            }

            override fun onFailure(call: Call<Disease_Hospital?>, t: Throwable) {
                Log.e("Error", "Unknown error")
                createDiseaseHospitalLiveData.postValue(null)
            }
        })
    }

    fun insertDiseaseHospitalRoom(diseaseHospitalRoom: DiseaseHospital_Room)= viewModelScope.launch(Dispatchers.IO){
        roomRepository.insert(diseaseHospitalRoom)
    }

    fun insertDiseaseHospitalRoomDataIntoRoomDb(diseaseHospitalRoom: DiseaseHospital_Room) {
        viewModelScope.launch {
            this.let {
                try {
                        insertDiseaseHospitalRoom(
                            DiseaseHospital_Room(
                                id = diseaseHospitalRoom.id,
                                disease_id = diseaseHospitalRoom.disease_id,
                                disease_name = diseaseHospitalRoom.disease_name,
                                hospital_id = diseaseHospitalRoom.hospital_id,
                                hospital_name = diseaseHospitalRoom.hospital_name,
                                hospital_contact = diseaseHospitalRoom.hospital_contact,
                                hospital_address = diseaseHospitalRoom.hospital_address,
                            )
                        )
                }catch (e: Exception) {
                    Log.e("InsertDataIntoRoomDb", "Error inserting data into Room Database: ${e.message}", e)
                }
            }
        }

    }


}