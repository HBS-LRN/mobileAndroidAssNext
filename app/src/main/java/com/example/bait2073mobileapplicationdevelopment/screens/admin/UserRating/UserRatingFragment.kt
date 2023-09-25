package com.example.bait2073mobileapplicationdevelopment.screens.admin.UserList

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.bait2073mobileapplicationdevelopment.adapter.UserAdapter
import com.example.bait2073mobileapplicationdevelopment.entities.User
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.adapter.UserRatingAdapter
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentRatingListBinding
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentUserListBinding
import com.example.bait2073mobileapplicationdevelopment.screens.admin.UserRating.UserRatingViewModelFactory


class UserRatingFragment : Fragment(), UserRatingAdapter.UserClickListener{

    lateinit var recyclerViewAdapter: UserRatingAdapter
    lateinit var viewModel: UserRatingViewModel
    private lateinit var binding:FragmentRatingListBinding
    lateinit var users: List<User>
    private lateinit var dialog: Dialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRatingListBinding.inflate(inflater, container, false)
        initViewModel()
        initRecyclerView()

        searchUser()




        return binding.root
    }


    private fun searchUser() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false;
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                if (newText != null) {
                    recyclerViewAdapter.filerList(newText)
                }
                return true

            }
        })
    }
    private fun initRecyclerView() {
        binding.recycleView.setHasFixedSize(true)
        binding.recycleView.layoutManager = StaggeredGridLayoutManager(1, LinearLayout.VERTICAL)
        recyclerViewAdapter = UserRatingAdapter(requireContext(),this)
        binding.recycleView.adapter = recyclerViewAdapter

    }

    fun initViewModel() {
//        viewModel = ViewModelProvider(this,
//            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)).get(
//            UserRatingViewModel::class.java)



        viewModel = ViewModelProvider(
            this,
            UserRatingViewModelFactory()
        ).get(UserRatingViewModel::class.java)


        viewModel.getUserListObserverable().observe(viewLifecycleOwner, Observer<List<User?>> {userListResponse ->
            if(userListResponse == null) {
                Toast.makeText(requireContext(), "no result found...", Toast.LENGTH_LONG).show()
            } else {
//                recyclerViewAdapter.updateList(it.toList().get(1))
                val userList = userListResponse
                    .filterNotNull()
                    .filter { it.rating != null } // Filter out users with null ratings
                    .toMutableList()
                Log.i("haha", "$userList")
                recyclerViewAdapter.updateList(userList)
                recyclerViewAdapter.notifyDataSetChanged()
                val averageRating = calculateAverageRating(userList)
                binding.ratingTV.text = String.format("%.1f/5.0", averageRating)

            }
        })
        viewModel.getUsers()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(requestCode == 1000) {
            viewModel.getUsers()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onItemClicked(user: User) {

    }

    override fun onLongItemClicked(user: User, cardView: CardView) {

    }
    private fun calculateAverageRating(users: List<User>): Float {
        val ratedUsers = users.filter { it.rating != null }
        if (ratedUsers.isNotEmpty()) {
            var totalRating = 0.0f
            for (user in ratedUsers) {
                user.rating?.toFloat()?.let {
                    totalRating += it
                }
            }
            return totalRating / ratedUsers.size
        } else {
            return 0.0f
        }
    }








}