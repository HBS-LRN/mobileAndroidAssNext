package com.example.bait2073mobileapplicationdevelopment.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


@Entity(tableName = "EventParticipants")
class EventParticipants (

    @PrimaryKey
    @ColumnInfo(name="id")
    @Expose
    @SerializedName("id")
    val id: Int?,

    @ColumnInfo(name="title")
    @Expose
    @SerializedName("title")
    val title: String?,

    @ColumnInfo(name="details")
    @Expose
    @SerializedName("details")
    val details: String?,

    @ColumnInfo(name = "image")
    @Expose
    @SerializedName("image")
    val image: String?,

    @ColumnInfo(name="date")
    @Expose
    @SerializedName("date")
    val date: String?,

    @ColumnInfo(name="address")
    @Expose
    @SerializedName("address")
    val address: String?,

    @ColumnInfo(name="status")
    @Expose
    @SerializedName("status")
    var status: String?,

    @ColumnInfo(name="user_id")
    @Expose
    @SerializedName("user_id")
    val user_id: Int?

)




