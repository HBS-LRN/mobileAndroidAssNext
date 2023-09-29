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
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentUserListBinding
import com.example.bait2073mobileapplicationdevelopment.screens.admin.UserForm.UserFormViewModel
import com.example.bait2073mobileapplicationdevelopment.screens.admin.UserForm.UserFormViewModelFactory


class UserListFragment: Fragment(), UserAdapter.UserClickListener, PopupMenu.OnMenuItemClickListener {

    lateinit var recyclerViewAdapter: UserAdapter
    lateinit var viewModel: UserListViewModel
    private lateinit var binding:FragmentUserListBinding
    lateinit var selectedUser : User
    private lateinit var dialog: Dialog
    private lateinit var viewModelFactory: UserListViewModelFactory
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentUserListBinding.inflate(inflater, container, false)
        initViewModel()
        initRecyclerView()
        observeUserDeletion()
        searchUser()

        binding.addUserBtn.setOnClickListener {

            val action =
                UserListFragmentDirections.actionUserListFragmentToCreateUserFragement(0)
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


        viewModelFactory = UserListViewModelFactory()
        viewModel = ViewModelProvider(this, viewModelFactory).get(UserListViewModel::class.java)




        // Initialize your ViewModel using the custom factory
        //another initialize method
//        viewModel = ViewModelProvider(this, UserListViewModelFactory()).get(UserListViewModel::class.java)
        viewModel.getUserListObserverable().observe(viewLifecycleOwner, Observer<List<User?>> {userListResponse ->
            if(userListResponse == null) {
                Toast.makeText(requireContext(), "no result found...", Toast.LENGTH_LONG).show()
            } else {
//                recyclerViewAdapter.updateList(it.toList().get(1))
                val userList = userListResponse.filterNotNull().toMutableList()
                Log.i("haha", "$userList")
                recyclerViewAdapter.updateList(userList)
                recyclerViewAdapter.notifyDataSetChanged()
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

        val action =
            UserListFragmentDirections.actionUserListFragmentToCreateUserFragement(
                user.id ?: 0
            )
        this.findNavController().navigate(action)

    }

    override fun onLongItemClicked(user: User, cardView: CardView) {
        selectedUser = user
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

            viewModel.deleteUser(selectedUser)
        }
        return false
    }

    private fun observeUserDeletion() {
        viewModel.getDeleteUserObservable().observe(viewLifecycleOwner, Observer<User?> { deletedUser ->
            if (deletedUser == null) {
                Toast.makeText(requireContext(), "Cannot Delete User", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Successfully Deleted User", Toast.LENGTH_SHORT).show()
                viewModel.getUsers()

                // You can perform any other actions needed after successful deletion here
            }
        })
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