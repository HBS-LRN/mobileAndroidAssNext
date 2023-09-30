package com.example.bait2073mobileapplicationdevelopment.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "DiseaseSymptom_Room")

data class DiseaseSymptom_Room (
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

    @ColumnInfo(name = "symptom_id")
    @Expose
    @SerializedName("symptom_id")
    val symptom_id: Int,

    @ColumnInfo(name = "symptom_name")
    @Expose
    @SerializedName("symptom_name")
    val symptom_name: String,

    @ColumnInfo(name = "symptom_image")
    @Expose
    @SerializedName("symptom_image")
    val symptom_image: String?,

    @ColumnInfo(name = "symptom_description")
    @Expose
    @SerializedName("symptom_description")
    val symptom_description: String?,
)