package com.example.bait2073mobileapplicationdevelopment.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bait2073mobileapplicationdevelopment.database.HealthyLifeDatabase
import com.example.bait2073mobileapplicationdevelopment.entities.User
import com.example.bait2073mobileapplicationdevelopment.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: UserRepository

    val allUsers: LiveData<List<User>>



    init {
        val dao = HealthyLifeDatabase.getDatabase(application).userDao()
        repository = UserRepository(dao)
        allUsers = repository.allUsers

    }

    // Define the function to retrieve a specific user


    // Define the function to insert a user
    fun insertUser(user: User) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(user)
    }

    suspend fun getUserById(userId: Int): LiveData<User> {
        return repository.getUserById(userId)
    }


}
