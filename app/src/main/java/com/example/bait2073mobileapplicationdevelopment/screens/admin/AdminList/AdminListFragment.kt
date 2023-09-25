package com.example.bait2073mobileapplicationdevelopment.screens.admin.AdminList

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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.bait2073mobileapplicationdevelopment.adapter.UserAdapter
import com.example.bait2073mobileapplicationdevelopment.entities.User
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentAdminListBinding
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentUserListBinding
import com.example.bait2073mobileapplicationdevelopment.screens.admin.UserList.AdminListViewModel


class AdminListFragment : Fragment(), UserAdapter.UserClickListener,
    PopupMenu.OnMenuItemClickListener {

    lateinit var recyclerViewAdapter: UserAdapter
    lateinit var viewModel: AdminListViewModel
    private lateinit var binding: FragmentAdminListBinding
    lateinit var selectedUser: User
    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAdminListBinding.inflate(inflater, container, false)
        initViewModel()
        initRecyclerView()

        observeUserDeletion()
        searchUser()

        binding.deleteImg.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Long-press to delete a admin.",
                Toast.LENGTH_SHORT
            ).show()
        }
        binding.addUserBtn.setOnClickListener {

            val action =
                AdminListFragmentDirections.actionAdminListFragmentToAdminFormFragment(0)
            this.findNavController().navigate(action)
        }


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
        recyclerViewAdapter = UserAdapter(requireContext(), this)
        binding.recycleView.adapter = recyclerViewAdapter

    }

    fun initViewModel() {
//        viewModel = ViewModelProvider(
//            this,
//            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
//        ).get(
//            AdminListViewModel::class.java
//        )


        // Initialize your ViewModel using the custom factory
        viewModel = ViewModelProvider(this, AdminListViewModelFactory()).get(AdminListViewModel::class.java)
        viewModel.getUserListObserverable()
            .observe(viewLifecycleOwner, Observer<List<User?>> { userListResponse ->
                if (userListResponse == null) {
                    Toast.makeText(requireContext(), "no result found...", Toast.LENGTH_LONG).show()
                } else {
//                recyclerViewAdapter.updateList(it.toList().get(1))

                    val userData = retrieveUserDataFromSharedPreferences(requireContext())
                    val userId = userData?.first
                    val filteredUserList = userListResponse.filter { user ->
                        user?.id != userId
                    }.filterNotNull().toMutableList()
                    Log.i("haha", "$filteredUserList")
                    recyclerViewAdapter.updateList(filteredUserList)
                    recyclerViewAdapter.notifyDataSetChanged()
                }
            })
        viewModel.getUsers()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 1000) {
            viewModel.getUsers()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onItemClicked(user: User) {


        val action =
            AdminListFragmentDirections.actionAdminListFragmentToAdminFormFragment(user.id!!)
        this.findNavController().navigate(action)

    }

    override fun onLongItemClicked(user: User, cardView: CardView) {
        selectedUser = user
        popUpDisplay(cardView)
    }

    private fun popUpDisplay(cardView: CardView) {

        val popup = PopupMenu(requireContext(), cardView)
        popup.setOnMenuItemClickListener(this)
        popup.inflate(R.menu.pop_up_menu)
        popup.show()

    }


    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.delete_note) {

            viewModel.deleteUser(selectedUser)
        }
        return false
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

    private fun observeUserDeletion() {
        viewModel.getDeleteUserObservable()
            .observe(viewLifecycleOwner, Observer<User?> { deletedUser ->
                if (deletedUser == null) {
                    Toast.makeText(requireContext(), "Cannot Delete User", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(requireContext(), "Successfully Deleted User", Toast.LENGTH_SHORT).show()
                    viewModel.getUsers()

                    // You can perform any other actions needed after successful deletion here
                }
            })
    }


    private fun showSuccessDialog() {
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.custom_dialog_success)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false) // Optional
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        dialog.window?.attributes?.windowAnimations =
            R.style.DialogAnimation // Setting the animations to dialog

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