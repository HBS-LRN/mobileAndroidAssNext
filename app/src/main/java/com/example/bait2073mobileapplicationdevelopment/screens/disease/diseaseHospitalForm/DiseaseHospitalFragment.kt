package com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseHospitalForm

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
import com.example.bait2073mobileapplicationdevelopment.adapter.DiseaseHospitalListAdapter
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentDiseasehospitalListBinding
import com.example.bait2073mobileapplicationdevelopment.entities.Disease_Hospital
import com.example.bait2073mobileapplicationdevelopment.database.HealthyLifeDatabase
import com.example.bait2073mobileapplicationdevelopment.viewmodel.DiseaseViewModel

class DiseaseHospitalFragment : Fragment(), DiseaseHospitalListAdapter.DiseaseHospitalClickListener, PopupMenu.OnMenuItemClickListener {

    private lateinit var dialog: Dialog
    private lateinit var binding: FragmentDiseasehospitalListBinding
    private lateinit var database: HealthyLifeDatabase
    lateinit var viewModel: DiseaseViewModel
    lateinit var adapter: DiseaseHospitalListAdapter
    lateinit var selectedDiseaseHospital: Disease_Hospital

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDiseasehospitalListBinding.inflate(inflater, container, false)
        initUi()
        viewModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)).get(
            DiseaseViewModel::class.java)
        viewModel.allDiseaseHospitals.observe(viewLifecycleOwner){list->
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
        adapter = DiseaseHospitalListAdapter(requireContext(), this)
        binding.recycleView.adapter = adapter

        binding.addDiseaseHospitalBtn.setOnClickListener {

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

    override fun onItemClicked(diseaseHospital: Disease_Hospital) {
        TODO("Not yet implemented")
    }

    override fun onLongItemClicked(diseaseHospital: Disease_Hospital, cardView: CardView) {
        TODO("Not yet implemented")
    }

    override fun onMenuItemClick(p0: MenuItem?): Boolean {
        TODO("Not yet implemented")
    }
}
