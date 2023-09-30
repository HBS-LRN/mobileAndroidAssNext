package com.example.bait2073mobileapplicationdevelopment.screens.recipe.recipeList

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bait2073mobileapplicationdevelopment.entities.Recipe
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetRecipeDataService
import com.example.bait2073mobileapplicationdevelopment.database.HealthyLifeDatabase
import com.example.bait2073mobileapplicationdevelopment.repository.RecipeRepository
import com.example.bait2073mobileapplicationdevelopment.retrofitclient.RetrofitClientInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback

class RecipeListViewModel(application: Application) :AndroidViewModel(application){

    var recyclerListData: MutableLiveData<List<Recipe?>> = MutableLiveData()
    var deleteRecipeLiveData: MutableLiveData<Recipe?> = MutableLiveData()
    private val apiService = RetrofitClientInstance.retrofitInstance!!.create(GetRecipeDataService::class.java)
    var specificRecipeLiveData: MutableLiveData<Recipe?> = MutableLiveData()

    val allRecipe : LiveData<List<Recipe>>
    val recipeListDataDao : LiveData<List<Recipe>>
    private val repository : RecipeRepository
    init{
        val dao = HealthyLifeDatabase.getDatabase(application).recipeDao()
        repository = RecipeRepository(dao)
        allRecipe = repository.allRecipes
        recipeListDataDao = repository.retrieve()
    }


    fun getDeleteRecipeObservable(): MutableLiveData<Recipe?> {
        return deleteRecipeLiveData
    }
    fun getRecipeListObservable(): MutableLiveData<List<Recipe?>> {
        return recyclerListData
    }

    fun getRecipeList() {
        apiService.getRecipeList().enqueue(object : Callback<List<Recipe>> {

            override fun onFailure(call: Call<List<Recipe>>, t: Throwable) {
                // Handle network or API errors
                Log.e("API Error", "Failed to fetch data: ${t.message}", t)
            }

            override fun onResponse(call: Call<List<Recipe>>, response: Response<List<Recipe>>) {
                if (response.isSuccessful) {
                    val recipeLists = response.body()
                    Log.e("Response not successful", "Response not successful, code: ${recipeLists}")
                    if (!recipeLists.isNullOrEmpty()) {
                        // Insert the recipe data into the Room Database
                        recyclerListData.postValue(response.body())
                        insertRecipeDataIntoRoomDb(recipeLists)
                    } else {
                        // Handle the case where the response is empty
                        Log.e("API Response", "Response body is empty")
                    }
                } else {
                    // Handle the case where the API response is not successful
                    Log.e("API Response", "Response not successful, code: ${response.code()}")
                }
            }


        })
    }

    fun insertRecipe(recipe: Recipe)= viewModelScope.launch(Dispatchers.IO){
        repository.insert(recipe)
    }

    fun insertRecipeDataIntoRoomDb(recipes: List<Recipe>) {
        viewModelScope.launch {
            this.let {
                try {
                    for (recipe in recipes) {
                        Log.d("InsertRecipeDataIntoRoomDb", "Inserting recipe with ID: ${recipe.id}")
                        insertRecipe(
                            Recipe(
                                id = recipe.id,
                                recipe_name = recipe.recipe_name,
                                recipe_image = recipe.recipe_image ?: "",
                                recipe_description = recipe.recipe_description ?: "",
                                recipe_servings = recipe.recipe_servings,
                            )
                        )
                    }
                }catch (e: Exception) {
                    Log.e("InsertDataIntoRoomDb", "Error inserting data into Room Database: ${e.message}", e)
                }
            }
        }

    }
    private fun removeRecipeFromLocalDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }


    fun deleteRecipe(recipe:Recipe) {

        apiService.deleteRecipe(recipe.id?:0).enqueue(object : Callback<Recipe?> {

            override fun onFailure(call: Call<Recipe?>, t: Throwable) {
                Log.e("API Error", t.message ?: "Unknown error")
                deleteRecipeLiveData.postValue(null)
            }

            override fun onResponse(call: Call<Recipe?>, response: Response<Recipe?>) {
                if(response.isSuccessful) {
                    Log.e("API Response", "Response body is empty")
                    deleteRecipeLiveData.postValue(response.body())
                    removeRecipeFromLocalDatabase()
                } else {
                    Log.e("API Response", "Response body is empty")
                    deleteRecipeLiveData.postValue(null)
                }
            }
        })
    }

    fun getSpecificRecipe(recipe_id:Int?):LiveData<Recipe?> {

        apiService.getRecipe(recipe_id).enqueue(object : Callback<Recipe?> {
            override fun onFailure(call: Call<Recipe?>, t: Throwable) {
                Log.e("error", "?error")
                specificRecipeLiveData.postValue(null)
            }

            override fun onResponse(call: Call<Recipe?>, response: Response<Recipe?>) {
                if(response.isSuccessful) {
                    Log.e("Response", "Response success")
                    specificRecipeLiveData.postValue(response.body())
                } else {
                    Log.e("Response", "Response body empty")
                    specificRecipeLiveData.postValue(null)
                }
            }
        })
        return specificRecipeLiveData
    }

}
