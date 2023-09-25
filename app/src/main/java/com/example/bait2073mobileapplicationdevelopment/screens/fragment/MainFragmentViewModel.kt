package com.example.bait2073mobileapplicationdevelopment.screens.fragment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bait2073mobileapplicationdevelopment.entities.User
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetUserDataService
import com.example.bait2073mobileapplicationdevelopment.retrofitclient.RetrofitClientInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainFragmentViewModel : ViewModel() {


    lateinit var loadUserData: MutableLiveData<User?>


    init {
        loadUserData = MutableLiveData()

    }






    fun getLoadUserObservable(): MutableLiveData<User?> {
        return loadUserData
    }








    fun getUserData(user_id: Int?) {
        val service = RetrofitClientInstance.retrofitInstance!!.create(GetUserDataService::class.java)
        val call = service.getUser(user_id!!)
        call.enqueue(object : Callback<User?> {

            override fun onFailure(call: Call<User?>, t: Throwable) {
                Log.e("haha", "wandan")
                loadUserData.postValue(null)
            }

            override fun onResponse(call: Call<User?>, response: Response<User?>) {
                if (response.isSuccessful) {
                    val resposne = response.body()
                    Log.i("haha", "$resposne")
                    loadUserData.postValue(response.body())
                } else {
                    Log.i("haha", "ggla")
                    loadUserData.postValue(null)
                }
            }


        })
    }

}