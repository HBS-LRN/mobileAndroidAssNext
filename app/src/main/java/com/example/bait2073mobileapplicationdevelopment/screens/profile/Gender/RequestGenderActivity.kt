package com.example.bait2073mobileapplicationdevelopment.screens.profile.Gender

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.databinding.ActivityRequestGenderBinding
import com.example.bait2073mobileapplicationdevelopment.entities.UpdateGenderUser
import com.example.bait2073mobileapplicationdevelopment.screens.profile.BMI.RequestBmiActivity

class RequestGenderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRequestGenderBinding
    private lateinit var viewModel: RequestGenderViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRequestGenderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViewModel()
        updateUserObservable()


        binding.maleImage.setOnClickListener {
            // Change the image resource to "gender_male_click"
            binding.maleImage.setImageResource(R.drawable.gender_male_click)

            // Change the text color to blue
            binding.maleText.setTextColor(resources.getColor(android.R.color.holo_blue_dark))

            // Reset female image and text
            binding.femaleImage.setImageResource(R.drawable.gender_female)
            binding.femaleText.setTextColor(resources.getColor(android.R.color.black))
        }

        binding.femaleImage.setOnClickListener {
            // Change the image resource to "gender_female_click"
            binding.femaleImage.setImageResource(R.drawable.gender_female_click)

            // Change the text color to pink (assuming you have defined this color in colors.xml)
            binding.femaleText.setTextColor(resources.getColor(R.color.pink))

            // Reset male image and text
            binding.maleImage.setImageResource(R.drawable.gender_male)
            binding.maleText.setTextColor(resources.getColor(android.R.color.black))
        }

        binding.nextButton.setOnClickListener {
            val userData = retrieveUserDataFromSharedPreferences(this)

            val userId = userData?.first

            Log.e("userId", "$userId")
            var gender = if (binding.maleImage.isClickable) {
                "Male"
            } else {
                "Female"
            }

            val user = UpdateGenderUser(
                userId,
                gender


            )


            viewModel.updateUser(userId ?: 0, user)

        }
    }


    private fun retrieveUserDataFromSharedPreferences(context: Context): Pair<Int, String>? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt(
            "UserId",
            -1
        ) // -1 is a default value if the key is not found
        val userName = sharedPreferences.getString(
            "UserName",
            null
        ) // null is a default value if the key is not found
        if (userId != -1 && userName != null) {
            return Pair(userId, userName)
        }
        return null
    }

    private fun updateUserObservable() {
        viewModel.getUpdateUserObservable().observe(this, Observer<UpdateGenderUser?> {
            if (it == null) {
                Toast.makeText(this, "Please Select A Gender", Toast.LENGTH_SHORT)
            } else {
                val intent = Intent(this, RequestBmiActivity::class.java)
                startActivity(intent)
            }
        })
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this, RequestGenderViewModelFactory())
            .get(RequestGenderViewModel::class.java)


    }
}
