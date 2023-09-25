package com.example.bait2073mobileapplicationdevelopment.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "Disease_Symptom")

data class Disease_Symptom (
    @PrimaryKey
    @ColumnInfo(name = "id")
    @Expose
    @SerializedName("id")
    val id: Int?,

    @ColumnInfo(name = "disease_id")
    @Expose
    @SerializedName("disease_id")
    val disease_id: Int,

    @ColumnInfo(name = "symptom_id")
    @Expose
    @SerializedName("symptom_id")
    val symptom_id: Int,
)