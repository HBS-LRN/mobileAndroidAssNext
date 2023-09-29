package com.example.bait2073mobileapplicationdevelopment.screens.workout

import UserPlanListModel
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.adapter.userPlanWorkoutShowAdapter
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentUserPlanWorkoutShowBinding
import com.example.bait2073mobileapplicationdevelopment.entities.UserPlan
import com.example.bait2073mobileapplicationdevelopment.entities.UserPlanList
import com.example.bait2073mobileapplicationdevelopment.entities.Workout
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetUserPLanListService
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetWorkoutDataService
import com.example.bait2073mobileapplicationdevelopment.retrofitclient.RetrofitClientInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class userPlanWorkoutShow : Fragment(), userPlanWorkoutShowAdapter.WorkoutClickListener {

    lateinit var recyclerViewAdapter: userPlanWorkoutShowAdapter
    lateinit var viewModel: userrPlanWorkoutViewModel
    lateinit var viewModelPlan: MyTrainViewModel
    lateinit var viewModelRoom: UserPlanListModel
    private lateinit var binding: FragmentUserPlanWorkoutShowBinding
    lateinit var selectedPlanList: UserPlanList


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e("userPlanWork","userPlanWork")
        binding = FragmentUserPlanWorkoutShowBinding.inflate(inflater, container, false)
        val args = userPlanWorkoutShowArgs.fromBundle(requireArguments())
        val user_plan_id = args.userPlanId
//        viewModelRoom = ViewModelProvider(
//            this,
//            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
//        ).get(PersonalizedWorkoutViewModel::class.java)
        binding.startButton.setOnClickListener{
            val action = userPlanWorkoutShowDirections.actionUserPlanWorkoutShowToStartPlan(user_plan_id)
            this.findNavController().navigate(action)
        }
        binding.EditUserPlanBtn.setOnClickListener {
            showEditUserPlanNameDialog()
        }
        binding.addWorkoutBtn.setOnClickListener{
            val action = userPlanWorkoutShowDirections.actionUserPlanWorkoutShowToAddPlanLIst(user_plan_id)
            this.findNavController().navigate(action)
        }
        initViewModel()

        initRecyclerView()
        searchWorkout()

//        binding.startButton.setOnClickListener {
//
//            findNavController().navigate(R.id.)
//
//        }
        viewModelRoom = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(UserPlanListModel::class.java)

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
        recyclerViewAdapter = userPlanWorkoutShowAdapter(requireContext(), this)
        binding.recycleView.adapter = recyclerViewAdapter

    }

    fun initViewModel() {
        val args = userPlanWorkoutShowArgs.fromBundle(requireArguments())
        val user_plan_id = args.userPlanId
        Log.e("TestUserPlanID","$user_plan_id")
        viewModel = ViewModelProvider(
            this,
            userPlanWorkoutViewModelFactory()
        ).get(userrPlanWorkoutViewModel::class.java)
        viewModelPlan = ViewModelProvider(this, MyTrainViewModelFactory()).get(MyTrainViewModel::class.java)


        viewModel.getWorkoutListObserverable()
            .observe(viewLifecycleOwner, Observer<List<UserPlanList?>> { userPlanListResponse ->
                if (userPlanListResponse == null) {
                    Toast.makeText(requireContext(), "no result found...", Toast.LENGTH_LONG).show()
                } else {
//                recyclerViewAdapter.updateList(it.toList().get(1))
                    val workoutList = userPlanListResponse.filterNotNull().toMutableList()
                    Log.i("workoutlist", "$workoutList")
                    recyclerViewAdapter.updateList(workoutList)
                    recyclerViewAdapter.notifyDataSetChanged()

//                    clearUserPlandDb()
                    insertDataIntoRoomDb(workoutList)
                }
            })

        viewModel.getUserPlanWorkoutList(user_plan_id)
    }

    private fun showEditUserPlanNameDialog() {
        val args = userPlanWorkoutShowArgs.fromBundle(requireArguments())
        val user_plan_id = args.userPlanId
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Enter Plan Name")

        val input = EditText(requireContext())
        input.hint = "Plan Name"
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, _ ->
            val planName = input.text.toString()
            if (planName.isNotEmpty()) {
                // Handle the planName as needed
                Log.e("value", planName)
                updatePlanName(user_plan_id,planName)
                val action = userPlanWorkoutShowDirections.actionUserPlanWorkoutShowToMyTrainList()
                findNavController().navigate(action)
            } else {
                Toast.makeText(requireContext(), "Plan name is empty", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun updatePlanName(UserPlanId: Int,newPlanName: String) {

        val userData = retrieveUserDataFromSharedPreferences(requireContext())

        val userId = userData?.first

        Log.e("user_id","$userId")

        val userPlan = UserPlan(
            UserPlanId,
            userId!!,
            newPlanName
        )
        viewModelPlan.editUserPlanName(userPlan)

    }

    fun insertDataIntoRoomDb(userPlanList: List<UserPlanList>) {


        try {
            for (userPlanWorkout in userPlanList) {
                Log.d("InsertDataIntoRoomDb", "Inserting workout with ID: ${userPlanWorkout}")
                viewModelRoom.insertUserPlanList(
                    UserPlanList(
                        id = userPlanWorkout.id,
                        userPlanId= userPlanWorkout.userPlanId,
                        workoutId = userPlanWorkout.workoutId,
                        name = userPlanWorkout.name,
                        userId= userPlanWorkout.userId,
                        description = userPlanWorkout.description,
                        link = userPlanWorkout.link,
                        gifimage = userPlanWorkout.gifimage,
                        calorie = userPlanWorkout.calorie,
                        bmi_status = userPlanWorkout.bmi_status

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

    fun clearUserPlandDb() {
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
        if (requestCode == 1000) {
            viewModel.getWorkoutListObserverable()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onItemClicked(userPlanList: UserPlanList) {
        val service = RetrofitClientInstance.retrofitInstance!!.create(GetWorkoutDataService::class.java)
        val call = service.getWorkout(userPlanList.workoutId)
        call.enqueue(object : Callback<Workout> {
            override fun onResponse(call: Call<Workout>, response: Response<Workout>) {
                if (response.isSuccessful) {
                    val workout = response.body()
                    if (workout != null) {
                        // Successfully retrieved the workout data
                        Log.e("API Response", "Response body is empty")
                        val intent = Intent(requireContext(), WorkoutDetailsActivity::class.java)
                        intent.putExtra("workout", workout)
                        startActivity(intent)
                    } else {
                        val resposne = response.body()
                        val errorBody = response.errorBody()?.string()
                        val responseCode = response.code()
                        val responseMessage = response.message()
                        Log.e("haha", "Response is not successful. Code: $responseCode, Message: $responseMessage, Error Body: $errorBody")
                    }
                } else {
                    val resposne = response.body()
                    val errorBody = response.errorBody()?.string()
                    val responseCode = response.code()
                    val responseMessage = response.message()
                    Log.e("haha", "Response is not successful. Code: $responseCode, Message: $responseMessage, Error Body: $errorBody")
                }
            }

            override fun onFailure(call: Call<Workout>, t: Throwable) {
                Log.e("API Response", "Response body is Fail")
            }
        })
//        val intent = Intent(requireContext(), WorkoutDetailsActivity::class.java)
//        intent.putExtra("workout", userPlanList)
//        startActivity(intent)

    }


//    private fun popUpDisplay(cardView: CardView) {
//
//        val popup = PopupMenu(requireContext(), cardView)
//        popup.setOnMenuItemClickListener(this)
//        popup.inflate(R.menu.pop_up_menu)
//        popup.show()
//
//    }
//
//    override fun onMenuItemClick(item: MenuItem?): Boolean {
//        val userData = retrieveUserDataFromSharedPreferences(requireContext())
//        val userId = userData?.first!!
//        if (item?.itemId == R.id.delete_note) {
//
//            viewModel.deleteUserPlanList(selectedPlanList.id)
//            val action =
//                userPlanWorkoutShowDirections.actionUserPlanWorkoutShowToMyTrainList()
//            this.findNavController().navigate(action)
//        }
//        return false
//    }
//    override fun OnLongItemClicked(userPlanList: UserPlanList, cardView: CardView) {
//        selectedPlanList = userPlanList
//        popUpDisplay(cardView)
//    }


}