package com.example.bait2073mobileapplicationdevelopment.screens.dashboard.UserDashboard

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentDashboardBinding
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentHomeBinding
import com.example.bait2073mobileapplicationdevelopment.entities.User
import com.example.bait2073mobileapplicationdevelopment.screens.admin.UserList.UserListFragmentDirections
import com.example.bait2073mobileapplicationdevelopment.screens.dashboard.AdminDashboard.AdminDashboardViewModel
import com.example.bait2073mobileapplicationdevelopment.viewmodel.StartWorkOutViewModel
import com.example.bait2073mobileapplicationdevelopment.viewmodel.UserViewModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class DashboardFragment : Fragment() {


    private lateinit var binding: FragmentDashboardBinding
    lateinit var viewModelStartWorkout: StartWorkOutViewModel
    private lateinit var viewModel: DashboardViewModel
    private lateinit var viewModelUser: UserViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        initUserViewModel()


        val userData = retrieveUserDataFromSharedPreferences(requireContext())
        val userName = userData?.second
        val userId = userData?.first
        binding.nameTextView.text = userName

        loadUserData(userId)


        //intialize view modelval userData = retrieveUserDataFromSharedPreferences(requireContext())
        //        val userName = userData?.second
        //        val userId = userData?.first
        viewModelUser = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(UserViewModel::class.java)



        viewModelStartWorkout = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(StartWorkOutViewModel::class.java)


        viewModelStartWorkout.allStartWorkout.observe(viewLifecycleOwner) { list ->
            list?.let {


                var totalCalorie = 0.0 // Initialize totalCalorie to zero

                for (workout in list) {
                    if (workout.userId == userId) {
                        workout.calorie?.let {
                            totalCalorie += it // Add the calorie value of each workout to totalCalorie
                        }
                    }
                }

//
//                for (workout in list) {
//                    totalCalorie += workout.calorie ?: 0.0 // Add the calorie value of each workout to totalCalorie
//                }

                val format = DecimalFormat("###.0")
                Log.e("calorie", "$totalCalorie")
                if (totalCalorie == 0.0) {
                    binding.totalCalorieSpent.text = "0.0 Kcal"
                } else {
                    val formattedCalorie = format.format(totalCalorie)
                    // Now, you can display the totalCalorie in your UI
                    binding.totalCalorieSpent.text = "$formattedCalorie Kcal"
                }
            }
        }
        navigate()




        return binding.root
    }


    private fun initUserViewModel() {
        viewModel = ViewModelProvider(this, DashboardViewModelFactory())
            .get(DashboardViewModel::class.java)

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


    private fun loadUserData(user_id: Int?) {

        Log.e("loaduser", "$user_id")
        viewModel.getLoadUserObservable().observe(viewLifecycleOwner, Observer<User?> {
            if (it != null) {

                insertDataIntoRoomDb(it)


                if (!it.image.isNullOrBlank()) {
                    Picasso.get().load(it.image).into(binding.profileImg)

                } else {
                    Log.e("noimage", "noimage")
                    Picasso.get().load(R.drawable.img_person).into(binding.profileImg)
                }
            }

        })
        viewModel.getUserData(user_id)
    }


    fun insertDataIntoRoomDb(user: User) {


        try {

            Log.d("InsertDataIntoRoomDb", "Inserting user with ID: ${user.id}")
            viewModelUser.insertUser(
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

        } catch (e: Exception) {
            Log.e(
                "InsertDataIntoRoomDb",
                "Error inserting data into Room Database: ${e.message}",
                e
            )
        }
    }


    private fun navigate() {
        binding.homeFragmentCardView.setOnClickListener {


            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.dashboardFragment, true)
                .build()
            findNavController().navigate(
                R.id.action_dashboardFragment_to_homeFragment,
                null,
                navOptions
            )
        }

        binding.passwordFragmentCardView.setOnClickListener {

            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.dashboardFragment, true)
                .build()
            findNavController().navigate(
                R.id.action_dashboardFragment_to_changePasswordFragment,
                null,
                navOptions
            )
        }
        binding.profileFragmentCardView.setOnClickListener {

            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.dashboardFragment, true)
                .build()
            findNavController().navigate(
                R.id.action_dashboardFragment_to_profileFragment,
                null,
                navOptions
            )
        }
        binding.workoutFragmentCardView.setOnClickListener {

            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.dashboardFragment, true)
                .build()
            findNavController().navigate(
                R.id.action_dashboardFragment_to_myTrainList,
                null,
                navOptions
            )
        }

        binding.signOutFragmentCardView.setOnClickListener {

            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.dashboardFragment, true)
                .build()
            findNavController().navigate(
                R.id.action_dashboardFragment_to_logoutFragment,
                null,
                navOptions
            )
        }

        binding.eventFragmentCardView.setOnClickListener {

            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.dashboardFragment, true)
                .build()
            findNavController().navigate(
                R.id.action_dashboardFragment_to_eventFragment2,
                null,
                navOptions
            )
        }


    }
}



