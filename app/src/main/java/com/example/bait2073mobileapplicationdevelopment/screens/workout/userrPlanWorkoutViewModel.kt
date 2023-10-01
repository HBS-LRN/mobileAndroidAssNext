package com.example.bait2073mobileapplicationdevelopment.screens.workout

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bait2073mobileapplicationdevelopment.database.HealthyLifeDatabase
import com.example.bait2073mobileapplicationdevelopment.entities.UserPlan
import com.example.bait2073mobileapplicationdevelopment.entities.UserPlanList
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetUserPLanListService
import com.example.bait2073mobileapplicationdevelopment.repository.UserPlanListRepository
import com.example.bait2073mobileapplicationdevelopment.repository.UserPlanRepository
import com.example.bait2073mobileapplicationdevelopment.retrofitclient.RetrofitClientInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class userrPlanWorkoutViewModel(application: Application) : AndroidViewModel(application){

    private val userPlanListRepository : UserPlanListRepository
    var recyclerListData: MutableLiveData<List<UserPlanList?>> = MutableLiveData()
    val userPlanWorkoutRecyclerListDataDao: LiveData<List<UserPlanList>>

    init {

        val userPlanID = retrieveUserIdFromSharedPreferences(getApplication<Application>().applicationContext)
        val userPlanListDao = HealthyLifeDatabase.getDatabase(application).userPlanListDao()
        userPlanListRepository = UserPlanListRepository(userPlanListDao)
        userPlanWorkoutRecyclerListDataDao = userPlanListRepository.getAllWorkoutsByUserPlanId(userPlanID)
        Log.e("PassingUserPlanIDTest","$userPlanID")
    }
    private fun retrieveUserIdFromSharedPreferences(context: Context): Int {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("UserId", -1)
    }
    fun getLocalDao(){

        Log.e("LossConnect","Dao")
        userPlanWorkoutRecyclerListDataDao.observeForever { newData ->
            val sortedData = newData.sortedBy { it.id }
            recyclerListData.postValue(sortedData)
            Log.e("Error Event API onResponse", "Local Database ${newData}")
        }

    }

    fun getWorkoutListObserverable(): MutableLiveData<List<UserPlanList?>> {
        return recyclerListData
    }


    fun getUserPlanWorkoutList(userPlanID: Int?) {

        val service =
            RetrofitClientInstance.retrofitInstance!!.create(GetUserPLanListService::class.java)
        val call = service.getUserPlanListByUserPlanId(userPlanID!!)
        call.enqueue(object : Callback<List<UserPlanList>> {
            override fun onFailure(call: Call<List<UserPlanList>>, t: Throwable) {
                // Handle API call failure
                Log.e("API Error", t.message ?: "Unknown error")
                getLocalDao()
            }

            override fun onResponse(call: Call<List<UserPlanList>>, response: Response<List<UserPlanList>>) {
                if (response.isSuccessful) {
                    // Response contains a list of User objects
                    val userPlanWorkoutList = response.body()
                    Log.e("gg", "Response not successful, code: ${userPlanWorkoutList}")
                    if (userPlanWorkoutList != null && userPlanWorkoutList.isNotEmpty()) {
                        // Insert the user data into the Room Database
                        recyclerListData.postValue(response.body())
                    } else {
                        // Handle the case where the response is empty
                        Log.e("API Response", "Response body is empty")
                    }
                } else {
                    // Handle the case where the API response is not successful
                    Log.e("API Response", "Response not successful, code: ${response.code()}")
                }
            }
        })
    }





}
