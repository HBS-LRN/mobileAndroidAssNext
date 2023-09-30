package com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseRecipeList

import DiseaseRecipeListViewModelFactory
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
import com.example.bait2073mobileapplicationdevelopment.adapter.DiseaseRecipeListAdapter
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentDiseaserecipeListBinding
import com.example.bait2073mobileapplicationdevelopment.entities.Disease_Recipe
import com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseRecipeList.DiseaseRecipeListFragmentDirections
import com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseRecipeList.DiseaseRecipeListViewModel

class DiseaseRecipeListFragment : Fragment(), DiseaseRecipeListAdapter.DiseaseRecipeClickListener, PopupMenu.OnMenuItemClickListener {
    private lateinit var viewModel: DiseaseRecipeListViewModel
    private lateinit var adapter: DiseaseRecipeListAdapter
    private lateinit var binding: FragmentDiseaserecipeListBinding
    lateinit var selectedDiseaseRecipe: Disease_Recipe
    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiseaserecipeListBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, DiseaseRecipeListViewModelFactory(requireActivity().application))
            .get(DiseaseRecipeListViewModel::class.java)

        // Observe disease Recipe list
        viewModel.getDiseaseRecipeListObservable().observe(viewLifecycleOwner, Observer<List<Disease_Recipe?>> { diseaseRecipeListResponse ->
            if (diseaseRecipeListResponse == null) {
                Toast.makeText(requireContext(), "no result found...", Toast.LENGTH_LONG).show()
            } else {
                val diseaseRecipeList = diseaseRecipeListResponse.filterNotNull().toMutableList()
                Log.i("hahaaa", "$diseaseRecipeList")
                adapter.setData(diseaseRecipeList)
                adapter.notifyDataSetChanged()
            }
        })


        binding.lifecycleOwner = viewLifecycleOwner
        binding.diseaseRecipeListViewModel= viewModel
        adapter = DiseaseRecipeListAdapter(requireContext(), this)
        viewModel.getDiseaseRecipeList()
        val diseaseRecipeRecyclerView = binding.recycleView
        diseaseRecipeRecyclerView.setHasFixedSize(true)
        diseaseRecipeRecyclerView.layoutManager = LinearLayoutManager(context)
        diseaseRecipeRecyclerView.adapter = adapter


        viewModel.diseaseRecipeListMut.observe(viewLifecycleOwner, Observer { data ->
            adapter.setData(data as List<Disease_Recipe>)
        })

        observeDiseaseRecipeDeletion()
        searchDiseaseRecipe()

        binding.addDiseaseRecipeBtn.setOnClickListener {
            val action =
                DiseaseRecipeListFragmentDirections.actionDiseaseRecipeListFragmentToCreateDiseaseRecipeFragment()
            this.findNavController().navigate(action)
        }
        binding.deleteImg.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Long-press to delete a disease Recipe.",
                Toast.LENGTH_SHORT
            ).show()
        }

        return binding.root
    }

    private fun searchDiseaseRecipe() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
            viewModel.getDiseaseRecipeList()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun popUpDisplay(cardView: CardView) {

        val popup = PopupMenu(requireContext(),cardView)
        popup.setOnMenuItemClickListener(this)
        popup.inflate(R.menu.pop_up_menu)
        popup.show()
    }

    override fun onItemClicked(diseaseRecipe: Disease_Recipe) {

        val action =
            DiseaseRecipeListFragmentDirections.actionDiseaseRecipeListFragmentToCreateDiseaseRecipeFragment()

        this.findNavController().navigate(action)

    }

    override fun onLongItemClicked(diseaseRecipe: Disease_Recipe, cardView: CardView) {
        selectedDiseaseRecipe = diseaseRecipe
        popUpDisplay(cardView)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.delete_note){
            viewModel.deleteDiseaseRecipe(selectedDiseaseRecipe)
        }
        return false
    }

    private fun observeDiseaseRecipeDeletion() {
        viewModel.getDeleteDiseaseRecipeObservable().observe(viewLifecycleOwner, Observer<Disease_Recipe?> { deletedDiseaseRecipe ->
            if (deletedDiseaseRecipe == null) {
                Toast.makeText(requireContext(), "Cannot Delete Disease Recipe", Toast.LENGTH_SHORT).show()
            } else {
                showSuccessDialog()
                viewModel.getDiseaseRecipeList()

            }
        })
    }
    private fun showSuccessDialog() {
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.custom_dialog_success)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        dialog.window?.attributes?.windowAnimations =
            R.style.DialogAnimation

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getDiseaseRecipeList()
    }
}