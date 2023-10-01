package com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseList

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bait2073mobileapplicationdevelopment.entities.Disease
import com.example.bait2073mobileapplicationdevelopment.entities.Hospital
import com.example.bait2073mobileapplicationdevelopment.entities.Symptom
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetDiseaseDataService
import com.example.bait2073mobileapplicationdevelopment.database.HealthyLifeDatabase
import com.example.bait2073mobileapplicationdevelopment.entities.DiseaseSymptom_Room
import com.example.bait2073mobileapplicationdevelopment.repository.DiseaseRepository
import com.example.bait2073mobileapplicationdevelopment.repository.HospitalRepository
import com.example.bait2073mobileapplicationdevelopment.retrofitclient.RetrofitClientInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback

class DiseaseListViewModel(application: Application) : AndroidViewModel(application){

    private val apiService = RetrofitClientInstance.retrofitInstance!!.create(GetDiseaseDataService::class.java)
    var diseaseListMut = MutableLiveData<List<Disease?>>()
    var deleteDiseaseLiveData: MutableLiveData<Disease?> = MutableLiveData()
    var specificDiseaseLiveData: MutableLiveData<Disease?> = MutableLiveData()

    val allDisease : LiveData<List<Disease>>
    val diseaseListDataDao : LiveData<List<Disease>>


    private val repository : DiseaseRepository
    init{
        val dao = HealthyLifeDatabase.getDatabase(application).diseaseDao()
        repository = DiseaseRepository(dao)
        allDisease = repository.allDiseases
        diseaseListDataDao = repository.retrieve()
    }

    fun getDeleteDiseaseObservable(): MutableLiveData<Disease?> {
        return deleteDiseaseLiveData
    }

    fun getDiseaseListObservable(): MutableLiveData<List<Disease?>> {
        return diseaseListMut
    }
    fun getSpecificDiseaseObservable(): MutableLiveData<Disease?> {
        return specificDiseaseLiveData
    }


    fun getDiseaseList() {
        apiService.getDiseaseList().enqueue(object : Callback<List<Disease>> {

            override fun onResponse(call: Call<List<Disease>>, response: Response<List<Disease>>) {
                if (response.isSuccessful) {
                    val diseaseLists = response.body()
//                        Log.e("gg", "Response not successful, code: ${diseaseLists}")
                    if (!diseaseLists.isNullOrEmpty()) {
                        // Insert the disease data into the Room Database
                        diseaseListMut.postValue(response.body())
                        insertDiseaseDataIntoRoomDb(diseaseLists)
                    } else {
                        // Handle the case where the response is empty
                        Log.e("API Response", "Response body is empty")
                    }
                } else {
                    // Handle the case where the API response is not successful
                    Log.e("API Response", "Response not successful, code: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<List<Disease>>, t: Throwable) {
                // Handle network or API errors
                Log.e("API Error", "Failed to fetch data: ${t.message}", t)
            }

        })
    }

    fun insertDisease(disease: Disease)= viewModelScope.launch(Dispatchers.IO){
        repository.insert(disease)
    }

    fun insertDiseaseDataIntoRoomDb(diseases: List<Disease>) {
        viewModelScope.launch {
            this.let {
                try {
                    for (disease in diseases) {
                        Log.d("InsertDiseaseDataIntoRoomDb", "Inserting disease with ID: ${disease.id}")
                        insertDisease(
                            Disease(
                                id = disease.id,
                                disease_name = disease.disease_name,
                                disease_image = disease.disease_image ?: "",
                                disease_causes = disease.disease_causes ?: "",
                                disease_description = disease.disease_description ?: "",
                            )
                        )
                    }
                }catch (e: Exception) {
                    Log.e("InsertDataIntoRoomDb", "Error inserting data into Room Database: ${e.message}", e)
                }
            }
        }

    }
     fun removeDiseaseFromLocalDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }
    fun deleteDisease(disease: Disease) {

        apiService.deleteDisease(disease.id?:0).enqueue(object : Callback<Disease?> {
            override fun onFailure(call: Call<Disease?>, t: Throwable) {
                Log.e("error", "?error")
                deleteDiseaseLiveData.postValue(null)
            }

            override fun onResponse(call: Call<Disease?>, response: Response<Disease?>) {
                if(response.isSuccessful) {
                    Log.e("Response", "Response body empty")
                    deleteDiseaseLiveData.postValue(response.body())
                    removeDiseaseFromLocalDatabase()
                } else {
                    Log.e("Response", "Response body empty")
                    deleteDiseaseLiveData.postValue(null)
                }
            }
        })
    }

    fun getSpecificDisease(disease_id:Int?) {

        apiService.getDisease(disease_id).enqueue(object : Callback<Disease?> {
            override fun onFailure(call: Call<Disease?>, t: Throwable) {
                Log.e("error", "?error")
                specificDiseaseLiveData.postValue(null)
            }

            override fun onResponse(call: Call<Disease?>, response: Response<Disease?>) {
                if(response.isSuccessful) {
                    Log.e("Response", "Response sucess")
                   specificDiseaseLiveData.postValue(response.body())
                } else {
                    Log.e("Response", "Response body empty")
                    specificDiseaseLiveData.postValue(null)
                }
            }
        })
    }
}
