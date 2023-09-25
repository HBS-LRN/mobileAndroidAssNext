package com.example.bait2073mobileapplicationdevelopment.screens.report.UserReport

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.adapter.CalendarAdapter
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentReportBinding
import com.example.bait2073mobileapplicationdevelopment.entities.User
import com.example.bait2073mobileapplicationdevelopment.utilities.CalendarUtils
import com.example.bait2073mobileapplicationdevelopment.utilities.calendar.MonthlyViewActivity
import com.example.bait2073mobileapplicationdevelopment.viewmodel.StartWorkOutViewModel
import com.example.bait2073mobileapplicationdevelopment.viewmodel.UserViewModel
import com.squareup.picasso.Picasso
import java.text.DecimalFormat
import java.time.LocalDate

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserReportFragment : Fragment(), CalendarAdapter.OnItemListener {

    private var monthYearText: TextView? = null
    private var calendarRecyclerView: RecyclerView? = null
    private lateinit var binding: FragmentReportBinding
    lateinit var viewModelStartWorkout: StartWorkOutViewModel
    lateinit var viewModel: UserReportViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate view and obtain an instance of the binding class
        binding = FragmentReportBinding.inflate(
            inflater,
            container,
            false
        )

        viewModel = ViewModelProvider(this, UserReportViewModelFactory())
            .get(UserReportViewModel::class.java)


        val userData = retrieveUserDataFromSharedPreferences(requireContext())
        val userId = userData?.first
        loadUserData(userId)


        //intialize view model
        viewModelStartWorkout = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(StartWorkOutViewModel::class.java)



        viewModelStartWorkout.allStartWorkout.observe(viewLifecycleOwner) { list ->
            list?.let {


                var totalCalorie = 0.0 // Initialize totalCalorie to zero
                var durationInSeconds = 0


                for (workout in list) {
                    if (workout.userId == userId) {
                        workout.calorie?.let {
                            totalCalorie += it // Add the calorie value of each workout to totalCalorie
                        }
                        workout.duration?.let {
                            durationInSeconds += it // Add the calorie value of each workout to totalCalorie
                        }
                    }
                }

                // Calculate minutes and seconds
                val minutes = durationInSeconds / 60
                val seconds = durationInSeconds % 60

               // Format minutes and seconds into "mm:ss" format
                val formattedDuration = String.format("%02d:%02d", minutes, seconds)
                binding.totalDurationTV.text = "$formattedDuration"
                binding.totalWorkOutTV.text = list.size.toString()




//
//                for (workout in list) {
//                    totalCalorie += workout.calorie ?: 0.0 // Add the calorie value of each workout to totalCalorie
//                }

                val format = DecimalFormat("###.0")
                if(totalCalorie==0.0){
                    binding.totalCalorieTV.text = "0.0"
                }else {
                    val formattedCalorie = format.format(totalCalorie)
                    // Now, you can display the totalCalorie in your UI
                    binding.totalCalorieTV.text = "$formattedCalorie"
                }
            }
        }
        // Update your UI elements with the user data
        binding.heightImageView.setOnClickListener() {

            navigateToProfileFragment()
        }


        binding.weightImageView.setOnClickListener {
            navigateToProfileFragment()
        }

        binding.editBmiClick.setOnClickListener {
            navigateToProfileFragment()
        }
        if (CalendarUtils.selectedDate == null) {
            CalendarUtils.selectedDate = LocalDate.now()
        }
        initWidgets()
        setWeekView()


        val nextHistoryImageView = binding.nextHistory
        nextHistoryImageView.setOnClickListener {
            // Create an intent to navigate to the destination activity
            val intent = Intent(requireContext(), MonthlyViewActivity::class.java)
            // Start the destination activity
            startActivity(intent)
        }

        return binding.root

    }

    private fun navigateToProfileFragment() {

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.reportFragment, true)
            .build()
        findNavController().navigate(
            R.id.action_reportFragment_to_profileFragment,
            null,
            navOptions
        )
    }

    private fun loadUserData(user_id: Int?) {
        viewModel.getLoadUserObservable().observe(viewLifecycleOwner, Observer<User?> {
            if (it != null) {

                val format = DecimalFormat("###.0")
                val formattedHeight = format.format(it.height)
                val formattedWeight = format.format(it.weight)
                binding.heightEdit.text = "$formattedHeight"
                binding.weightEdit.text = "$formattedWeight"

                calculateBMI(it.height ?: 0.0, it.weight ?: 0.0)

            }


        })
        viewModel.getUserData(user_id)
    }

    private fun calculateBMI(height: Double, weight: Double) {

        val heightInMeters = height / 100.0
        // Calculate BMI
        val bmi = weight / (heightInMeters * heightInMeters)

        binding.bmiEdit.text = String.format("%.2f", bmi)
        binding.bmiStatus.text = String.format("%s", healthyMessage(bmi))

    }

    private fun healthyMessage(bmi: Double): String {
        if (bmi < 18.5)
            return "Underweight"
        if (bmi < 25.0)
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


    private fun initWidgets() {
        calendarRecyclerView = binding.calendarRecyclerView
        monthYearText = binding.monthYearTV

    }


    private fun setWeekView() {
        monthYearText!!.text = CalendarUtils.monthYearFromDate(CalendarUtils.selectedDate!!)
        val days = CalendarUtils.daysInWeekArray(CalendarUtils.selectedDate!!)
        val calendarAdapter = CalendarAdapter(days, this)
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(requireContext(), 7)
        calendarRecyclerView!!.layoutManager = layoutManager
        calendarRecyclerView!!.adapter = calendarAdapter
//        setEventAdpater()
    }

    fun previousWeekAction(view: View?) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate!!.minusWeeks(1)
        setWeekView()
    }

    fun nextWeekAction(view: View?) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate!!.plusWeeks(1)
        setWeekView()
    }


    override fun onResume() {
        super.onResume()
//        setEventAdpater()
    }

//    private fun setEventAdpater() {
//        val dailyEvents: ArrayList<Event> = Event.eventsForDate(CalendarUtils.selectedDate)
//        val eventAdapter = EventAdapter(applicationContext, dailyEvents)
//        eventListView!!.adapter = eventAdapter
//    }
//
//    fun newEventAction(view: View?) {
//        startActivity(Intent(this, EventEditActivity::class.java))
//    }


    override fun onItemClick(position: Int, date: LocalDate?) {
        CalendarUtils.selectedDate = date
        setWeekView()
    }


}