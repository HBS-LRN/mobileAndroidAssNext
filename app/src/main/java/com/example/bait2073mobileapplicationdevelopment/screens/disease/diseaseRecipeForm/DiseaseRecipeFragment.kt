package com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseRecipeForm

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
import com.example.bait2073mobileapplicationdevelopment.adapter.DiseaseRecipeListAdapter
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentDiseaserecipeListBinding
import com.example.bait2073mobileapplicationdevelopment.entities.Disease_Recipe
import com.example.bait2073mobileapplicationdevelopment.database.HealthyLifeDatabase
import com.example.bait2073mobileapplicationdevelopment.viewmodel.DiseaseViewModel

class DiseaseRecipeFragment : Fragment(), DiseaseRecipeListAdapter.DiseaseRecipeClickListener, PopupMenu.OnMenuItemClickListener {

    private lateinit var dialog: Dialog
    private lateinit var binding: FragmentDiseaserecipeListBinding
    private lateinit var database: HealthyLifeDatabase
    lateinit var viewModel: DiseaseViewModel
    lateinit var adapter: DiseaseRecipeListAdapter
    lateinit var selectedDiseaseRecipe: Disease_Recipe

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDiseaserecipeListBinding.inflate(inflater, container, false)
        initUi()
        viewModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)).get(
            DiseaseViewModel::class.java)
        viewModel.allDiseaseRecipes.observe(viewLifecycleOwner){list->
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
        adapter = DiseaseRecipeListAdapter(requireContext(), this)
        binding.recycleView.adapter = adapter

        binding.addDiseaseRecipeBtn.setOnClickListener {

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

    override fun onItemClicked(diseaseRecipe: Disease_Recipe) {
        TODO("Not yet implemented")
    }

    override fun onLongItemClicked(diseaseRecipe: Disease_Recipe, cardView: CardView) {
        TODO("Not yet implemented")
    }

    override fun onMenuItemClick(p0: MenuItem?): Boolean {
        TODO("Not yet implemented")
    }
}
