package com.example.bait2073mobileapplicationdevelopment.repository

import androidx.lifecycle.LiveData
import com.example.bait2073mobileapplicationdevelopment.dao.UserPlanDao
import com.example.bait2073mobileapplicationdevelopment.entities.User
import com.example.bait2073mobileapplicationdevelopment.entities.UserPlan

class UserPlanRepository(private val userPlanDao: UserPlanDao) {

    var allUserPlan : LiveData<List<UserPlan>> = userPlanDao.getAllUserPlans()

    // LiveData to observe user plans by user ID
    fun getUserPlansByUserId(userId: Int): LiveData<List<UserPlan>> {
        return userPlanDao.getUserPlansByUserId(userId)
    }

    // Function to insert a new user plan
    fun insertUserPlan(userPlan: UserPlan) {
        userPlanDao.insertUserPlan(userPlan)
    }

    // Function to delete a user plan
    suspend fun deleteUserPlan(userPlan: UserPlan) {
        userPlanDao.deleteUserPlan(userPlan)
    }

    // Function to update a user plan's name
    suspend fun updateUserPlan(userPlan: UserPlan) {
        userPlanDao.updateUserPlan(userPlan)
    }
    suspend fun getUserPlansByUserId(plan_name: String){
        userPlanDao.getUserPlansByName(plan_name)
    }

    suspend fun clearWorkout() {
        userPlanDao.clearDb()
    }
}
