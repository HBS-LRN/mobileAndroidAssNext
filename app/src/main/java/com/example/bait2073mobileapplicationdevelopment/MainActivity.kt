package com.example.bait2073mobileapplicationdevelopment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.Pair
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.bait2073mobileapplicationdevelopment.screens.auth.Login.LoginActivity
import com.example.bait2073mobileapplicationdevelopment.database.HealthyLifeDatabase
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetUserDataService
import com.example.bait2073mobileapplicationdevelopment.entities.User
import com.example.bait2073mobileapplicationdevelopment.viewmodel.UserViewModel
import com.example.bait2073mobileapplicationdevelopment.retrofitclient.RetrofitClientInstance
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : BaseActivity() {

    private val SPLASH_SCREEN = 0L // Change SPLASH_SCREEN type to Long
    private lateinit var image: ImageView
    private lateinit var logo: TextView
    lateinit var viewModel: UserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(UserViewModel::class.java)

//        readStorageTask()
        // Initialize your views using findViewById
        image = findViewById(R.id.logo_image)
        logo = findViewById(R.id.logo_text)

        val topAnim: Animation = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        val bottomAnim: Animation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)

        // Set animations to views
        image.startAnimation(topAnim)
        logo.startAnimation(bottomAnim)

        // Delay the transition to the next screen
        Handler().postDelayed({
            val intent = Intent(this@MainActivity, LoginActivity::class.java)

            val pairs = arrayOf<Pair<View, String>>(
                Pair(image, "logo_image"),
                Pair(logo, "logo_text")
            )

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                val options = android.app.ActivityOptions.makeSceneTransitionAnimation(
                    this@MainActivity,
                    *pairs
                )
                startActivity(intent, options.toBundle())
            } else {
                startActivity(intent)
            }
            finish()
        }, SPLASH_SCREEN)
    }

    fun getUsers() {
        val service = RetrofitClientInstance.retrofitInstance!!.create(GetUserDataService::class.java)
        val call = service.getUserList()
        call.enqueue(object : Callback<List<User>> {
            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                // Handle API call failure
                Log.e("API Error", t.message ?: "Unknown error")
            }

            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    // Response contains a list of User objects
                    val userList = response.body()
                    Log.e("gg", "Response not successful, code: ${userList}")
                    if (userList != null && userList.isNotEmpty()) {
                        // Insert the user data into the Room Database
//                        insertDataIntoRoomDb(userList)
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

    fun insertDataIntoRoomDb(users: List<User>) {

        launch {
            this.let {

                try {
                    for (user in users) {
                        Log.d("InsertDataIntoRoomDb", "Inserting user with ID: ${user.id}")
                      viewModel.insertUser(
                            User(
                                id = user.id,
                                name = user.name,
                                email = user.email,
                                gender = user.gender,
                                image = user.image ?: "",
                                phone = user.phone ?: "",
                                birthdate = user.birthdate,
                                weight = user.weight,
                                height = user.height,
                                rating = user.rating
                            )
                        )
                    }
                }catch (e: Exception) {
                        Log.e("InsertDataIntoRoomDb", "Error inserting data into Room Database: ${e.message}", e)
                    }
            }
        }

    }

    fun clearDataBase() {
        launch {
            this.let {
                HealthyLifeDatabase.getDatabase(this@MainActivity).userDao().clearDb()
            }
        }
    }

    private fun readStorageTask() {

//        clearDataBase()
        getUsers()
    }

}
