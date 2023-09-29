package com.example.bait2073mobileapplicationdevelopment.screens.auth.SignUp

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bait2073mobileapplicationdevelopment.entities.RegisterUser
import com.example.bait2073mobileapplicationdevelopment.entities.User
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetUserDataService
import com.example.bait2073mobileapplicationdevelopment.retrofitclient.RetrofitClientInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpViewModel : ViewModel() {

    lateinit var createNewUserLiveData: MutableLiveData<RegisterUser?>




    init {
        createNewUserLiveData = MutableLiveData()
    }

    fun getCreateNewUserObservable(): MutableLiveData<RegisterUser?> {
        return createNewUserLiveData
    }



    fun createUser(user: RegisterUser) {
        val service = RetrofitClientInstance.retrofitInstance!!.create(GetUserDataService::class.java)
        val call = service.registerUser(user)
        call.enqueue(object : Callback<RegisterUser?> {
            override fun onFailure(call: Call<RegisterUser?>, t: Throwable) {
                Log.e("haha", "wandan")
                createNewUserLiveData.postValue(null)
            }

            override fun onResponse(call: Call<RegisterUser?>, response: Response<RegisterUser?>) {
                if (response.isSuccessful) {
                    val resposne = response.body()
                    Log.i("haha", "$resposne")
                    createNewUserLiveData.postValue(response.body())
                } else {
                    val resposne = response.body()
                    val errorBody = response.errorBody()?.string()
                    val responseCode = response.code()
                    val responseMessage = response.message()
                    Log.e("haha", "Response is not successful. Code: $responseCode, Message: $responseMessage, Error Body: $errorBody")
                    createNewUserLiveData.postValue(null)
                }
            }
        })
    }
}
