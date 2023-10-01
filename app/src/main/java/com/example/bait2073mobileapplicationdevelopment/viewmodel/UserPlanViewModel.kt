package com.example.bait2073mobileapplicationdevelopment.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.bait2073mobileapplicationdevelopment.database.HealthyLifeDatabase
import com.example.bait2073mobileapplicationdevelopment.entities.UserPlan
import com.example.bait2073mobileapplicationdevelopment.repository.UserPlanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class UserPlanViewModel(application: Application, private val repository: UserPlanRepository) : AndroidViewModel(application) {
//    var userPlanRecyclerListData: MutableLiveData<List<UserPlan?>> = MutableLiveData()
//    var userPlanListRecyclerListData: MutableLiveData<List<UserPlanList?>> = MutableLiveData()
//    var deleteUserPlanList: MutableLiveData<UserPlanList?> = MutableLiveData()
//    var deleteUserPlan: MutableLiveData<UserPlan?> = MutableLiveData()

    private val userPlanRepository: UserPlanRepository


    val allUserPlans: LiveData<List<UserPlan>>
    init {
        val UserPlandao = HealthyLifeDatabase.getDatabase(application).userPlanDao()
        userPlanRepository = UserPlanRepository(UserPlandao)
        allUserPlans = userPlanRepository.allUserPlan
    }

    fun getUserPlansByUserId(userId: Int): LiveData<List<UserPlan>> {
        return userPlanRepository.getUserPlansByUserId(userId)
    }

    // Function to insert a new user plan
     fun insertUserPlan(userPlan: UserPlan) {
        userPlanRepository.insertUserPlan(userPlan)
    }

    // Function to delete a user plan
    suspend fun deleteUserPlan(userPlan: UserPlan) {
        userPlanRepository.deleteUserPlan(userPlan)
    }

    // Function to update a user plan's name
    suspend fun updateUserPlan(userPlan: UserPlan) {
        userPlanRepository.updateUserPlan(userPlan)
    }
    fun clearWorkout() = viewModelScope.launch(Dispatchers.IO) {
        userPlanRepository.clearWorkout()
    }




}