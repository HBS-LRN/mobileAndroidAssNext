package com.example.bait2073mobileapplicationdevelopment.screens.admin.UserForm

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.bait2073mobileapplicationdevelopment.adapter.UserAdapter
import com.example.bait2073mobileapplicationdevelopment.database.HealthyLifeDatabase
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentUserListBinding
import com.example.bait2073mobileapplicationdevelopment.entities.User
import com.example.bait2073mobileapplicationdevelopment.viewmodel.UserViewModel

class UserFragment : Fragment(), UserAdapter.UserClickListener, PopupMenu.OnMenuItemClickListener {


    private lateinit var dialog: Dialog
    private lateinit var binding:FragmentUserListBinding
    private lateinit var database: HealthyLifeDatabase
    lateinit var viewModel: UserViewModel
    lateinit var adapter: UserAdapter
    lateinit var selectedCustomer: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        binding = FragmentUserListBinding.inflate(inflater, container, false)

        initUi()
        viewModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)).get(UserViewModel::class.java)

        viewModel.allUsers.observe(viewLifecycleOwner){list->
            list?.let{
                adapter.updateList(list)
            }
        }

        database = HealthyLifeDatabase.getDatabase(requireContext())

        return binding.root
    }

    private fun initUi() {
        binding.recycleView.setHasFixedSize(true)
        binding.recycleView.layoutManager = StaggeredGridLayoutManager(1, LinearLayout.VERTICAL)
        adapter = UserAdapter(requireContext(), this)
        binding.recycleView.adapter = adapter

        //register for activity
//        val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
//                result -> if(result.resultCode == Activity.RESULT_OK ){
//            val note = result.data?.getSerializableExtra("note") as? Note
//            if(note != null ){
//                viewModel.insertNote(note)
//            }
//
//        }
//        }
        binding.addUserBtn.setOnClickListener {


            //navigate user to add
//            val intent = Intent(this,AddNote::class.java)
//            getContent.launch(intent)
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false;
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                if (newText != null) {
                    adapter.filerList(newText)
                }
                return true;
            }
        })
    }

    override fun onItemClicked(user: User) {
        TODO("Not yet implemented")
    }

    override fun onLongItemClicked(user: User, cardView: CardView) {
        TODO("Not yet implemented")
    }

    override fun onMenuItemClick(p0: MenuItem?): Boolean {
        TODO("Not yet implemented")
    }
}