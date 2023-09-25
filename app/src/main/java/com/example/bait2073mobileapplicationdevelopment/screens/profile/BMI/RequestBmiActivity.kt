package com.example.bait2073mobileapplicationdevelopment.screens.profile.BMI


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.bait2073mobileapplicationdevelopment.databinding.AcitvityRequestBmiBinding
import com.example.bait2073mobileapplicationdevelopment.entities.UpdateBmiUser
import com.example.bait2073mobileapplicationdevelopment.entities.UpdateGenderUser
import com.example.bait2073mobileapplicationdevelopment.screens.fragment.MainFragment
import com.example.bait2073mobileapplicationdevelopment.screens.profile.Gender.RequestGenderViewModel


class RequestBmiActivity : AppCompatActivity(){

    private lateinit var binding: AcitvityRequestBmiBinding

    private lateinit var viewModel: RequestBmiActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = AcitvityRequestBmiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViewModel()
        updateUserObservable()




        binding.weightPicker.minValue = 30
        binding.weightPicker.maxValue = 150

        binding.heightPicker.minValue = 100
        binding.heightPicker.maxValue = 250

        binding.heightPicker.value = 170

        binding.weightPicker.value = 55

        // Set click listener for the back arrow ImageView
        binding.imgBackArrow.setOnClickListener {
           onBackPressed()
        }
        binding.nextButton.setOnClickListener {

            val userData = retrieveUserDataFromSharedPreferences(this)

            val userId = userData?.first

            Log.e("userId", "$userId")



            val user = UpdateBmiUser(
                userId,
                binding.weightPicker.value.toDouble(),
                binding.heightPicker.value.toDouble()


            )


            viewModel.updateUser(userId ?: 0, user)

        }



        binding.weightPicker.setOnValueChangedListener{ _,_,_ ->
            calculateBMI()
        }

        binding.heightPicker.setOnValueChangedListener{ _,_,_ ->
            calculateBMI()
        }

    }

    private fun calculateBMI()
    {
        val height = binding.heightPicker.value
        val doubleHeight = height.toDouble() / 100

        val weight = binding.weightPicker.value

        val bmi = weight.toDouble() / (doubleHeight * doubleHeight)

//        binding.resultsTV.text = String.format("Your BMI is: %.2f", bmi)
//        binding.healthyTV.text = String.format("Considered: %s", healthyMessage(bmi))

    }

    private fun healthyMessage(bmi: Double): String
    {
        if (bmi < 18.5)
            return "Underweight"
        if(bmi < 25.0)
            return "Healthy"
        if (bmi < 30.0)
            return "Overweight"

        return "Obese"
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
        viewModel.getUpdateUserObservable().observe(this, Observer<UpdateBmiUser?> {
            if (it == null) {
                Toast.makeText(this, "Please Select A Gender", Toast.LENGTH_SHORT)
            } else {
                val intent = Intent(this, MainFragment::class.java)
                startActivity(intent)
            }
        })
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this, RequestBmiActivityViewModelFactory())
            .get(RequestBmiActivityViewModel::class.java)


    }

}