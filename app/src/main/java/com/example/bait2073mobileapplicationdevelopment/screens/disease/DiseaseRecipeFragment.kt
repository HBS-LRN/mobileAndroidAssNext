package com.example.bait2073mobileapplicationdevelopment.screens.disease;

import DiseaseRecipeListViewModelFactory
import RecipeListViewModelFactory
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.adapter.DiseaseRecipeAdapter
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentDiseaseRecipeBinding
import com.example.bait2073mobileapplicationdevelopment.entities.Disease
import com.example.bait2073mobileapplicationdevelopment.entities.DiseaseRecipe_Room
import com.example.bait2073mobileapplicationdevelopment.entities.DiseaseSymptom_Room
import com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseList.DiseaseListViewModel
import com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseRecipeList.DiseaseRecipeListViewModel
import com.example.bait2073mobileapplicationdevelopment.screens.recipe.recipeList.RecipeListViewModel
import com.example.bait2073mobileapplicationdevelopment.screens.symptom.symptomList.SymptomListViewModel

class DiseaseRecipeFragment: Fragment() {
    private lateinit var viewModel: DiseaseRecipeListViewModel
    private lateinit var recipeViewModel: RecipeListViewModel
    private lateinit var adapter: DiseaseRecipeAdapter
    private lateinit var binding: FragmentDiseaseRecipeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiseaseRecipeBinding.inflate(inflater, container, false)
        viewModel =  ViewModelProvider(this, DiseaseRecipeListViewModelFactory(requireActivity().application))
            .get(DiseaseRecipeListViewModel::class.java)
        recipeViewModel = ViewModelProvider(this, RecipeListViewModelFactory(requireActivity().application))
            .get(RecipeListViewModel::class.java)
        val args = DiseaseRecipeFragmentArgs.fromBundle(requireArguments())
        val disease_id = args.diseaseId
        val disease_name = args.diseaseName
        Log.i("lol is this disease id","$disease_id")
        adapter = DiseaseRecipeAdapter(requireContext())

        val isNetworkAvailable = isNetworkAvailable(requireContext())

        if(isNetworkAvailable) {
        viewModel.getDiseaseRecipeData(disease_id)
            viewModel.removeDiseaseRecipeRoomFromLocalDatabase()

        viewModel.getLoadDiseaseRecipeObservable().observe(viewLifecycleOwner, Observer { diseaseRecipes ->
            Log.i("finddaolemaaaaaa","$diseaseRecipes")
            if (diseaseRecipes != null) {
                for (diseaseRecipe in diseaseRecipes) {
                    Log.i("specificrecipeid??","${diseaseRecipe.recipe_id}")
                    recipeViewModel.getSpecificRecipe(diseaseRecipe.recipe_id).observe(viewLifecycleOwner, Observer { specificRecipe ->
                        Log.i("specificrecipe??", "$specificRecipe")
                        val recipeId = specificRecipe?.id
                        val recipeName = specificRecipe?.recipe_name
                        val recipeImg = specificRecipe?.recipe_image
                        val recipeDesc = specificRecipe?.recipe_description
                        val recipeServings = specificRecipe?.recipe_servings

                        recipeId?.let {
                            if (recipeName != null) {
                                val diseaseRecipeRoom = DiseaseRecipe_Room(
                                    null,
                                    disease_id,
                                    disease_name,
                                    it,
                                    recipeName,
                                    recipeImg,
                                    recipeDesc,
                                    recipeServings,
                                )
                                Log.i("finddaol??", "$diseaseRecipeRoom")
                                viewModel.insertDiseaseRecipeRoomDataIntoRoomDb(diseaseRecipeRoom)
                            }

                        }
                    })
                }
                adapter.setData(diseaseRecipes)
                adapter.notifyDataSetChanged()
            } else {
                // Handle the case where data is not available
                Log.i("wandndn","$diseaseRecipes")
                Toast.makeText(requireContext(), "No data found...", Toast.LENGTH_LONG).show()
            }
        })}else{
            viewModel.diseaseRecipeDataFromRoomDB.observe(viewLifecycleOwner, Observer { roomData ->
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


        val recipeRecyclerView = binding.recycleRecipe
       recipeRecyclerView.setHasFixedSize(true)
        recipeRecyclerView.layoutManager = GridLayoutManager(requireContext(),2)
        recipeRecyclerView.adapter = adapter

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 1000) {
            viewModel.getDiseaseRecipeList()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getDiseaseRecipeList()
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}