package com.example.bait2073mobileapplicationdevelopment.screens.disease;

import DiseaseHospitalListViewModelFactory
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.adapter.DiseaseHospitalAdapter
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentDiseaseHospitalBinding
import com.example.bait2073mobileapplicationdevelopment.entities.Disease
import com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseList.DiseaseListViewModel
import com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseHospitalList.DiseaseHospitalListViewModel

class DiseaseHospitalFragment  : Fragment() {
    private lateinit var viewModel: DiseaseHospitalListViewModel
    private lateinit var adapter: DiseaseHospitalAdapter
    private lateinit var binding: FragmentDiseaseHospitalBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiseaseHospitalBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, DiseaseHospitalListViewModelFactory(requireActivity().application))
            .get(DiseaseHospitalListViewModel::class.java)

        val args = DiseaseHospitalFragmentArgs.fromBundle(requireArguments())
        val disease_id = args.diseaseId
        Log.i("lol is this disease id","$disease_id")

        adapter = DiseaseHospitalAdapter(requireContext())
        val isNetworkAvailable = isNetworkAvailable(requireContext())

        if(isNetworkAvailable){
        viewModel.getDiseaseHospitalData(disease_id)
        viewModel.getLoadDiseaseHospitalObservable().observe(viewLifecycleOwner, Observer { diseaseHospitals ->
            Log.i("finddaolemaaaaaa","$diseaseHospitals")
            if (diseaseHospitals != null) {
                // Update the adapter with the fetched data
                adapter.setData(diseaseHospitals)
                adapter.notifyDataSetChanged()
            } else {
                // Handle the case where data is not available
                Log.i("wandndn","$diseaseHospitals")
                Toast.makeText(requireContext(), "No data found...", Toast.LENGTH_LONG).show()
            }
        })
        }else {
            viewModel.diseaseHospitalDataFromRoomDB.observe(viewLifecycleOwner, Observer { roomData ->
                if (roomData != null && roomData.isNotEmpty()) {
                    // Update the adapter with RoomDB data
                    adapter.setRoomData(roomData)
                    adapter.notifyDataSetChanged()
                } else {
                    // Handle the case where RoomDB data is not available
                    Log.i("roomdbdata", "No RoomDB data found...")
                }
            })
        }

        val hospitalRecyclerView = binding.recycleView
        hospitalRecyclerView.setHasFixedSize(true)
        hospitalRecyclerView.layoutManager = LinearLayoutManager(context)
       hospitalRecyclerView.adapter = adapter

        return binding.root
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 1000) {
            viewModel.getDiseaseHospitalList()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getDiseaseHospitalList()
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }


}