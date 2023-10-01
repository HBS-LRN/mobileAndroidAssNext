package com.example.bait2073mobileapplicationdevelopment.screens.disease


import SymptomListViewModelFactory
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.Toast
import androidx.cardview.widget.CardView

import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.adapter.DiseaseCheckerAdapter
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentSymptomsSearchBinding
import com.example.bait2073mobileapplicationdevelopment.entities.Symptom
import com.example.bait2073mobileapplicationdevelopment.screens.disease.SymptomCheckerFragmentDirections
import com.example.bait2073mobileapplicationdevelopment.screens.symptom.symptomList.SymptomListViewModel


class SymptomCheckerFragment : Fragment(), DiseaseCheckerAdapter.SymptomClickListener, PopupMenu.OnMenuItemClickListener {
    private lateinit var binding: FragmentSymptomsSearchBinding
    private lateinit var viewModel: SymptomListViewModel
    private lateinit var adapter: DiseaseCheckerAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSymptomsSearchBinding.inflate(inflater, container, false)
        viewModel =  ViewModelProvider(this, SymptomListViewModelFactory(requireActivity().application)
        ).get(SymptomListViewModel::class.java)

        viewModel.getSymptomListObservable()
            .observe(viewLifecycleOwner, Observer<List<Symptom?>> { symptomListResponse ->
                if (symptomListResponse == null) {
                    Toast.makeText(requireContext(), "no result found...", Toast.LENGTH_LONG).show()
                } else {
                    val symptomList = symptomListResponse.filterNotNull().toMutableList()
                    Log.i("haha", "$symptomList")
                    adapter.setData(symptomList)
                    adapter.notifyDataSetChanged()
                }
            })
        viewModel.getSymptomList()

        binding.lifecycleOwner = viewLifecycleOwner

        val symptomRecyclerView = binding.recyclerView

        symptomRecyclerView.setHasFixedSize(true)
        symptomRecyclerView.layoutManager = LinearLayoutManager(context)
        adapter = DiseaseCheckerAdapter(requireContext(), this)
        symptomRecyclerView.adapter = adapter

        viewModel.symptomListMut.observe(viewLifecycleOwner, Observer { data ->
            adapter.setData(data as List<Symptom>)
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getSymptomList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1000) {
            viewModel.getSymptomList()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onItemClicked(symptom: Symptom) {

        val action =
            SymptomCheckerFragmentDirections.actionDiseaseCheckerFragmentToDiseaseResultFragment(
                symptom.id ?: 0
            )
        this.findNavController().navigate(action)

    }

    override fun onMenuItemClick(p0: MenuItem?): Boolean {
        TODO("Not yet implemented")
    }

}
