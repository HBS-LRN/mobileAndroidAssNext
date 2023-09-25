package com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseSymptomList

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bait2073mobileapplicationdevelopment.entities.Disease
import com.example.bait2073mobileapplicationdevelopment.entities.Disease_Symptom
import com.example.bait2073mobileapplicationdevelopment.entities.Symptom
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetDiseaseSymptomDataService
import com.example.bait2073mobileapplicationdevelopment.database.HealthyLifeDatabase
import com.example.bait2073mobileapplicationdevelopment.repository.DiseaseRepository
import com.example.bait2073mobileapplicationdevelopment.repository.DiseaseSymptomRepository
import com.example.bait2073mobileapplicationdevelopment.retrofitclient.RetrofitClientInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiseaseSymptomListViewModel (application: Application) : AndroidViewModel(application){

    private val apiService = RetrofitClientInstance.retrofitInstance!!.create(
        GetDiseaseSymptomDataService::class.java
    )
    var diseaseSymptomListMut = MutableLiveData<List<Disease_Symptom?>>()
    var deleteDiseaseSymptomLiveData: MutableLiveData<Disease_Symptom?> = MutableLiveData()
    var loadDiseaseSymptomData: MutableLiveData<List<Disease_Symptom>?> = MutableLiveData()
    var loadSymptomDiseaseData: MutableLiveData<List<Disease_Symptom>?> = MutableLiveData()

    val allDiseaseSymptom : LiveData<List<Disease_Symptom>>
    val diseaseSymptomListDataDao : LiveData<List<Disease_Symptom>>
    private val repository : DiseaseSymptomRepository
    init{
        val dao = HealthyLifeDatabase.getDatabase(application).diseaseSymptomDao()
        repository = DiseaseSymptomRepository(dao)
        allDiseaseSymptom = repository.allDiseaseSymptoms
        diseaseSymptomListDataDao = repository.retrieve()
    }

    fun getLoadDiseaseSymptomObservable(): MutableLiveData<List<Disease_Symptom>?> {
        return loadDiseaseSymptomData
    }


    fun getDeleteDiseaseSymptomObservable(): MutableLiveData<Disease_Symptom?> {
        return deleteDiseaseSymptomLiveData
    }

    fun getDiseaseSymptomListObservable(): MutableLiveData<List<Disease_Symptom?>> {
        return diseaseSymptomListMut
    }
    fun getLoadSymptomDiseaseObservable(): MutableLiveData<List<Disease_Symptom>?> {
        return loadSymptomDiseaseData
    }


    fun getDiseaseSymptomList() {
        apiService.getDiseaseSymptomList().enqueue(object : Callback<List<Disease_Symptom>> {

            override fun onResponse(
                call: Call<List<Disease_Symptom>>,
                response: Response<List<Disease_Symptom>>
            ) {
                if (response.isSuccessful) {
                    val diseaseSymptomLists = response.body()
//                        Log.e("gg", "Response not successful, code: ${diseaseSymptomLists}")
                    if (!diseaseSymptomLists.isNullOrEmpty()) {
                        // Insert the disease symptom data into the Room Database
                        diseaseSymptomListMut.postValue(response.body())
                        insertDiseaseSymptomDataIntoRoomDb(diseaseSymptomLists)
                    } else {
                        // Handle the case where the response is empty
                        Log.e("API Response", "Response body is empty")
                    }
                } else {
                    // Handle the case where the API response is not successful
                    Log.e("API Response", "Response not successful, code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Disease_Symptom>>, t: Throwable) {
                // Handle network or API errors
                Log.e("API Error", "Failed to fetch data: ${t.message}", t)
            }

        })
    }

    fun insertDiseaseSymptom(diseaseSymptom: Disease_Symptom)= viewModelScope.launch(Dispatchers.IO){
        repository.insert(diseaseSymptom)
    }

    fun insertDiseaseSymptomDataIntoRoomDb(diseaseSymptoms: List<Disease_Symptom>) {
        viewModelScope.launch {
            this.let {
                try {
                    for (diseaseSymptom in diseaseSymptoms) {
                        Log.d("InsertDiseaseSymptomDataIntoRoomDb", "Inserting disease with ID: ${diseaseSymptom.id}")
                        insertDiseaseSymptom(
                            Disease_Symptom(
                                id = diseaseSymptom.id,
                                disease_id = diseaseSymptom.disease_id,
                                symptom_id = diseaseSymptom.symptom_id,
                            )
                        )
                    }
                }catch (e: Exception) {
                    Log.e("InsertDataIntoRoomDb", "Error inserting data into Room Database: ${e.message}", e)
                }
            }
        }

    }
    private fun removeDiseaseSymptomFromLocalDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }

    fun deleteDiseaseSymptom(diseaseSymptom: Disease_Symptom) {

        apiService.deleteDiseaseSymptom(diseaseSymptom.id ?: 0)
            .enqueue(object : Callback<Disease_Symptom?> {
                override fun onFailure(call: Call<Disease_Symptom?>, t: Throwable) {
                    Log.e("error", "?error")
                    deleteDiseaseSymptomLiveData.postValue(null)
                }

                override fun onResponse(
                    call: Call<Disease_Symptom?>,
                    response: Response<Disease_Symptom?>
                ) {
                    if (response.isSuccessful) {
                        Log.e("Response", "Response body empty")
                        deleteDiseaseSymptomLiveData.postValue(response.body())
                        removeDiseaseSymptomFromLocalDatabase()
                    } else {
                        Log.e("Response", "Response body empty")
                        deleteDiseaseSymptomLiveData.postValue(null)
                    }
                }
            })
    }

    fun getDiseaseSymptomData(disease_id: Int) {
        apiService.getDiseaseSymptom(disease_id).enqueue(object : Callback<List<Disease_Symptom>> {
            override fun onResponse(
                call: Call<List<Disease_Symptom>>,
                response: Response<List<Disease_Symptom>>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.i("hahagetdiseasesymptom", "$result")
                    loadDiseaseSymptomData.postValue(response.body())
                } else {
                    Log.i("haha", "not workinggg")
                    loadDiseaseSymptomData.postValue(null)
                }
            }

            override fun onFailure(call: Call<List<Disease_Symptom>>, t: Throwable) {
                Log.e("API Error", "Failed to fetch data: ${t.message}", t)
                loadDiseaseSymptomData.postValue(null)
            }
        })
    }

    fun getSymptomDiseaseData(symptom_id: Int) {
        apiService.getSymptomDisease(symptom_id).enqueue(object : Callback<List<Disease_Symptom>> {
            override fun onResponse(
                call: Call<List<Disease_Symptom>>,
                response: Response<List<Disease_Symptom>>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.i("hahagetsymptomdisease", "$result")
                    loadSymptomDiseaseData.postValue(response.body())
                } else {
                    Log.i("haha", "not workinggg")
                    loadSymptomDiseaseData.postValue(null)
                }
            }

            override fun onFailure(call: Call<List<Disease_Symptom>>, t: Throwable) {
                Log.e("API Error", "Failed to fetch data: ${t.message}", t)
                loadSymptomDiseaseData.postValue(null)
            }
        })
    }
}
