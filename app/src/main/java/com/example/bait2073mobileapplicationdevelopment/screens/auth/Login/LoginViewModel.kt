package com.example.bait2073mobileapplicationdevelopment.screens.auth.Login

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.entities.LoginUser
import com.example.bait2073mobileapplicationdevelopment.entities.User
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetUserDataService
import com.example.bait2073mobileapplicationdevelopment.retrofitclient.RetrofitClientInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel  : ViewModel()  {


    var authenticateUserData: MutableLiveData<LoginUser?>
    val networkErrorLiveData = MutableLiveData<Unit>()


    init {
        authenticateUserData = MutableLiveData()
    }

    fun getAuthenticateUserObservable(): MutableLiveData<LoginUser?> {
        return authenticateUserData
    }

    fun authenticate(user: LoginUser) {
        val service = RetrofitClientInstance.retrofitInstance!!.create(GetUserDataService::class.java)
        val call = service.authenticate(user)
        call.enqueue(object : Callback<LoginUser?> {

            override fun onFailure(call: Call<LoginUser?>, t: Throwable) {
                //if user do not have internet network
                networkErrorLiveData.postValue(Unit)
            }

            override fun onResponse(call: Call<LoginUser?>, response: Response<LoginUser?>) {
                if (response.isSuccessful) {
                    val resposne = response.body()
                    Log.i("success", "$resposne")
                    authenticateUserData.postValue(response.body())
                } else {
                    val errorBody = response.errorBody()?.string()
                    val responseCode = response.code()
                    val responseMessage = response.message()
                    Log.e("error", "Response is not successful. Code: $responseCode, Message: $responseMessage, Error Body: $errorBody")
                    authenticateUserData.postValue(null)
                }
            }


        })
    }


}