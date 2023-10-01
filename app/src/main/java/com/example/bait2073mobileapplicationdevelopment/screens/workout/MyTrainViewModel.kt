package com.example.bait2073mobileapplicationdevelopment.screens.workout

import GetUserPlanService
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bait2073mobileapplicationdevelopment.database.HealthyLifeDatabase
import com.example.bait2073mobileapplicationdevelopment.entities.Event
import com.example.bait2073mobileapplicationdevelopment.entities.UserPlan
import com.example.bait2073mobileapplicationdevelopment.entities.UserPlanList
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetUserPLanListService
import com.example.bait2073mobileapplicationdevelopment.repository.EventRepository
import com.example.bait2073mobileapplicationdevelopment.repository.UserPlanListRepository
import com.example.bait2073mobileapplicationdevelopment.repository.UserPlanRepository
import com.example.bait2073mobileapplicationdevelopment.retrofitclient.RetrofitClientInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyTrainViewModel(application: Application): AndroidViewModel(application){

    private val userPlanRepository :UserPlanRepository
    private val userPlanListRepository: UserPlanListRepository
    var userPlanRecyclerListData: MutableLiveData<List<UserPlan?>> = MutableLiveData()
    var userPlanListRecyclerListData: MutableLiveData<List<UserPlanList?>> = MutableLiveData()
    var deleteUserPlanList: MutableLiveData<UserPlanList?> = MutableLiveData()
    var deleteUserPlan: MutableLiveData<UserPlan?> = MutableLiveData()
    val userPlanRecyclerListDataDao: LiveData<List<UserPlan>>
    val allUserPlanList : LiveData<List<UserPlanList>>

    lateinit var createNewUserPlanLiveData: MutableLiveData<UserPlan?>
    //    fun getDeleteUserObservable(): MutableLiveData<User?> {
//        return  deleteUserLiveData
//    }
    init {
        val dao = HealthyLifeDatabase.getDatabase(application).userPlanListDao()
        userPlanListRepository = UserPlanListRepository(dao)
        allUserPlanList = userPlanListRepository.allUserPlanList
    }
    fun getUserPlanObserverable(): MutableLiveData<List<UserPlan?>> {
        return userPlanRecyclerListData
    }
    fun insertUserPlanList(userPlanList: UserPlanList )= viewModelScope.launch(Dispatchers.IO){
        userPlanListRepository.insertWorkout(userPlanList)
    }
    init {
        val userID = retrieveUserIdFromSharedPreferences(getApplication<Application>().applicationContext)
        val userPlanDao = HealthyLifeDatabase.getDatabase(application).userPlanDao()
        createNewUserPlanLiveData = MutableLiveData()
        userPlanRepository = UserPlanRepository(userPlanDao)
        userPlanRecyclerListDataDao = userPlanRepository.getUserPlansByUserId(userID)

    }

    private fun retrieveUserIdFromSharedPreferences(context: Context): Int {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("UserId", -1)
    }

    fun getCreateNewUserObservable(): MutableLiveData<UserPlan?> {
        return createNewUserPlanLiveData
    }


    fun getLocalDao(){
        Log.e("LossConnect","Dao")
        userPlanRecyclerListDataDao.observeForever { newData ->
            val sortedData = newData.sortedBy { it.id }
            userPlanRecyclerListData.postValue(sortedData)
            Log.e("Error Event API onResponse", "Local Database ${newData}")
        }
    }

    fun insertEventDataIntoRoomDb(userPlanLists: List<UserPlanList>) {
        viewModelScope.launch {
            this.let {

                try {
                    for (workout in userPlanLists) {
                        Log.d("InsertEventDataIntoRoomDb", "Inserting event with ID: ${workout.id}")
                        insertUserPlanList(
                            UserPlanList(id = workout.id,
                                userPlanId= workout.userPlanId,
                                workoutId = workout.workoutId,
                                name = workout.name,
                                userId= workout.userId,
                                description = workout.description,
                                link = workout.link,
                                gifimage = workout.gifimage,
                                calorie = workout.calorie,
                                bmi_status = workout.bmi_status)
                        )
                    }
                }catch (e: Exception) {
                    Log.e("InsertDataIntoRoomDb", "Error inserting data into Room Database: ${e.message}", e)
                }
            }
        }

    }

    fun getPlan(userId :Int){

        val userPlanService = RetrofitClientInstance.retrofitInstance!!.create(GetUserPlanService::class.java)
        val callUserPlanService = userPlanService.getUserPlan(userId)
        callUserPlanService.enqueue(object : Callback<List<UserPlan>> {
            override fun onFailure(call: Call<List<UserPlan>>, t: Throwable) {
                // Handle API call failure
                getLocalDao()
                Log.e("API Error", t.message ?: "Unknown error")
            }

            override fun onResponse(
                callUserPlan: Call<List<UserPlan>>,
                response: Response<List<UserPlan>>
            ) {
                if (response.isSuccessful) {
                    // Response contains a list of User objects
                    val userPlan = response.body()
                    Log.e("Response not successful", "Response not successful, code: ${userPlan}")
                    if (userPlan != null && userPlan.isNotEmpty()) {
                        // Insert the user data into the Room Database
                        userPlanRecyclerListData.postValue(response.body())
                    } else {
                        // Handle the case where the response is empty
                        Log.e("API Response", "Response body is empty")
                    }
                } else {
//                    // Handle the case where the API response is not successful
//                    Log.e("API Response", "Response not successful, code: ${response.code()}")
                    val resposne = response.body()
                    val errorBody = response.errorBody()?.string()
                    val responseCode = response.code()
                    val responseMessage = response.message()
                    Log.e("getPlanListError", "Response is not successful. Code: $responseCode, Message: $responseMessage, Error Body: $errorBody")

                }
            }



        })
        val userPlanListService = RetrofitClientInstance.retrofitInstance!!.create(GetUserPLanListService::class.java)
        val callUserPlanListService = userPlanListService.getUserPlanList()
        callUserPlanListService.enqueue(object : Callback<List<UserPlanList>> {
            override fun onResponse(
                call: Call<List<UserPlanList>>,
                response: Response<List<UserPlanList>>
            ) {
                if (response.isSuccessful) {
                    // Response contains a list of User objects
                    val workout = response.body()
                    Log.e("Response not successful", "Response not successful, code: ${workout}")
                    if (workout != null && workout.isNotEmpty()) {
                        insertEventDataIntoRoomDb(workout)

                    } else {
                        // Handle the case where the response is empty
                        Log.e("API Response", "Response body is empty")
                    }
                } else {
//                    // Handle the case where the API response is not successful
//                    Log.e("API Response", "Response not successful, code: ${response.code()}")
                    val resposne = response.body()
                    val errorBody = response.errorBody()?.string()
                    val responseCode = response.code()
                    val responseMessage = response.message()
                    Log.e("getPlanListError", "Response is not successful. Code: $responseCode, Message: $responseMessage, Error Body: $errorBody")

                }
            }

            override fun onFailure(call: Call<List<UserPlanList>>, t: Throwable) {
                Log.e("API Error", t.message ?: "Unknown error")
            }


        })

    }

    fun deleteUserPlan(userPlanId: Int?) {
        if (userPlanId != null) {
            // Directly delete the user plan
            val service = RetrofitClientInstance.retrofitInstance!!.create(GetUserPlanService::class.java)
            val call = service.deleteUserPlanByPlanID(userPlanId)
            call.enqueue(object : Callback<UserPlan?> {

                override fun onFailure(call: Call<UserPlan?>, t: Throwable) {
                    Log.e("API Error", t.message ?: "Unknown error")
                    deleteUserPlan.postValue(null)
                }

                override fun onResponse(call: Call<UserPlan?>, response: Response<UserPlan?>) {
                    if (response.isSuccessful) {
                        Log.e("API Response", "Response body is success")
                        deleteUserPlan.postValue(response.body())

                    } else {
                        Log.e("API Response", "Response body is fail")
                        deleteUserPlan.postValue(null)
                    }
                }
            })
        }
    }


    fun createUserPlan(userPlan: UserPlan) {

        Log.e("userplanarray","$userPlan")
        val service = RetrofitClientInstance.retrofitInstance!!.create(GetUserPlanService::class.java)
        val call = service.createUserPlan(userPlan)
        call.enqueue(object : Callback<UserPlan?> {

            override fun onFailure(call: Call<UserPlan?>, t: Throwable) {
                Log.e("API Error", t.message ?: "Unknown error")
                createNewUserPlanLiveData.postValue(null)
            }

            override fun onResponse(call: Call<UserPlan?>, response: Response<UserPlan?>) {
                if (response.isSuccessful) {
                    Log.e("API Response", "Response body is empty")
                    createNewUserPlanLiveData.postValue(response.body())
                } else {
//                    Log.e("API Response", "Response body is empty")
//                    createNewUserPlanLiveData.postValue(null)
                    // Handle the case where the API response is not successful
//                    Log.e("API Response", "Response not successful, code: ${response.code()}")
                    val resposne = response.body()
                    val errorBody = response.errorBody()?.string()
                    val responseCode = response.code()
                    val responseMessage = response.message()
                    Log.e("haha", "Response is not successful. Code: $responseCode, Message: $responseMessage, Error Body: $errorBody")
                }
            }
        })
    }

    fun editUserPlanName(userPlan: UserPlan) {
        val service = RetrofitClientInstance.retrofitInstance!!.create(GetUserPlanService::class.java)

        // Create an updated UserPlanList object with the new user plan name


        val call = service.updateUserPlanByUserPlanId(userPlan.id!!, userPlan)
        Log.e("EditUserPlanName","${userPlan.id}")
        call.enqueue(object : Callback<UserPlan?> {

            override fun onFailure(call: Call<UserPlan?>, t: Throwable) {
                Log.e("API Error", t.message ?: "Unknown error")
                Log.e("EditUserPlan","${userPlan.plan_name}")
                // Handle the error as needed, e.g., show an error message
            }

            override fun onResponse(call: Call<UserPlan?>, response: Response<UserPlan?>) {
                if (response.isSuccessful) {
                    Log.e("EditUserPlanName","${userPlan.plan_name}")
                    Log.e("API Response", "Response body is secussful")
                    val updatedPlan = response.body()
                    if (updatedPlan != null) {
                        Log.e("API Response", "Response body is secussful2")
                        deleteUserPlan.postValue(response.body())
                    } else {
//                    createNewUserPlanLiveData.postValue(null)
                        // Handle the case where the API response is not successful
//                    Log.e("API Response", "Response not successful, code: ${response.code()}")
                        val resposne = response.body()
                        val errorBody = response.errorBody()?.string()
                        val responseCode = response.code()
                        val responseMessage = response.message()
                        Log.e("haha", "Response is not successful. Code: $responseCode, Message: $responseMessage, Error Body: $errorBody")
                    }
                } else {
                    Log.e("API Response", "Response body is fail")
                    val resposne = response.body()
                    val errorBody = response.errorBody()?.string()
                    val responseCode = response.code()
                    val responseMessage = response.message()
                    Log.e("haha", "Response is not successful. Code: $responseCode, Message: $responseMessage, Error Body: $errorBody")
                }
            }
        })
    }




}