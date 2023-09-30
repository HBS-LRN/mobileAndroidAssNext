package com.example.bait2073mobileapplicationdevelopment.screens.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.adapter.UserEventListAdapter
import com.example.bait2073mobileapplicationdevelopment.adapter.UserEventPartListAdapter
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentEventBinding
import com.example.bait2073mobileapplicationdevelopment.entities.Event
import com.example.bait2073mobileapplicationdevelopment.entities.EventParticipants
import com.example.bait2073mobileapplicationdevelopment.screens.admin.AdminForm.AdminFormViewModel
import com.example.bait2073mobileapplicationdevelopment.screens.admin.AdminForm.AdminFormViewModelFactory
import com.example.bait2073mobileapplicationdevelopment.screens.admin.EventForm.EventFormViewModelFactory
import com.example.bait2073mobileapplicationdevelopment.screens.admin.EventList.EventListViewModelFactory
import com.example.bait2073mobileapplicationdevelopment.screens.event.EventForm.EventFormViewModel
import com.example.bait2073mobileapplicationdevelopment.screens.event.EventList.EventListFragmentDirections
import com.example.bait2073mobileapplicationdevelopment.screens.event.EventList.EventListViewModel
import com.example.bait2073mobileapplicationdevelopment.screens.event.EventParticipants.EventParticipantsViewModelFactory
import com.example.bait2073mobileapplicationdevelopment.screens.eventParticipants.EventParticipantsParticipants.EventParticipantsViewModel


class EventFragment: Fragment(), UserEventListAdapter.EventClickListerner, PopupMenu.OnMenuItemClickListener {

    lateinit var recyclerViewAdapter: UserEventListAdapter
    lateinit var recyclerViewAdapterPart: UserEventPartListAdapter

//    lateinit var viewModel: EventListViewModel
    lateinit var viewModelPart: EventParticipantsViewModel

//    lateinit var api: GetEventDataService
//    lateinit var db: EventDatabase

    private lateinit var binding: FragmentEventBinding
    lateinit var selectedEvent : Event
    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentEventBinding.inflate(inflater, container, false)
        initViewModel()
        initRecyclerView()
//        observeEventDeletion()
        searchEvent()

//        binding.addEventBtn.setOnClickListener {
//            val action =
//                EventListFragmentDirections.actionEventListFragmentToManageEventFragment(0)
//            this.findNavController().navigate(action)
//        }

        binding.apply {
            recycleViewEventListUser.apply {
                adapter = recyclerViewAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }


            recycleViewEventPartListUser.apply {
                adapter = recyclerViewAdapterPart
                layoutManager = LinearLayoutManager(requireContext())
            }


            return binding.root
        }
    }

    private fun searchEvent(){
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?) :Boolean{
                return false
            }

            override fun onQueryTextChange(newText:String?):Boolean{
                if(newText!=null){
                    recyclerViewAdapterPart.filterList(newText)
                    recyclerViewAdapter.filterList(newText)
                }
                return true
            }
        })
    }

    private fun initRecyclerView(){
        binding.recycleViewEventListUser.setHasFixedSize(true)
        binding.recycleViewEventListUser.layoutManager = StaggeredGridLayoutManager(1, LinearLayout.VERTICAL)
        recyclerViewAdapter = UserEventListAdapter(requireContext(),this,this)
        binding.recycleViewEventListUser.adapter = recyclerViewAdapter


        recyclerViewAdapterPart = UserEventPartListAdapter(requireContext(),this,this)
        binding.recycleViewEventPartListUser.adapter = recyclerViewAdapterPart
    }

    fun initViewModel(){
        val userData = retrieveUserDataFromSharedPreferences(requireContext())
        val userId = userData?.first
//        viewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application))
//            .get(EventListViewModel::class.java)

//        viewModel = ViewModelProvider(
//            this,
//            EventListViewModelFactory(requireActivity().application)
//        ).get(EventListViewModel::class.java)
//
//
//
//
//        viewModel.getEventListObserverable().observe(viewLifecycleOwner,Observer<List<Event?>>{
//                eventListResponse->
//            if (eventListResponse==null){
//                Toast.makeText(requireContext(),"EventListFragment initViewModel() no result found...",Toast.LENGTH_LONG).show()
//            }else{
//                val eventList = eventListResponse.filterNotNull().toMutableList()
//                Log.i("EventListFragment","initViewModel:\n"+"$eventList")
//                recyclerViewAdapter.updateList(eventList)
//                recyclerViewAdapter.notifyDataSetChanged()
//            }
//        })



//        viewModelPart = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application))
//            .get(EventParticipantsViewModel::class.java)


        viewModelPart = ViewModelProvider(
            this,
            EventParticipantsViewModelFactory(requireActivity().application)
        ).get(EventParticipantsViewModel::class.java)


        viewModelPart.getEventListObserverable().observe(viewLifecycleOwner,Observer<List<EventParticipants?>?>{
                eventListResponse->
            if (eventListResponse==null){
//                Toast.makeText(requireContext(),"EventListFragment initViewModel() no result found...",Toast.LENGTH_LONG).show()
            }else{

                val eventList = eventListResponse.filterNotNull().toMutableList()
                Log.i("viewModelPart","initViewModel:\n"+"$eventList")
                if (eventList.isNotEmpty()) {
                    Log.i("viewModelPart", "initViewModel:\n" + "$eventList")
                    recyclerViewAdapterPart.updateList(eventList)
                    recyclerViewAdapterPart.notifyDataSetChanged()
                } else {
                    // Handle the case where eventList is empty
                    // You can display a message or perform some other action
                }
//                recyclerViewAdapterPart.updateList(eventList)
//                recyclerViewAdapterPart.notifyDataSetChanged()
            }
        })

        viewModelPart.getEventListEventPartObserverable().observe(viewLifecycleOwner,Observer<List<Event?>?>{
                eventListEventPartResponse->
            if (eventListEventPartResponse==null){
//                Toast.makeText(requireContext(),"EventListFragment initViewModel() no result found...",Toast.LENGTH_LONG).show()
            }else{

                val eventList = eventListEventPartResponse.filterNotNull().toMutableList()
                Log.i("viewModelPart","initViewModel:\n"+"$eventList")
                if (eventList.isNotEmpty()) {
                    Log.i("viewModelPart", "initViewModel:\n" + "$eventList")
                    recyclerViewAdapter.updateList(eventList)
                    recyclerViewAdapter.notifyDataSetChanged()
                }
            }
        })


        viewModelPart.getEventsPart(userId)
        viewModelPart.getEventsHaventPart(userId)
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
        if(requestCode==1000){
            val userData = retrieveUserDataFromSharedPreferences(requireContext())
            val userId = userData?.first
            viewModelPart.getEventsHaventPart(userId)
            viewModelPart.getEventsPart(userId)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }



    override fun onItemClicked(event: Event) {
        val action=EventListFragmentDirections.actionEventListFragmentToManageEventFragment(event.id?:0,false)
        this.findNavController().navigate(action)
    }

    override fun onLongItemClicked(event: Event, cardView: CardView) {
        selectedEvent=event
        popUpDisplay(cardView)
    }

    private fun popUpDisplay(cardView:CardView){
        val popup = PopupMenu(requireContext(),cardView)
        popup.setOnMenuItemClickListener(this)
        popup.inflate(R.menu.pop_up_menu)
        popup.show()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.delete_note){

        }
        return false
    }

//    private fun observeEventDeletion(){
//        viewModel.getDeleteEventObservable().observe(viewLifecycleOwner,Observer<Event?>{
//                deletedUser ->
//            if(deletedUser == null){
//                Toast.makeText(requireContext(), "Cannot Delete Event", Toast.LENGTH_SHORT).show()
//            }else{
//                showSuccessDialog()
//                viewModel.getEvents()
//            }
//        })
//    }

    private fun showSuccessDialog(){
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.custom_dialog_success)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation

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
        dialog.show()
    }



}

