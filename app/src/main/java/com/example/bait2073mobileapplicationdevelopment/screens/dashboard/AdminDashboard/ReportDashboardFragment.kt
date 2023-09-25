package com.example.bait2073mobileapplicationdevelopment.screens.dashboard.AdminDashboard

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
import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentReportDashboardBinding
import com.example.bait2073mobileapplicationdevelopment.entities.User
import com.squareup.picasso.Picasso

/**
 * A simple [Fragment] subclass.
 * Use the [ReportDashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReportDashboardFragment  : Fragment() {


    private lateinit var binding: FragmentReportDashboardBinding
    private  lateinit var viewModel: AdminDashboardViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReportDashboardBinding.inflate(inflater, container, false)
        initUserViewModel()

        val userData = retrieveUserDataFromSharedPreferences(requireContext())
        val userName = userData?.second
        val userId = userData?.first
        binding.nameTV.text = userName

        loadUserData(userId)

        return binding.root
    }

    private fun loadUserData(user_id: Int?) {

        Log.e("loaduser","$user_id")
        viewModel.getLoadUserObservable().observe(viewLifecycleOwner, Observer<User?> {




            if (!it?.image.isNullOrBlank()) {
                Picasso.get().load(it?.image).into(binding.profileTV)

            } else {
                Log.e("noimage", "noimage")
                Picasso.get().load(R.drawable.img_person).into(binding.profileTV)
            }

        })
        viewModel.getUserData(user_id)
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
    private fun initUserViewModel() {
        viewModel = ViewModelProvider(this, AdminDashboardViewModelFactory())
            .get(AdminDashboardViewModel::class.java)

        viewModel.getUserListObserverable().observe(viewLifecycleOwner, Observer<List<User?>> {userListResponse ->
            if(userListResponse == null) {
                Toast.makeText(requireContext(), "no result found...", Toast.LENGTH_LONG).show()
            } else {
                val userList = userListResponse.filterNotNull().toMutableList()
                binding.usercountTV.text = userList.size.toString()
                val reviewedUsersCount = userList.count { it.rating != null }
                binding.reviewcountTV.text = reviewedUsersCount.toString()
            }
        })
        viewModel.getUsers()


        viewModel.getAdminListObserverable().observe(viewLifecycleOwner, Observer<List<User?>> {userListResponse ->
            if(userListResponse == null) {
                Toast.makeText(requireContext(), "no result found...", Toast.LENGTH_LONG).show()
            } else {
                val adminList = userListResponse.filterNotNull().toMutableList()
                binding.admincountTV.text = adminList.size.toString()

            }
        })

        viewModel.getAdmins()

    }
}