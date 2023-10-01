package com.example.bait2073mobileapplicationdevelopment.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.bait2073mobileapplicationdevelopment.entities.UserPlan

@Dao
interface UserPlanDao {
    @Query("SELECT * FROM user_plan")
    fun getAllUserPlans(): LiveData<List<UserPlan>>
    @Query("SELECT * FROM user_plan WHERE user_id = :userId")
    fun getUserPlansByUserId(userId: Int): LiveData<List<UserPlan>>
    @Query("SELECT * FROM user_plan WHERE plan_name = :plan_name")
    fun getUserPlansByName(plan_name: String): LiveData<List<UserPlan>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserPlan(userPlan: UserPlan)

    @Delete
    fun deleteUserPlan(userplan:UserPlan)

    @Query("DELETE FROM user_plan")
    fun clearDb()

    @Update
    fun updateUserPlan(userplan:UserPlan)

}