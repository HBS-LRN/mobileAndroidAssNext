package com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseRecipeForm

import DiseaseListViewModelFactory
import DiseaseRecipeFormViewModelFactory
import RecipeListViewModelFactory
import android.R
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentDiseaserecipeFormBinding
import com.example.bait2073mobileapplicationdevelopment.entities.Disease_Recipe
import com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseList.DiseaseListViewModel
import com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseRecipeForm.DiseaseRecipeFormFragmentDirections
import com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseSymptomForm.DiseaseSymptomFormViewModel
import com.example.bait2073mobileapplicationdevelopment.screens.recipe.recipeList.RecipeListViewModel
import com.google.android.material.textfield.TextInputLayout

class DiseaseRecipeFormFragment : Fragment() {

    private lateinit var viewModel: DiseaseRecipeFormViewModel
    private lateinit var diseaseViewModel: DiseaseListViewModel
    private lateinit var recipeViewModel: RecipeListViewModel
    private lateinit var binding: FragmentDiseaserecipeFormBinding
    private lateinit var dialog: Dialog
    private var selectedDiseaseId: Int? = null
    private var selectedRecipeId: Int? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDiseaserecipeFormBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, DiseaseRecipeFormViewModelFactory())
            .get(DiseaseRecipeFormViewModel::class.java)
        createDiseaseRecipeObservable()

        diseaseViewModel = ViewModelProvider(this, DiseaseListViewModelFactory(requireActivity().application))
            .get(DiseaseListViewModel::class.java)


        recipeViewModel = ViewModelProvider(
            this,
            RecipeListViewModelFactory(requireActivity().application)
        ).get(RecipeListViewModel::class.java)

        val autoCompleteDiseaseTextView = binding.autoCompleteDiseaseTextView
        val autoCompleteRecipeTextView = binding.autoCompleteRecipeTextView

        diseaseViewModel.diseaseListMut.observe(viewLifecycleOwner, Observer { diseases ->
            Log.i("disease list", "$diseases")
            if (diseases != null) {
                val adapter = ArrayAdapter(
                    requireContext(),
                    R.layout.simple_dropdown_item_1line,
                    diseases.map {
                        it?.disease_name
                    })
                autoCompleteDiseaseTextView.setAdapter(adapter)

                autoCompleteDiseaseTextView.setOnItemClickListener { _, _, position, _ ->
                    val selectedDisease = diseases[position]
                    selectedDiseaseId = selectedDisease?.id

                }
            }
        })
        recipeViewModel.recyclerListData.observe(viewLifecycleOwner, Observer { recipes ->
            Log.i("recipe list", "$recipes")
            if (recipes != null) {
                val adapter = ArrayAdapter(
                    requireContext(),
                    R.layout.simple_dropdown_item_1line,
                    recipes.map { it?.recipe_name })
                autoCompleteRecipeTextView.setAdapter(adapter)

                autoCompleteRecipeTextView.setOnItemClickListener { _, _, position, _ ->
                    val selectedRecipe = recipes[position]
                    selectedRecipeId = selectedRecipe?.id

                }
            }
        })

        diseaseViewModel.getDiseaseList()
        recipeViewModel.getRecipeList()

        binding.createDiseaseRecipeBtn.setOnClickListener {
            if (validateForm()) {
                createDiseaseRecipe(0)
            }
        }

        return binding.root
    }

    private fun createDiseaseRecipe(diseaseRecipe_id: Int?) {
        Log.i("selectedDiseaseid", "{$selectedDiseaseId}")
        Log.i("selectedRecipeid", "{$selectedRecipeId}")

        val diseaseRecipe =
            selectedDiseaseId?.let {
                selectedRecipeId?.let { it1 ->
                    Disease_Recipe(
                        null,
                        it,
                        it1,
                    )
                }
            }
        Log.i("createddiseaseRecipe", "{$diseaseRecipe}")
        if (diseaseRecipe_id == 0) {
            if (diseaseRecipe != null) {
                viewModel.createDiseaseRecipe(diseaseRecipe)
            }
        }
    }


    private fun createDiseaseRecipeObservable() {
        viewModel.getCreateDiseaseRecipeObservable()
            .observe(viewLifecycleOwner, Observer<Disease_Recipe?> {
                if (it == null) {
                    Log.e("error", "error")
                } else {
                    showSuccessDialog()

                }
            })
    }

    private fun showSuccessDialog() {
        dialog = Dialog(requireContext())
        dialog.setContentView(com.example.bait2073mobileapplicationdevelopment.R.layout.custom_dialog_success)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false) // Optional
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        dialog.window?.attributes?.windowAnimations =
            com.example.bait2073mobileapplicationdevelopment.R.style.DialogAnimation

        val okay: Button =
            dialog.findViewById(com.example.bait2073mobileapplicationdevelopment.R.id.btn_okay)
        val cancel: Button =
            dialog.findViewById(com.example.bait2073mobileapplicationdevelopment.R.id.btn_cancel)

        okay.setOnClickListener {
            dialog.dismiss()
            val action =
                DiseaseRecipeFormFragmentDirections.actionCreateDiseaseRecipeFragmentToDiseaseRecipeListFragment()
            findNavController().navigate(action)
        }

        cancel.setOnClickListener {
            Toast.makeText(requireContext(), "Cancel", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        dialog.show()
    }

    private fun validDiseaseName(autoCompleteDiseaseTextView: AutoCompleteTextView): String? {
        val selectedDisease = autoCompleteDiseaseTextView.text.toString()
        if (selectedDisease.isEmpty()) {
            return "Disease Name Is Required"
        }
        return null
    }

    private fun validRecipeName(autoCompleteRecipeTextView: AutoCompleteTextView): String? {
        val selectedRecipe = autoCompleteRecipeTextView.text.toString()
        if (selectedRecipe.isEmpty()) {
            return "Recipe Name Is Required"
        }
        return null
    }

    private fun validateForm(): Boolean {

        val diseaseName = binding.autoCompleteDiseaseTextView
        val recipeName = binding.autoCompleteRecipeTextView

        val layoutDiseaseName: TextInputLayout = binding.layoutDiseasesName
        val layoutRecipeName: TextInputLayout = binding.layoutRecipesName

        val diseaseNameError = validDiseaseName(diseaseName)
        val recipeNameError = validRecipeName(recipeName)

        var isValidate = true

        if (diseaseNameError != null) {
            layoutDiseaseName.error = diseaseNameError
            isValidate = false
        }
        if (recipeNameError != null) {
            layoutRecipeName.error = recipeNameError
            isValidate = false
        }
        return isValidate
    }
}