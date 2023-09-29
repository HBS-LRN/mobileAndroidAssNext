package com.example.bait2073mobileapplicationdevelopment.screens.workout

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.bait2073mobileapplicationdevelopment.adapter.PersonalizedWorkOutAdapter
import com.example.bait2073mobileapplicationdevelopment.adapter.WorkOutAdapter
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentAddPlanLIstBinding
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentDisplayPersonalizedWorkoutBinding
import com.example.bait2073mobileapplicationdevelopment.entities.PersonalizedWorkout
import com.example.bait2073mobileapplicationdevelopment.entities.UserPlan
import com.example.bait2073mobileapplicationdevelopment.entities.UserPlanList
import com.example.bait2073mobileapplicationdevelopment.entities.Workout
import com.example.bait2073mobileapplicationdevelopment.screens.admin.UserForm.UserFormFragmentArgs
import com.example.bait2073mobileapplicationdevelopment.screens.admin.UserList.UserListFragmentDirections
import com.example.bait2073mobileapplicationdevelopment.screens.personalized.WorkoutFragmentDirections
import com.example.bait2073mobileapplicationdevelopment.screens.personalized.WorkoutFragmentViewModel
import com.example.bait2073mobileapplicationdevelopment.viewmodel.PersonalizedWorkoutViewModel

class AddPlanList : Fragment(), WorkOutAdapter.WorkoutClickListener {

    lateinit var recyclerViewAdapter: WorkOutAdapter
    lateinit var viewModel: AddPlanListViewModel
//    lateinit var viewModelRoom: PersonalizedWorkoutViewModel
    private lateinit var binding: FragmentAddPlanLIstBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.e("addplan","addplan")
        binding = FragmentAddPlanLIstBinding.inflate(inflater, container, false)

//        viewModelRoom = ViewModelProvider(
//            this,
//            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
//        ).get(PersonalizedWorkoutViewModel::class.java)
        initViewModel()
        initRecyclerView()
        searchWorkout()
        binding.savePlanBtn.setOnClickListener{
            val action =
                AddPlanListDirections.actionAddPlanLIstToMyTrainList()
            this.findNavController().navigate(action)
        }

        return binding.root
    }


    private fun searchWorkout() {
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
        recyclerViewAdapter = WorkOutAdapter(requireContext(), this)
        binding.recycleView.adapter = recyclerViewAdapter

    }

    fun initViewModel() {
//        viewModel = ViewModelProvider(
//            this,
//            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
//        ).get(
//            AddPlanListViewModel::class.java
//        )
        // Inside your AddPlanList fragment
        viewModel = ViewModelProvider(this, AddPlanListViewModelFactory()).get(AddPlanListViewModel::class.java)



        viewModel.getWorkoutObserverable()
            .observe(viewLifecycleOwner, Observer<List<Workout?>> { WorkoutListResponse ->
                if (WorkoutListResponse == null) {
                    Toast.makeText(requireContext(), "no result found...", Toast.LENGTH_LONG).show()
                } else {
//                recyclerViewAdapter.updateList(it.toList().get(1))
                    val workoutList = WorkoutListResponse.filterNotNull().toMutableList()
                    Log.i("workoutlist", "$workoutList")
                    recyclerViewAdapter.updateList(workoutList)
                    recyclerViewAdapter.notifyDataSetChanged()


                }
            })
        viewModel.getWorkouts()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val userData = retrieveUserDataFromSharedPreferences(requireContext())
        val userId = userData?.first
        if (requestCode == 1000) {
            viewModel.getWorkouts()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onItemClicked(workout: Workout) {
        val args = AddPlanListArgs.fromBundle(requireArguments())
        val user_plan_id = args.userPlanId
        // Start the new activity here
        val intent = Intent(requireContext(), WorkoutDetailsActivity::class.java)

        val userData = retrieveUserDataFromSharedPreferences(requireContext())

        val userId = userData?.first

        Log.e("userplanid","$userId")

        val userPlanList= UserPlanList(
            null,
            user_plan_id!!,
            workout.id!!,
            workout.name!!,
            userId!!,
            workout.description!!,
            workout.gifimage!!,
            workout.calorie!!,
            workout.link!!,
            workout.bmi_status
        )
        viewModel.createPlanWorkout(userPlanList)
        Toast.makeText(requireContext(),"Workout Created to your plan",Toast.LENGTH_SHORT).show()


    }


}
