package com.example.bait2073mobileapplicationdevelopment.screens.workout

import UserPlanListAdapter
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentHomeBinding
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentMyTrainListBinding
import com.example.bait2073mobileapplicationdevelopment.entities.User
import com.example.bait2073mobileapplicationdevelopment.entities.UserPlan
import com.example.bait2073mobileapplicationdevelopment.entities.UserPlanList
import com.example.bait2073mobileapplicationdevelopment.entities.Workout
import com.example.bait2073mobileapplicationdevelopment.screens.admin.UserList.UserListFragmentDirections
import com.example.bait2073mobileapplicationdevelopment.screens.admin.WorkoutList.WorkoutListFragmentDirections
import com.example.bait2073mobileapplicationdevelopment.screens.dialog.AddPlanPopUpFragment
import com.example.bait2073mobileapplicationdevelopment.viewmodel.UserPlanViewModel

class MyTrainList : Fragment(), UserPlanListAdapter.UserPlanClickListener, PopupMenu.OnMenuItemClickListener {

    lateinit var recyclerViewAdapter: UserPlanListAdapter
    lateinit var viewModel: MyTrainViewModel
    private lateinit var binding: FragmentMyTrainListBinding

    lateinit var selectedPlan: UserPlan
    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_my_train_list, container, false)
        binding = FragmentMyTrainListBinding.inflate(inflater, container, false)
        initViewModel()
        initRecyclerView()
        binding.ActionBtn.setOnClickListener{
            showPlanNameDialog()
        }

        createUserObservable()

        Log.e("mytrainlist","mytrain")



        return binding.root

    }


    private fun showPlanNameDialog() {
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
                createPlan(planName)
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

    private fun createPlan(plan_name: String) {

        val userData = retrieveUserDataFromSharedPreferences(requireContext())

        val userId = userData?.first

        Log.e("user_plan_id","$userId")

        val userPlan = UserPlan(
            null,
            userId!!,
            plan_name
        )
        viewModel.createUserPlan(userPlan)

    }

    private fun createUserObservable() {
        viewModel.getCreateNewUserObservable().observe(viewLifecycleOwner, Observer<UserPlan?> {
            if (it == null) {

                Log.e("create fail","create fail")
            } else {
                Log.e("create success","create success")


                val action =
                    MyTrainListDirections.actionMyTrainListToAddPlanLIst(it.id!!)
                this.findNavController().navigate(action)

            }
        })
    }



//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        val planNameEditText = view.findViewById<EditText>(R.id.PlanNameTextField)
//        val addPlanBtn = view.findViewById<Button>(R.id.addPlanbtn)
//        binding.ActionBtn.setOnClickListener {
//            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_add_plan_pop_up, null)
//            val dialog = AlertDialog.Builder(requireContext())
//                .setView(dialogView)
//                .create()
//
//            // Handle "Add Plan" button click inside the dialog
//            addPlanBtn.setOnClickListener {
//                val planName = planNameEditText.text.toString()
//
//                if (planName.isNotEmpty()) {
//                    // Handle the planName as needed
//                    Log.e("value", planName)
//                    dialog.dismiss()
//                } else {
//                    Toast.makeText(requireContext(), "Plan name is empty", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            dialog.show()
//        }
//    }

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

    private fun initRecyclerView() {
        binding.recycleView.setHasFixedSize(true)
        binding.recycleView.layoutManager = StaggeredGridLayoutManager(1, LinearLayout.VERTICAL)
        recyclerViewAdapter = UserPlanListAdapter(requireContext(), this)
        binding.recycleView.adapter = recyclerViewAdapter
        //recyclerViewAdapter.setData() // Replace 'yourUserPlanListData' with your actual data
//        recyclerViewAdapter.notifyDataSetChanged()
    }

    private fun initViewModel() {


        val userData = retrieveUserDataFromSharedPreferences(requireContext())
        val userId = userData?.first!!
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(
            MyTrainViewModel::class.java
        )

        viewModel.getUserPlanObserverable()
            .observe(viewLifecycleOwner, Observer<List<UserPlan?>> { userListResponse ->
                if (userListResponse == null) {
                    Toast.makeText(requireContext(), "no result found...", Toast.LENGTH_LONG).show()
                } else {
//                recyclerViewAdapter.updateList(it.toList().get(1))
                    val userPlanList = userListResponse.filterNotNull().toMutableList()
                    Log.i("foyooooo", "$userPlanList")

                    recyclerViewAdapter.updateUserPlanList(userPlanList)
                    recyclerViewAdapter.setData(userPlanList)

                    recyclerViewAdapter.notifyDataSetChanged()

                }
            })

        Log.e("user", "$userId")
        viewModel.getPlan(userId)
    }

//    override fun onItemClicked(userPlan: UserPlan, userPlanList: UserPlanList) {
//        val action = MyTrainListDirections.actionMyTrainListToAddPlanLIst()
//        this.findNavController().navigate(action)
//    }
//
//    override fun OnLongItemClicked(
//        userPlan: UserPlan,
//    userPlanList: UserPlanList,
//    cardView: CardView
//    ) {
//        selectedPlanList = userPlanList
//        selectedPlan = userPlan
//        popUpDisplay(cardView)
//    }

    private fun popUpDisplay(cardView: CardView) {

        val popup = PopupMenu(requireContext(), cardView)
        popup.setOnMenuItemClickListener(this)
        popup.inflate(R.menu.pop_up_menu)
        popup.show()

    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        val userData = retrieveUserDataFromSharedPreferences(requireContext())
        val userId = userData?.first!!
        if (item?.itemId == R.id.delete_note) {

            viewModel.deleteUserPlan(selectedPlan.id)
            val action =
                MyTrainListDirections.actionMyTrainListToHomeFragment()
            this.findNavController().navigate(action)
        }
        return false
    }

    override fun onItemClicked(userPlan: UserPlan) {
//        // Start the new activity here
//        val intent = Intent(requireContext(), userPlanWorkoutShow::class.java)
//        intent.putExtra("userPlanList", userPlan.id)
//        startActivity(intent)
        val action = MyTrainListDirections.actionMyTrainListToUserPlanWorkoutShow(userPlan.id!!)
        this.findNavController().navigate(action)
    }

    override fun OnLongItemClicked(userPlan: UserPlan, cardView: CardView) {
        selectedPlan = userPlan
        popUpDisplay(cardView)
    }




}
