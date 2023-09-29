package com.example.bait2073mobileapplicationdevelopment.screens.workout

import GetUserPlanService
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bait2073mobileapplicationdevelopment.entities.UserPlan
import com.example.bait2073mobileapplicationdevelopment.entities.UserPlanList
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetUserPLanListService
import com.example.bait2073mobileapplicationdevelopment.retrofitclient.RetrofitClientInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyTrainViewModel: ViewModel() {
    var userPlanRecyclerListData: MutableLiveData<List<UserPlan?>> = MutableLiveData()
    var userPlanListRecyclerListData: MutableLiveData<List<UserPlanList?>> = MutableLiveData()
    var deleteUserPlanList: MutableLiveData<UserPlanList?> = MutableLiveData()
    var deleteUserPlan: MutableLiveData<UserPlan?> = MutableLiveData()
    lateinit var createNewUserPlanLiveData: MutableLiveData<UserPlan?>
    //    fun getDeleteUserObservable(): MutableLiveData<User?> {
//        return  deleteUserLiveData
//    }
    fun getUserPlanObserverable(): MutableLiveData<List<UserPlan?>> {
        return userPlanRecyclerListData
    }







    init {
        createNewUserPlanLiveData = MutableLiveData()


    }

    fun getCreateNewUserObservable(): MutableLiveData<UserPlan?> {
        return createNewUserPlanLiveData
    }
    fun getUserPlanListObserverable(): MutableLiveData<List<UserPlanList?>> {
        return userPlanListRecyclerListData
    }
    fun getPlanList(userPlanId :Int){

        val userPlanWorkoutService = RetrofitClientInstance.retrofitInstance!!.create(
            GetUserPLanListService::class.java)
        val callUserPlanWorkoutService = userPlanWorkoutService.getUserPlanListByUserPlanId(userPlanId)
        callUserPlanWorkoutService.enqueue(object : Callback<List<UserPlanList>> {
            override fun onFailure(call: Call<List<UserPlanList>>, t: Throwable) {
                // Handle API call failure
                Log.e("API Error", t.message ?: "Unknown error")
            }

            override fun onResponse(
                call: Call<List<UserPlanList>>,
                response: Response<List<UserPlanList>>
            ) {
                if (response.isSuccessful) {
                    // Response contains a list of User objects
                    val userPlanList = response.body()
                    Log.e("Response not successful", "Response not successful, code: ${userPlanList}")
                    if (userPlanList != null && userPlanList.isNotEmpty()) {
                        // Insert the user data into the Room Database
                        userPlanListRecyclerListData.postValue(response.body())
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

    fun getPlan(userId :Int){

        val userPlanService = RetrofitClientInstance.retrofitInstance!!.create(GetUserPlanService::class.java)
        val callUserPlanService = userPlanService.getUserPlan(userId)
        callUserPlanService.enqueue(object : Callback<List<UserPlan>> {
            override fun onFailure(call: Call<List<UserPlan>>, t: Throwable) {
                // Handle API call failure
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
                    Log.e("haha", "Response is not successful. Code: $responseCode, Message: $responseMessage, Error Body: $errorBody")

                }
            }



        })

    }

    fun deleteUserWorkoutPlan(userPlanId: Int, callback: (Boolean) -> Unit) {
        val service = RetrofitClientInstance.retrofitInstance!!.create(GetUserPLanListService::class.java)
        val call = service.deleteUserPlanListByUserPlanId(userPlanId)
        call.enqueue(object : Callback<UserPlanList?> {

            override fun onFailure(call: Call<UserPlanList?>, t: Throwable) {
                Log.e("API Error", t.message ?: "Unknown error")
                callback(false) // Notify the callback that the operation failed
            }

            override fun onResponse(call: Call<UserPlanList?>, response: Response<UserPlanList?>) {
                if (response.isSuccessful) {
                    Log.e("API Response", "Response body is workSuccess")
                    callback(true) // Notify the callback that the operation was successful
                } else {
                    Log.e("API Response", "Response body is WorkFail")
                    callback(false) // Notify the callback that the operation failed
                }
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

    private fun deletePlanIfNoUserPlanList(userId: Int, userPlanId: Int) {
        // Check if there are no user plan list items with the same userPlan_id
        // If there are none, delete the user plan directly
        val service = RetrofitClientInstance.retrofitInstance!!.create(GetUserPLanListService::class.java)
        val call = service.checkUserPlanListEmpty(userPlanId) // Replace with the actual API call to check if the list is empty

        call.enqueue(object : Callback<Boolean?> {

            override fun onFailure(call: Call<Boolean?>, t: Throwable) {
                Log.e("API Error", t.message ?: "Unknown error")
                deleteUserPlan.postValue(null)
            }

            override fun onResponse(call: Call<Boolean?>, response: Response<Boolean?>) {
                if (response.isSuccessful && response.body() == true) {
                    // If the user plan list is empty, delete the user plan
                    val planService = RetrofitClientInstance.retrofitInstance!!.create(GetUserPlanService::class.java)
                    val planCall = planService.deleteUserPlanByUserId(userId)
                    planCall.enqueue(object : Callback<UserPlan?> {

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
                } else {
                    // Handle the case where the user plan list is not empty
                    Log.e("UserPlan Deletion", "UserPlanList is not empty, cannot delete UserPlan")
                }
            }
        })


    }


    fun deleteUserWorkOut(workOutId: Int) {
        val service = RetrofitClientInstance.retrofitInstance!!.create(GetUserPLanListService::class.java)
        val call = service.deleteUserPlanListById(workOutId)
        call.enqueue(object : Callback<UserPlanList?> {

            override fun onFailure(call: Call<UserPlanList?>, t: Throwable) {
                Log.e("API Error", t.message ?: "Unknown error")
                deleteUserPlanList.postValue(null)
            }

            override fun onResponse(call: Call<UserPlanList?>, response: Response<UserPlanList?>) {
                if(response.isSuccessful) {
                    Log.e("API Response", "Response body is empty")
                    deleteUserPlanList.postValue(response.body())
                } else {
                    Log.e("API Response", "Response body is empty")
                    deleteUserPlanList.postValue(null)
                }
            }
        })
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