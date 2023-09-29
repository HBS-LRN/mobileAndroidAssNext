package com.example.bait2073mobileapplicationdevelopment.screens.admin.WorkoutList

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.adapter.WorkoutListAdapter
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentAddPlanLIstBinding
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentWorkoutListBinding
import com.example.bait2073mobileapplicationdevelopment.entities.User
import com.example.bait2073mobileapplicationdevelopment.entities.Workout
import com.example.bait2073mobileapplicationdevelopment.screens.admin.UserList.UserListFragmentDirections
import com.example.bait2073mobileapplicationdevelopment.screens.workout.AddPlanListArgs
import com.example.bait2073mobileapplicationdevelopment.screens.workout.AddPlanListDirections
import com.example.bait2073mobileapplicationdevelopment.screens.workout.AddPlanListViewModel

class WorkoutListFragment : Fragment(), WorkoutListAdapter.WorkoutClickListener, PopupMenu.OnMenuItemClickListener {

    lateinit var recyclerViewAdapter: WorkoutListAdapter
    lateinit var viewModel: WorkoutListViewModel
    //    lateinit var viewModelRoom: PersonalizedWorkoutViewModel
    private lateinit var binding: FragmentWorkoutListBinding
    lateinit var selectedWorkout : Workout
    private lateinit var dialog: Dialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.e("addplan","addplan")
        binding = FragmentWorkoutListBinding.inflate(inflater, container, false)

//        viewModelRoom = ViewModelProvider(
//            this,
//            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
//        ).get(PersonalizedWorkoutViewModel::class.java)
        initViewModel()
        initRecyclerView()
        searchWorkout()
        observeWorkoutDeletion()


        binding.addUserBtn.setOnClickListener {

            val action =
                WorkoutListFragmentDirections.actionWorkoutListFragmentToWorkoutFormFragment(
                    0
                )
            this.findNavController().navigate(action)
        }

        binding.deleteImg.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Long-press to delete a user.",
                Toast.LENGTH_SHORT
            ).show()
        }


        return binding.root
    }



    private fun observeWorkoutDeletion() {
        viewModel.getDeleteWorkoutObservable().observe(viewLifecycleOwner, Observer<Workout?> { deletedUser ->
            if (deletedUser == null) {
                Toast.makeText(requireContext(), "Cannot Delete User", Toast.LENGTH_SHORT).show()
            } else {
                showSuccessDialog()
                viewModel.getWorkouts()

                // You can perform any other actions needed after successful deletion here
            }
        })
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
        recyclerViewAdapter = WorkoutListAdapter(requireContext(), this)
        binding.recycleView.adapter = recyclerViewAdapter

    }

    fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            WorkoutListViewModelFactory()
        ).get(WorkoutListViewModel::class.java)


        viewModel.getWorkoutListObserverable()
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


        if (requestCode == 1000) {
            viewModel.getWorkouts()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onItemClicked(workout: Workout) {


        val action =
            WorkoutListFragmentDirections.actionWorkoutListFragmentToWorkoutFormFragment(
                workout.id!!
            )
        this.findNavController().navigate(action)



    }

    override fun onLongItemClicked(workout: Workout, cardView: CardView) {
        selectedWorkout = workout
        popUpDisplay(cardView)
    }


    private fun popUpDisplay(cardView: CardView) {

        val popup = PopupMenu(requireContext(),cardView)
        popup.setOnMenuItemClickListener(this)
        popup.inflate(R.menu.pop_up_menu)
        popup.show()

    }


    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.delete_note){

            viewModel.deleteWorkout(selectedWorkout)

        }
        return false
    }



    private fun showSuccessDialog(){
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.custom_dialog_success)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(false) // Optional
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation // Setting the animations to dialog

        val okay: Button = dialog.findViewById(R.id.btn_okay)
        val cancel: Button = dialog.findViewById(R.id.btn_cancel)

        okay.setOnClickListener {
            dialog.dismiss()
        }

        cancel.setOnClickListener {
            Toast.makeText(requireContext(), "Cancel", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        dialog.show() // Showing the dialog here
    }



}