package com.example.bait2073mobileapplicationdevelopment.interfaces

import com.example.bait2073mobileapplicationdevelopment.entities.Recipe
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface GetRecipeDataService {
    @GET("recipes")
    fun getRecipeList(): Call<List<Recipe>>

    @GET("recipes/{recipe_id}")
    @Headers("Accept:application/json","Content-Type:application/json")
    fun getRecipe(@Path("recipe_id") recipe_id: Int?): Call<Recipe>

    @POST("recipes")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun createRecipe(@Body params: Recipe): Call<Recipe>

    @PATCH("recipes/{recipe_id}")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun updateRecipe(@Path("recipe_id") recipe_id: Int, @Body params: Recipe): Call<Recipe>

    @DELETE("recipes/{recipe_id}")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun deleteRecipe(@Path("recipe_id") recipe_id: Int): Call<Recipe>
}