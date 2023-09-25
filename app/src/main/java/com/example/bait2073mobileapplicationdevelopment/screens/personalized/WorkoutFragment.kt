package com.example.bait2073mobileapplicationdevelopment.screens.personalized

import WorkoutFragmentViewModelFactory
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
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentDisplayPersonalizedWorkoutBinding
import com.example.bait2073mobileapplicationdevelopment.entities.PersonalizedWorkout
import com.example.bait2073mobileapplicationdevelopment.entities.Workout
import com.example.bait2073mobileapplicationdevelopment.screens.workout.WorkoutDetailsActivity
import com.example.bait2073mobileapplicationdevelopment.viewmodel.PersonalizedWorkoutViewModel

class WorkoutFragment : Fragment(), PersonalizedWorkOutAdapter.WorkoutClickListener {

    lateinit var recyclerViewAdapter: PersonalizedWorkOutAdapter
    lateinit var viewModel: WorkoutFragmentViewModel
    lateinit var viewModelRoom: PersonalizedWorkoutViewModel
    private lateinit var binding: FragmentDisplayPersonalizedWorkoutBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDisplayPersonalizedWorkoutBinding.inflate(inflater, container, false)

        viewModelRoom = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(PersonalizedWorkoutViewModel::class.java)
        initViewModel()




        initRecyclerView()
        searchWorkout()

        binding.startButton.setOnClickListener {
            val action = WorkoutFragmentDirections.actionWorkoutFragmentToStartPersonalizedFragment()
            findNavController().navigate(action)

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
        recyclerViewAdapter = PersonalizedWorkOutAdapter(requireContext(), this)
        binding.recycleView.adapter = recyclerViewAdapter

    }

    fun initViewModel() {

        viewModel = ViewModelProvider(this, WorkoutFragmentViewModelFactory())
            .get(WorkoutFragmentViewModel::class.java)


        viewModel.getWorkoutListObserverable()
            .observe(viewLifecycleOwner, Observer<List<Workout?>> { personalizedListResponse ->
                if (personalizedListResponse == null) {
                    Toast.makeText(requireContext(), "no result found...", Toast.LENGTH_LONG).show()
                } else {
//                recyclerViewAdapter.updateList(it.toList().get(1))
                    val workoutList = personalizedListResponse.filterNotNull().toMutableList()
                    Log.i("workoutlist", "$workoutList")
                    recyclerViewAdapter.updateList(workoutList)
                    recyclerViewAdapter.notifyDataSetChanged()

                    clearPersonalizedDb()
                    insertDataIntoRoomDb(workoutList)
                }
            })

        val userData = retrieveUserDataFromSharedPreferences(requireContext())
        val userId = userData?.first
        viewModel.getPersonalizedWorkoutList(userId)
    }


    fun insertDataIntoRoomDb(workouts: List<Workout>) {


        try {
            for (workout in workouts) {
                Log.d("InsertDataIntoRoomDb", "Inserting workout with ID: ${workout}")
                viewModelRoom.insertWorkout(
                    PersonalizedWorkout(
                        id = workout.id,
                        name = workout.name,
                        description = workout.description,
                        link = workout.link,
                        gifimage = workout.gifimage,
                        calorie = workout.calorie,
                        bmi_status = workout.bmi_status
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(
                "InsertDataIntoRoomDb",
                "Error inserting data into Room Database: ${e.message}",

            )
        }


    }

    fun clearPersonalizedDb() {


        viewModelRoom.clearWorkout()

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
            viewModel.getPersonalizedWorkoutList(userId)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onItemClicked(workout: Workout) {
        // Start the new activity here
        val intent = Intent(requireContext(), WorkoutDetailsActivity::class.java)
        intent.putExtra("workout", workout)
        startActivity(intent)

    }


}
