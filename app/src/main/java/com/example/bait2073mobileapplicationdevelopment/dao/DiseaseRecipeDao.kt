package com.example.bait2073mobileapplicationdevelopment.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import com.example.bait2073mobileapplicationdevelopment.entities.Disease_Recipe

@Dao
interface DiseaseRecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDiseaseRecipe(diseaseRecipe: Disease_Recipe)

    @Query("SELECT * from disease_recipe WHERE id = :diseaseRecipeId")
    fun getDiseaseRecipeById(diseaseRecipeId: Int): LiveData<Disease_Recipe>


    @Query("SELECT * FROM disease_recipe")
    fun getAllDiseasesRecipe(): LiveData<List<Disease_Recipe>>

    @Query("DELETE FROM disease_recipe")
    fun deleteDiseaseRecipe()

    @Query("DELETE FROM disease_recipe WHERE disease_id =:diseaseId")
    fun deleteOneDiseaseRecipe(diseaseId: Int)

    @Query("DELETE FROM disease_recipe WHERE recipe_id =:recipeId")
    fun deleteOneRecipeDisease(recipeId: Int) //if want return type(know the number row delete) put :Int
}