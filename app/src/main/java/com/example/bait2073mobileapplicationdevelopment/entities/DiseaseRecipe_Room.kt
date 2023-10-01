package com.example.bait2073mobileapplicationdevelopment.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "DiseaseRecipe_Room")

data class DiseaseRecipe_Room (
    @PrimaryKey
    @ColumnInfo(name = "id")
    @Expose
    @SerializedName("id")
    val id: Int?,

    @ColumnInfo(name = "disease_id")
    @Expose
    @SerializedName("disease_id")
    val disease_id: Int,

    @ColumnInfo(name = "disease_name")
    @Expose
    @SerializedName("disease_name")
    val disease_name: String,

    @ColumnInfo(name = "recipe_id")
    @Expose
    @SerializedName("recipe_id")
    val recipe_id: Int,

    @ColumnInfo(name = "recipe_name")
    @Expose
    @SerializedName("recipe_name")
    val recipe_name: String,

    @ColumnInfo(name = "recipe_image")
    @Expose
    @SerializedName("recipe_image")
    val recipe_image: String?,

    @ColumnInfo(name = "recipe_description")
    @Expose
    @SerializedName("recipe_description")
    val recipe_description: String?,

    @ColumnInfo(name = "recipe_servings")
    @Expose
    @SerializedName("recipe_servings")
    val recipe_servings: Int?,
)