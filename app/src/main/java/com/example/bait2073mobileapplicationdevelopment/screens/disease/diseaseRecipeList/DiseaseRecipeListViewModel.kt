package com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseRecipeList

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bait2073mobileapplicationdevelopment.entities.Disease_Recipe
import com.example.bait2073mobileapplicationdevelopment.entities.Disease_Symptom
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetDiseaseRecipeDataService
import com.example.bait2073mobileapplicationdevelopment.database.HealthyLifeDatabase
import com.example.bait2073mobileapplicationdevelopment.entities.DiseaseRecipe_Room
import com.example.bait2073mobileapplicationdevelopment.entities.DiseaseSymptom_Room
import com.example.bait2073mobileapplicationdevelopment.repository.DiseaseRecipeRepository
import com.example.bait2073mobileapplicationdevelopment.repository.DiseaseRecipeRoomRepository
import com.example.bait2073mobileapplicationdevelopment.repository.DiseaseSymptomRepository
import com.example.bait2073mobileapplicationdevelopment.repository.DiseaseSymptomRoomRepository
import com.example.bait2073mobileapplicationdevelopment.retrofitclient.RetrofitClientInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiseaseRecipeListViewModel (application: Application) : AndroidViewModel(application){

    private val apiService = RetrofitClientInstance.retrofitInstance!!.create(
        GetDiseaseRecipeDataService::class.java
    )
    var diseaseRecipeListMut = MutableLiveData<List<Disease_Recipe?>>()
    var deleteDiseaseRecipeLiveData: MutableLiveData<Disease_Recipe?> = MutableLiveData()
    var loadDiseaseRecipeData: MutableLiveData<List<Disease_Recipe>?> = MutableLiveData()

    val allDiseaseRecipe : LiveData<List<Disease_Recipe>>
    val diseaseRecipeListDataDao : LiveData<List<Disease_Recipe>>
    val diseaseRecipeDataFromRoomDB : LiveData<List<DiseaseRecipe_Room>>

    private val repository : DiseaseRecipeRepository
    private val roomRepository : DiseaseRecipeRoomRepository
    init{
        val dao = HealthyLifeDatabase.getDatabase(application).diseaseRecipeDao()
        repository = DiseaseRecipeRepository(dao)
        val dao2 = HealthyLifeDatabase.getDatabase(application).diseaseRecipeRoomDao()
        roomRepository = DiseaseRecipeRoomRepository(dao2)
        diseaseRecipeDataFromRoomDB = roomRepository.allDiseaseRecipesRoom
        allDiseaseRecipe = repository.allDiseaseRecipes
        diseaseRecipeListDataDao = repository.retrieve()
    }

    fun getLoadDiseaseRecipeObservable(): MutableLiveData<List<Disease_Recipe>?> {
        return loadDiseaseRecipeData
    }


    fun getDeleteDiseaseRecipeObservable(): MutableLiveData<Disease_Recipe?> {
        return deleteDiseaseRecipeLiveData
    }

    fun getDiseaseRecipeListObservable(): MutableLiveData<List<Disease_Recipe?>> {
        return diseaseRecipeListMut
    }

    fun getDiseaseRecipeList() {
        apiService.getDiseaseRecipeList().enqueue(object : Callback<List<Disease_Recipe>> {

            override fun onResponse(
                call: Call<List<Disease_Recipe>>,
                response: Response<List<Disease_Recipe>>
            ) {
                if (response.isSuccessful) {
                    val diseaseRecipeLists = response.body()
//                        Log.e("gg", "Response not successful, code: ${diseaseRecipeLists}")
                    if (!diseaseRecipeLists.isNullOrEmpty()) {
                        // Insert the disease Recipe data into the Room Database
                        diseaseRecipeListMut.postValue(response.body())
                        insertDiseaseRecipeDataIntoRoomDb(diseaseRecipeLists)
                    } else {
                        // Handle the case where the response is empty
                        Log.e("API Response", "Response body is empty")
                    }
                } else {
                    // Handle the case where the API response is not successful
                    Log.e("API Response", "Response not successful, code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Disease_Recipe>>, t: Throwable) {
                // Handle network or API errors
                Log.e("API Error", "Failed to fetch data: ${t.message}", t)
            }

        })
    }

    fun insertDiseaseRecipe(diseaseRecipe: Disease_Recipe)= viewModelScope.launch(Dispatchers.IO){
        repository.insert(diseaseRecipe)
    }

    fun insertDiseaseRecipeDataIntoRoomDb(diseaseRecipes: List<Disease_Recipe>) {
        viewModelScope.launch {
            this.let {
                try {
                    for (diseaseRecipe in diseaseRecipes) {
                        Log.d("InsertDiseaseRecipeDataIntoRoomDb", "Inserting disease recipe with ID: ${diseaseRecipe.id}")
                        insertDiseaseRecipe(
                            Disease_Recipe(
                                id = diseaseRecipe.id,
                                disease_id = diseaseRecipe.disease_id,
                                recipe_id = diseaseRecipe.recipe_id,
                            )
                        )
                    }
                }catch (e: Exception) {
                    Log.e("InsertDataIntoRoomDb", "Error inserting data into Room Database: ${e.message}", e)
                }
            }
        }

    }
    fun insertDiseaseRecipeRoom(diseaseRecipeRoom: DiseaseRecipe_Room)= viewModelScope.launch(Dispatchers.IO){
        roomRepository.insert(diseaseRecipeRoom)
    }

    fun insertDiseaseRecipeRoomDataIntoRoomDb(diseaseRecipeRoom: DiseaseRecipe_Room) {
        viewModelScope.launch {
            this.let {
                try {
                    insertDiseaseRecipeRoom(
                        DiseaseRecipe_Room(
                            id = diseaseRecipeRoom.id,
                            disease_id = diseaseRecipeRoom.disease_id,
                            disease_name = diseaseRecipeRoom.disease_name,
                            recipe_id = diseaseRecipeRoom.recipe_id,
                            recipe_name = diseaseRecipeRoom.recipe_name,
                            recipe_image = diseaseRecipeRoom.recipe_image,
                            recipe_description = diseaseRecipeRoom.recipe_description,
                            recipe_servings =  diseaseRecipeRoom.recipe_servings
                        )
                    )
                }catch (e: Exception) {
                    Log.e("InsertDataIntoRoomDb", "Error inserting data into Room Database: ${e.message}", e)
                }
            }
        }

    }


    private fun removeDiseaseRecipeFromLocalDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }

    fun removeDiseaseRecipeRoomFromLocalDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            roomRepository.deleteAll()
        }
    }

    fun deleteDiseaseRecipe(diseaseRecipe: Disease_Recipe) {

        apiService.deleteDiseaseRecipe(diseaseRecipe.id ?: 0)
            .enqueue(object : Callback<Disease_Recipe?> {
                override fun onFailure(call: Call<Disease_Recipe?>, t: Throwable) {
                    Log.e("error", "?error")
                    deleteDiseaseRecipeLiveData.postValue(null)
                }

                override fun onResponse(
                    call: Call<Disease_Recipe?>,
                    response: Response<Disease_Recipe?>
                ) {
                    if (response.isSuccessful) {
                        Log.e("Response", "Response body empty")
                        deleteDiseaseRecipeLiveData.postValue(response.body())
                        removeDiseaseRecipeFromLocalDatabase()
                    } else {
                        Log.e("Response", "Response body empty")
                        deleteDiseaseRecipeLiveData.postValue(null)
                    }
                }
            })
    }

    fun getDiseaseRecipeData(disease_id: Int) {
        apiService.getDiseaseRecipe(disease_id).enqueue(object : Callback<List<Disease_Recipe>> {
            override fun onResponse(
                call: Call<List<Disease_Recipe>>,
                response: Response<List<Disease_Recipe>>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.i("hahagetdiseaseRecipe", "$result")
                    loadDiseaseRecipeData.postValue(response.body())
                } else {
                    Log.i("haha", "not workinggg")
                    loadDiseaseRecipeData.postValue(null)
                }
            }

            override fun onFailure(call: Call<List<Disease_Recipe>>, t: Throwable) {
                Log.e("API Error", "Failed to fetch data: ${t.message}", t)
                loadDiseaseRecipeData.postValue(null)
            }
        })
    }
}
