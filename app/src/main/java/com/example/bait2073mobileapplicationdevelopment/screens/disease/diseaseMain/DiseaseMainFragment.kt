package com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseMain

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.adapter.DiseaseMainAdapter
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentDiseasesPreventionBinding
import com.example.bait2073mobileapplicationdevelopment.entities.Disease
import com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseList.DiseaseListViewModel

class DiseaseMainFragment : Fragment(), DiseaseMainAdapter.DiseaseMainClickListener {
    private lateinit var viewModel: DiseaseListViewModel
    private lateinit var adapter: DiseaseMainAdapter
    private lateinit var binding: FragmentDiseasesPreventionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiseasesPreventionBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)).get(
            DiseaseListViewModel::class.java)
        val isNetworkAvailable = isNetworkAvailable(requireContext())

        if(isNetworkAvailable) {
            viewModel.getDiseaseList()
            viewModel.getDiseaseListObservable()
                .observe(viewLifecycleOwner, Observer<List<Disease?>> { diseaseListResponse ->
                    if (diseaseListResponse == null) {
                        Toast.makeText(requireContext(), "no result found...", Toast.LENGTH_LONG)
                            .show()
                    } else {
                        val diseaseList = diseaseListResponse.filterNotNull().toMutableList()
                        Log.i("haha", "$diseaseList")
                        viewModel.removeDiseaseFromLocalDatabase()
                        viewModel.insertDiseaseDataIntoRoomDb(diseaseList)
                        adapter.setData(diseaseList)
                        adapter.notifyDataSetChanged()
                    }
                })
        }else{
            viewModel.diseaseListDataDao.observe(viewLifecycleOwner, Observer { roomData ->
                if (roomData != null && roomData.isNotEmpty()) {
                    // Update the adapter with RoomDB data
                    adapter.setData(roomData)
                    adapter.notifyDataSetChanged()
                } else {
                    // Handle the case where RoomDB data is not available
                    Log.i("roomdbdata", "No RoomDB data found...")
                }
        })
        }

        val diseaseRecyclerView = binding.recyclerView3
        diseaseRecyclerView.setHasFixedSize(true)
        diseaseRecyclerView.layoutManager = LinearLayoutManager(context)
        adapter = DiseaseMainAdapter(requireContext(), this)
        diseaseRecyclerView.adapter = adapter

        viewModel.diseaseListMut.observe(viewLifecycleOwner, Observer { data ->
            adapter.setData(data as List<Disease>)
        })


        searchDisease()

        binding.feature1.setOnClickListener {
            val action =
                DiseaseMainFragmentDirections.actionDiseaseFragmentToDiseaseCheckerFragment() //to symptom checker
            this.findNavController().navigate(action)
        }

        return binding.root
    }

    private fun searchDisease() {
        binding.diseaseSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false;
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    adapter.filterList(newText)
                }
                return true
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 1000) {
            viewModel.getDiseaseList()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onItemClicked(disease: Disease) {

        val action =
            DiseaseMainFragmentDirections.actionDiseaseFragmentToDiseaseDetailFragment(
                disease.id ?:0,disease.disease_name
            )
        this.findNavController().navigate(action)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getDiseaseList()
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}