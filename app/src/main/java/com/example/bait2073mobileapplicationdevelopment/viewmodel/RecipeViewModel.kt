package com.example.bait2073mobileapplicationdevelopment.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.bait2073mobileapplicationdevelopment.entities.Recipe
import com.example.bait2073mobileapplicationdevelopment.database.HealthyLifeDatabase
import com.example.bait2073mobileapplicationdevelopment.repository.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecipeViewModel (application: Application) : AndroidViewModel(application) {
    private val repository: RecipeRepository

    val allRecipes: LiveData<List<Recipe>>

    init {
        val dao = HealthyLifeDatabase.getDatabase(application).recipeDao()
        repository = RecipeRepository(dao)
        allRecipes = repository.allRecipes

    }

    fun insertRecipe(recipe: Recipe) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(recipe)
    }

    suspend fun getRecipeById(recipeId: Int): LiveData<Recipe> {
        return repository.getRecipeById(recipeId)
    }


}