package com.example.bait2073mobileapplicationdevelopment.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "DiseaseHospital_Room")
data class DiseaseHospital_Room (
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

    @ColumnInfo(name = "hospital_id")
    @Expose
    @SerializedName("hospital_id")
    val hospital_id: Int,

    @ColumnInfo(name = "hospital_name")
    @Expose
    @SerializedName("hospital_name")
    val hospital_name: String,

    @ColumnInfo(name = "hospital_contact")
    @Expose
    @SerializedName("hospital_contact")
    val hospital_contact: String?,

    @ColumnInfo(name = "hospital_address")
    @Expose
    @SerializedName("hospital_address")
    val hospital_address: String?,
)