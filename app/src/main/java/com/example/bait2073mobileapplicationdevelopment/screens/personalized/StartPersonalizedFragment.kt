package com.example.bait2073mobileapplicationdevelopment.screens.personalized

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentStartPersonalizedPlanBinding
import com.example.bait2073mobileapplicationdevelopment.entities.PersonalizedWorkout
import com.example.bait2073mobileapplicationdevelopment.entities.StartWorkout
import com.example.bait2073mobileapplicationdevelopment.screens.personalized.StartPersonalizedFragmentDirections
import com.example.bait2073mobileapplicationdevelopment.viewmodel.PersonalizedWorkoutViewModel
import com.example.bait2073mobileapplicationdevelopment.viewmodel.StartWorkOutViewModel
import java.util.Date

class StartPersonalizedFragment : Fragment() {


    private lateinit var binding: FragmentStartPersonalizedPlanBinding

    private lateinit var dialog: Dialog
    private lateinit var viewModel: StartPersonalizedViewModel
    lateinit var viewModelRoom: PersonalizedWorkoutViewModel
    lateinit var viewModelStartWorkout: StartWorkOutViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_start_personalized_plan, // Make sure this matches your layout file name
            container,
            false)



//        val factory = StartPersonalizedViewModelFactory()
//        viewModel = ViewModelProvider(this, factory).get(StartPersonalizedViewModel::class.java)
        Log.i("GameFragment", "Called ViewModelProvider.get")
        viewModel = ViewModelProvider(this).get(StartPersonalizedViewModel::class.java)
//        viewModel.score.observe(viewLifecycleOwner, Observer { newScore ->
//            binding.scoreText.text = newScore.toString()
//
//        })


        viewModelRoom = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(PersonalizedWorkoutViewModel::class.java)
        viewModelRoom.allPersonalizedWorkout.observe(viewLifecycleOwner){list->
            list?.let{
                viewModel.updateList(list)
            }
        }

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


        binding.workoutViewModel = viewModel


        // Specify the fragment view as the lifecycle owner of the binding.
        // This is used so that the binding can observe LiveData updates
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.eventGameFinish.observe(viewLifecycleOwner, Observer<Boolean> { hasFinished ->
            if (hasFinished) activityFinished()
        })
        return binding.root

    }


    /** Methods for button click handlers **/


    private fun activityFinished() {

        viewModelRoom.allPersonalizedWorkout.observe(viewLifecycleOwner){list->
            list?.let{
                insertDataIntoRoomDb(list)
            }
        }



        showSuccessDialog()
        viewModel.onGameFinishComplete()
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
            val action =
                StartPersonalizedFragmentDirections.actionStartPersonalizedFragmentToReportFragment()
            findNavController().navigate(action)
            dialog.dismiss()

        }

        cancel.setOnClickListener {
            val action =
                StartPersonalizedFragmentDirections.actionStartPersonalizedFragmentToWorkoutFragment()
            findNavController().navigate(action)
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        dialog.show() // Showing the dialog here


    }

    fun insertDataIntoRoomDb(workouts: List<PersonalizedWorkout>) {

        val currentDate = Date() // Create a Date object with the current date and time
        try {
            for (workout in workouts) {
                Log.d("InsertDataIntoRoomDb", "Inserting workout with ID: ${workout}")
                viewModelStartWorkout.insertWorkout(
                    StartWorkout(
                        id = workout.id,
                        name = workout.name,
                        description = workout.description,
                        link = workout.link,
                        gifimage = workout.gifimage,
                        calorie = workout.calorie,
                        bmi_status = workout.bmi_status,
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

}