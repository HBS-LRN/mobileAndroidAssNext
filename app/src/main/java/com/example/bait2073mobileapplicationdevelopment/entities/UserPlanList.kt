package com.example.bait2073mobileapplicationdevelopment.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "User_Plan_List",
    foreignKeys = [
        ForeignKey(
            entity = UserPlan::class,
            parentColumns = ["id"],
            childColumns = ["user_plan_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Workout::class,
            parentColumns = ["id"],
            childColumns = ["workout_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class UserPlanList(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @Expose
    @SerializedName("id")
    val id: Int? = null,

    @ColumnInfo(name = "user_plan_id")
    @Expose
    @SerializedName("user_plan_id")
    val userPlanId: Int,

    @ColumnInfo(name = "workout_id")
    @Expose
    @SerializedName("workout_id")
    val workoutId: Int,

    @ColumnInfo(name = "name")
    @Expose
    @SerializedName("name")
    val name: String,

    @ColumnInfo(name = "gifimage")
    @Expose
    @SerializedName("gifimage")
    val gifimage: String?
)