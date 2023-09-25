package com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseSymptomForm

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
import com.example.bait2073mobileapplicationdevelopment.adapter.DiseaseSymptomListAdapter
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentDiseasesymptomListBinding
import com.example.bait2073mobileapplicationdevelopment.entities.Disease_Symptom
import com.example.bait2073mobileapplicationdevelopment.database.HealthyLifeDatabase
import com.example.bait2073mobileapplicationdevelopment.viewmodel.DiseaseViewModel

class DiseaseSymptomFragment: Fragment(), DiseaseSymptomListAdapter.DiseaseSymptomClickListener, PopupMenu.OnMenuItemClickListener {

    private lateinit var dialog: Dialog
    private lateinit var binding: FragmentDiseasesymptomListBinding
    private lateinit var database: HealthyLifeDatabase
    lateinit var viewModel: DiseaseViewModel
    lateinit var adapter: DiseaseSymptomListAdapter
    lateinit var selectedDiseaseSymptom: Disease_Symptom

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDiseasesymptomListBinding.inflate(inflater, container, false)
        initUi()
        viewModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)).get(
            DiseaseViewModel::class.java)
        viewModel.allDiseaseSymptoms.observe(viewLifecycleOwner){list->
            list?.let{
                adapter.setData(list)
            }
        }

        database = HealthyLifeDatabase.getDatabase(requireContext())
        return binding.root
    }

    private fun initUi() {
        binding.recycleView.setHasFixedSize(true)
        binding.recycleView.layoutManager = StaggeredGridLayoutManager(1, LinearLayout.VERTICAL)
        adapter = DiseaseSymptomListAdapter(requireContext(), this)
        binding.recycleView.adapter = adapter

        binding.addDiseaseSymptomBtn.setOnClickListener {

        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false;
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    adapter.filterList(newText)
                }
                return true;
            }
        })
    }

    override fun onItemClicked(diseaseSymptom: Disease_Symptom) {
        TODO("Not yet implemented")
    }

    override fun onLongItemClicked(diseaseSymptom: Disease_Symptom, cardView: CardView) {
        TODO("Not yet implemented")
    }

    override fun onMenuItemClick(p0: MenuItem?): Boolean {
        TODO("Not yet implemented")
    }
}
