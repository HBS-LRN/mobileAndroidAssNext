package com.example.bait2073mobileapplicationdevelopment.screens.workout

import UserPlanListModel
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentStartPlanBinding
import com.example.bait2073mobileapplicationdevelopment.entities.StartWorkout
import com.example.bait2073mobileapplicationdevelopment.entities.UserPlanList
import com.example.bait2073mobileapplicationdevelopment.screens.workout.StartPlanDirections
import com.example.bait2073mobileapplicationdevelopment.viewmodel.StartWorkOutViewModel
import java.util.Date


class StartPlan : Fragment() {
    private lateinit var binding: FragmentStartPlanBinding
    private lateinit var dialog: Dialog
    private lateinit var viewModel: StartUserWorkoutPlanViewModel
    lateinit var viewModelRoom: UserPlanListModel
    lateinit var viewModelStartWorkout: StartWorkOutViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val args = AddPlanListArgs.fromBundle(requireArguments())
        val user_plan_id = args.userPlanId
        Log.e("StartPlan", "$user_plan_id")
        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_start_plan, // Make sure this matches your layout file name
            container,
            false)



//        val factory = StartPersonalizedViewModelFactory()
//        viewModel = ViewModelProvider(this, factory).get(StartPersonalizedViewModel::class.java)
        Log.i("GameFragment", "Called ViewModelProvider.get")
//        viewModel = ViewModelProvider(this).get(StartUserWorkoutPlanViewModel::class.java)
        // Inside your StartPlan fragment
        viewModel = ViewModelProvider(this, StartUserWorkoutPlanViewModelFactory(this)).get(StartUserWorkoutPlanViewModel::class.java)

//        viewModel.score.observe(viewLifecycleOwner, Observer { newScore ->
//            binding.scoreText.text = newScore.toString()
//
//        })


        viewModelRoom = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(UserPlanListModel::class.java)
        viewModelRoom.getUserPlanListById(user_plan_id).observe(viewLifecycleOwner){list->
            list?.let{
                viewModel.updateList(list)
            }
        }

        viewModel.progressBar.observe(viewLifecycleOwner, Observer { progress ->
            // Update the ProgressBar's progress here
            binding.pbTimer.progress = progress
        })

        viewModelStartWorkout = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(StartWorkOutViewModel::class.java)

        // Inside your fragment or activity
        viewModel.gifImageUrl.observe(viewLifecycleOwner) { imageUrl ->
            // Load and display the GIF image using Glide
            Glide.with(this)
                .asGif()
                .load(Uri.parse(imageUrl))
                .into(binding.gifImageView) // Replace 'binding.gifImageView' with your GifImageView
        }


        binding.userPlanWorkoutViewModel = viewModel


        // Specify the fragment view as the lifecycle owner of the binding.
        // This is used so that the binding can observe LiveData updates
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.eventActivityFinish.observe(viewLifecycleOwner, Observer<Boolean> { hasFinished ->
            if (hasFinished) activityFinished()
        })
        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Start the timer when the fragment is created

    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Stop the timer when the fragment is destroyed

    }


    /** Methods for button click handlers **/


    private fun activityFinished() {
        val args = AddPlanListArgs.fromBundle(requireArguments())
        val user_plan_id = args.userPlanId
        Log.e("activity","$user_plan_id")

        viewModelRoom.getUserPlanListById(user_plan_id).observe(viewLifecycleOwner){list->
            list?.let{
                insertDataIntoRoomDb(list)
            }
        }



        Log.e("dialog","dialogshow")
//        if (){
//
//        }
//
        viewModel.onActivityFinishComplete()
        showSuccessDialog()
    }

    private fun showSuccessDialog(){
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.custom_dialog_success_workout)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(false) // Optional
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation // Setting the animations to dialog

        val okay: Button = dialog.findViewById(R.id.btn_okay)
        val cancel: Button = dialog.findViewById(R.id.btn_cancel)

        okay.setOnClickListener {
            val action =
                StartPlanDirections.actionStartPlanToReportFragment()
            findNavController().navigate(action)
            dialog.dismiss()

        }

        cancel.setOnClickListener {
            val action =
                StartPlanDirections.actionStartPlanToMyTrainList()
            findNavController().navigate(action)
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        dialog.show() // Showing the dialog here


    }

    fun insertDataIntoRoomDb(UserPlanWorkOut: List<UserPlanList>) {

        val currentDate = Date() // Create a Date object with the current date and time


        val args = AddPlanListArgs.fromBundle(requireArguments())
        val user_plan_id = args.userPlanId

        try {
            for (UserPlanList in UserPlanWorkOut) {
                Log.d("InsertDataIntoRoomDb", "Inserting workout with ID: ${UserPlanList}")
                viewModelStartWorkout.insertWorkout(
                    StartWorkout(
                        null,
                        UserPlanList.name,
                        UserPlanList.userId!!,
                        UserPlanList.description,
                        UserPlanList.link,
                        UserPlanList.gifimage,
                        UserPlanList.calorie,
                        UserPlanList.bmi_status,
                        30,
                        currentDate
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

//    private fun retrieveUserDataFromSharedPreferences(context: Context): Pair<Int, String>? {
//        val sharedPreferences: SharedPreferences =
//            context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
//        val userId = sharedPreferences.getInt(
//            "UserId",
//            -1
//        ) // -1 is a default value if the key is not found
//        val userName = sharedPreferences.getString(
//            "UserName",
//            null
//        ) // null is a default value if the key is not found
//        if (userId != -1 && userName != null) {
//            return Pair(userId, userName)
//        }
//        return null
//    }
}