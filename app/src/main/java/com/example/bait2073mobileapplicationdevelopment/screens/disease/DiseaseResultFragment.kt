package com.example.bait2073mobileapplicationdevelopment.screens.disease

import DiseaseSymptomListViewModelFactory
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
import com.example.bait2073mobileapplicationdevelopment.adapter.DiseaseResultAdapter
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentDiseaseResultBinding
import com.example.bait2073mobileapplicationdevelopment.entities.Disease_Symptom
import com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseList.DiseaseListViewModel
import com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseSymptomList.DiseaseSymptomListViewModel

class DiseaseResultFragment : Fragment() {
    private lateinit var viewModel: DiseaseSymptomListViewModel
    private lateinit var adapter: DiseaseResultAdapter
    private lateinit var binding: FragmentDiseaseResultBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiseaseResultBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, DiseaseSymptomListViewModelFactory(requireActivity().application)
        ).get(DiseaseSymptomListViewModel::class.java)

        val args = DiseaseResultFragmentArgs.fromBundle(requireArguments())
        val symptom_id = args.symptomId
        Log.i("lol is this symptom id","$symptom_id")

        adapter = DiseaseResultAdapter(requireContext())

        viewModel.getSymptomDiseaseData(symptom_id)
        viewModel.getLoadSymptomDiseaseObservable().observe(viewLifecycleOwner, Observer { symptomDiseases ->
            Log.i("findzzzzzlemaaaaaa","$symptomDiseases")
            if (symptomDiseases != null) {
                // Update the adapter with the fetched data
                adapter.setData(symptomDiseases)
                adapter.notifyDataSetChanged()
            } else {
                // Handle the case where data is not available
                Log.i("wandndn","$symptomDiseases")
                Toast.makeText(requireContext(), "No data found...", Toast.LENGTH_LONG).show()
            }
        })

        val diseaseResultRecyclerView = binding.recyclerView
        diseaseResultRecyclerView.setHasFixedSize(true)
        diseaseResultRecyclerView.layoutManager = LinearLayoutManager(context)
        diseaseResultRecyclerView.adapter = adapter

        return binding.root
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 1000) {
            viewModel.getDiseaseSymptomList()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getDiseaseSymptomList()
    }

}