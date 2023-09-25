package com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseHospitalList

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bait2073mobileapplicationdevelopment.entities.Disease_Hospital
import com.example.bait2073mobileapplicationdevelopment.entities.Disease_Recipe
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetDiseaseHospitalDataService
import com.example.bait2073mobileapplicationdevelopment.database.HealthyLifeDatabase
import com.example.bait2073mobileapplicationdevelopment.repository.DiseaseHospitalRepository
import com.example.bait2073mobileapplicationdevelopment.repository.DiseaseRecipeRepository
import com.example.bait2073mobileapplicationdevelopment.retrofitclient.RetrofitClientInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiseaseHospitalListViewModel (application: Application) : AndroidViewModel(application){

    private val apiService = RetrofitClientInstance.retrofitInstance!!.create(
        GetDiseaseHospitalDataService::class.java
    )
    var diseaseHospitalListMut = MutableLiveData<List<Disease_Hospital?>>()
    var deleteDiseaseHospitalLiveData: MutableLiveData<Disease_Hospital?> = MutableLiveData()
    var loadDiseaseHospitalData: MutableLiveData<List<Disease_Hospital>?> = MutableLiveData()

    val allDiseaseHospital : LiveData<List<Disease_Hospital>>
    val diseaseHospitalListDataDao : LiveData<List<Disease_Hospital>>
    private val repository : DiseaseHospitalRepository
    init{
        val dao = HealthyLifeDatabase.getDatabase(application).diseaseHospitalDao()
        repository = DiseaseHospitalRepository(dao)
        allDiseaseHospital = repository.allDiseaseHospitals
        diseaseHospitalListDataDao = repository.retrieve()
    }

    fun getLoadDiseaseHospitalObservable(): MutableLiveData<List<Disease_Hospital>?> {
        return loadDiseaseHospitalData
    }

    fun getDeleteDiseaseHospitalObservable(): MutableLiveData<Disease_Hospital?> {
        return deleteDiseaseHospitalLiveData
    }

    fun getDiseaseHospitalListObservable(): MutableLiveData<List<Disease_Hospital?>> {
        return diseaseHospitalListMut
    }

    fun getDiseaseHospitalList() {
        apiService.getDiseaseHospitalList().enqueue(object : Callback<List<Disease_Hospital>> {

            override fun onResponse(
                call: Call<List<Disease_Hospital>>,
                response: Response<List<Disease_Hospital>>
            ) {
                if (response.isSuccessful) {
                    val diseaseHospitalLists = response.body()
//                        Log.e("gg", "Response not successful, code: ${diseaseHospitalLists}")
                    if (!diseaseHospitalLists.isNullOrEmpty()) {
                        diseaseHospitalListMut.postValue(response.body())
                        insertDiseaseHospitalDataIntoRoomDb(diseaseHospitalLists)
                    } else {
                        // Handle the case where the response is empty
                        Log.e("API Response", "Response body is empty")
                    }
                } else {
                    // Handle the case where the API response is not successful
                    Log.e("API Response", "Response not successful, code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Disease_Hospital>>, t: Throwable) {
                // Handle network or API errors
                Log.e("API Error", "Failed to fetch data: ${t.message}", t)
            }

        })
    }

    fun insertDiseaseHospital(diseaseHospital: Disease_Hospital)= viewModelScope.launch(Dispatchers.IO){
        repository.insert(diseaseHospital)
    }

    fun insertDiseaseHospitalDataIntoRoomDb(diseaseHospitals: List<Disease_Hospital>) {
        viewModelScope.launch {
            this.let {
                try {
                    for (diseaseHospital in diseaseHospitals) {
                        Log.d("InsertDiseaseHospitalDataIntoRoomDb", "Inserting disease recipe with ID: ${diseaseHospital.id}")
                        insertDiseaseHospital(
                            Disease_Hospital(
                                id = diseaseHospital.id,
                                disease_id = diseaseHospital.disease_id,
                                hospital_id = diseaseHospital.hospital_id,
                            )
                        )
                    }
                }catch (e: Exception) {
                    Log.e("InsertDataIntoRoomDb", "Error inserting data into Room Database: ${e.message}", e)
                }
            }
        }

    }
    private fun removeDiseaseHospitalFromLocalDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }

    fun deleteDiseaseHospital(diseaseHospital: Disease_Hospital) {

        apiService.deleteDiseaseHospital(diseaseHospital.id ?: 0)
            .enqueue(object : Callback<Disease_Hospital?> {
                override fun onFailure(call: Call<Disease_Hospital?>, t: Throwable) {
                    Log.e("error", "?error")
                    deleteDiseaseHospitalLiveData.postValue(null)
                }

                override fun onResponse(
                    call: Call<Disease_Hospital?>,
                    response: Response<Disease_Hospital?>
                ) {
                    if (response.isSuccessful) {
                        Log.e("Response", "Response body empty")
                        deleteDiseaseHospitalLiveData.postValue(response.body())
                        removeDiseaseHospitalFromLocalDatabase()
                    } else {
                        Log.e("Response", "Response body empty")
                        deleteDiseaseHospitalLiveData.postValue(null)
                    }
                }
            })
    }

    fun getDiseaseHospitalData(disease_id: Int) {
        apiService.getDiseaseHospital(disease_id).enqueue(object : Callback<List<Disease_Hospital>> {
            override fun onResponse(
                call: Call<List<Disease_Hospital>>,
                response: Response<List<Disease_Hospital>>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.i("hahagetdiseaseHospital", "$result")
                    loadDiseaseHospitalData.postValue(response.body())
                } else {
                    Log.i("haha", "not workinggg")
                    loadDiseaseHospitalData.postValue(null)
                }
            }
            override fun onFailure(call: Call<List<Disease_Hospital>>, t: Throwable) {
                Log.e("API Error", "Failed to fetch data: ${t.message}", t)
                loadDiseaseHospitalData.postValue(null)
            }
        })
    }
}
