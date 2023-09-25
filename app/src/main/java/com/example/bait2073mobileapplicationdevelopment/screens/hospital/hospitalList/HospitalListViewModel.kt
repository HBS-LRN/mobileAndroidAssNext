package com.example.bait2073mobileapplicationdevelopment.screens.hospital.hospitalList

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bait2073mobileapplicationdevelopment.entities.Hospital
import com.example.bait2073mobileapplicationdevelopment.entities.Recipe
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetHospitalDataService
import com.example.bait2073mobileapplicationdevelopment.database.HealthyLifeDatabase
import com.example.bait2073mobileapplicationdevelopment.repository.HospitalRepository
import com.example.bait2073mobileapplicationdevelopment.repository.RecipeRepository
import com.example.bait2073mobileapplicationdevelopment.retrofitclient.RetrofitClientInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HospitalListViewModel (application: Application) : AndroidViewModel(application){

    private val apiService = RetrofitClientInstance.retrofitInstance!!.create(GetHospitalDataService::class.java)
    var hospitalListMut = MutableLiveData<List<Hospital?>>()
    var deleteHospitalLiveData: MutableLiveData<Hospital?> = MutableLiveData()

    val allHospital : LiveData<List<Hospital>>
    val hospitalListDataDao : LiveData<List<Hospital>>
    private val repository : HospitalRepository
    init{
        val dao = HealthyLifeDatabase.getDatabase(application).hospitalDao()
        repository = HospitalRepository(dao)
        allHospital = repository.allHospitals
        hospitalListDataDao = repository.retrieve()
    }

    fun getDeleteHospitalObservable(): MutableLiveData<Hospital?> {
        return deleteHospitalLiveData
    }

    fun getHospitalListObservable(): MutableLiveData<List<Hospital?>> {
        return hospitalListMut
    }


    fun getHospitalList() {
        apiService.getHospitalList().enqueue(object : Callback<List<Hospital>> {

            override fun onResponse(call: Call<List<Hospital>>, response: Response<List<Hospital>>) {
                if (response.isSuccessful) {
                    val hospitalLists = response.body()

                    if (!hospitalLists.isNullOrEmpty()) {
                        // Insert the hospital data into the Room Database
                        hospitalListMut.postValue(response.body())
                        insertHospitalDataIntoRoomDb(hospitalLists)
                    } else {
                        // Handle the case where the response is empty
                        Log.e("API Response", "Response body is empty")
                    }
                } else {
                    // Handle the case where the API response is not successful
                    Log.e("API Response", "Response not successful, code: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<List<Hospital>>, t: Throwable) {
                // Handle network or API errors
                Log.e("API Error", "Failed to fetch data: ${t.message}", t)
            }

        })
    }

    fun insertHospital(hospital: Hospital)= viewModelScope.launch(Dispatchers.IO){
        repository.insert(hospital)
    }

    fun insertHospitalDataIntoRoomDb(hospitals: List<Hospital>) {
        viewModelScope.launch {
            this.let {
                try {
                    for (hospital in hospitals) {
                        Log.d("InsertHospitalDataIntoRoomDb", "Inserting hospital with ID: ${hospital.id}")
                        insertHospital(
                            Hospital(
                                id = hospital.id,
                                hospital_name = hospital.hospital_name,
                                hospital_contact = hospital.hospital_contact ?: "",
                                hospital_address = hospital.hospital_address ?: "",
                            )
                        )
                    }
                }catch (e: Exception) {
                    Log.e("InsertDataIntoRoomDb", "Error inserting data into Room Database: ${e.message}", e)
                }
            }
        }

    }
    private fun removeHospitalFromLocalDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }
    fun deleteHospital(hospital: Hospital) {

        apiService.deleteHospital(hospital.id?:0).enqueue(object : Callback<Hospital?> {
            override fun onFailure(call: Call<Hospital?>, t: Throwable) {
                Log.e("error", "??error")
                deleteHospitalLiveData.postValue(null)
            }
            override fun onResponse(call: Call<Hospital?>, response: Response<Hospital?>) {
                if(response.isSuccessful) {
                    Log.e("Response", "Response body empty")
                    deleteHospitalLiveData.postValue(response.body())
                    removeHospitalFromLocalDatabase()
                } else {
                    Log.e("Response", "Response body empty")
                    deleteHospitalLiveData.postValue(null)
                }
            }
        })
    }
}
