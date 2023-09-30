package com.example.bait2073mobileapplicationdevelopment.screens.recipe.recipeList

import RecipeListViewModelFactory
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
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.adapter.RecipeListAdapter
import com.example.bait2073mobileapplicationdevelopment.adapter.UserAdapter
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentRecipeListBinding
import com.example.bait2073mobileapplicationdevelopment.entities.Recipe
import com.example.bait2073mobileapplicationdevelopment.entities.User
import com.example.bait2073mobileapplicationdevelopment.screens.admin.UserList.UserListFragmentDirections
import com.example.bait2073mobileapplicationdevelopment.screens.admin.UserList.UserListViewModel


class RecipeListFragment : Fragment(), RecipeListAdapter.RecipeClickListener, PopupMenu.OnMenuItemClickListener {
    private lateinit var binding: FragmentRecipeListBinding
    lateinit var viewModel: RecipeListViewModel
    lateinit var adapter: RecipeListAdapter
    lateinit var selectedRecipe: Recipe
    private lateinit var dialog: Dialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecipeListBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(
            this,
            RecipeListViewModelFactory(requireActivity().application)
        ).get(RecipeListViewModel::class.java)

        viewModel.getRecipeListObservable().observe(viewLifecycleOwner, Observer<List<Recipe?>> {recipeListResponse ->
            if(recipeListResponse == null) {
                Toast.makeText(requireContext(), "no result found...", Toast.LENGTH_LONG).show()
            } else {
                val recipeList = recipeListResponse.filterNotNull().toMutableList()
                Log.i("haha", "$recipeList")
                adapter.updateList(recipeList)
                adapter.notifyDataSetChanged()
            }
        })
        viewModel.getRecipeList()

        initRecyclerView()
        observeRecipeDeletion()
        searchRecipe()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.recipeListViewModel= viewModel
        binding.addRecipeBtn.setOnClickListener {
            val action =
                RecipeListFragmentDirections.actionRecipeListFragment2ToCreateRecipeFragment(0)
            this.findNavController().navigate(action)
        }

        binding.deleteImg.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Long-press to delete a recipe.",
                Toast.LENGTH_SHORT
            ).show()
        }

        return binding.root
    }

    private fun searchRecipe() {
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
    private fun initRecyclerView() {
        binding.recycleView.setHasFixedSize(true)
        binding.recycleView.layoutManager = StaggeredGridLayoutManager(1, LinearLayout.VERTICAL)
        adapter = RecipeListAdapter(requireContext(), this)
        binding.recycleView.adapter = adapter
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(requestCode == 1000) {
            viewModel.getRecipeList()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onItemClicked(recipe: Recipe) {

        val action =
            RecipeListFragmentDirections.actionRecipeListFragment2ToCreateRecipeFragment(
                recipe.id ?: 0
            )
        this.findNavController().navigate(action)

    }

    override fun onLongItemClicked(recipe:Recipe, cardView: CardView) {
        selectedRecipe = recipe
        popUpDisplay(cardView)
    }

    private fun popUpDisplay(cardView: CardView) {
        val popup = PopupMenu(requireContext(),cardView)
        popup.setOnMenuItemClickListener(this)
        popup.inflate(R.menu.pop_up_menu)
        popup.show()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.delete_note){
            viewModel.deleteRecipe(selectedRecipe)
        }
        return false
    }

    private fun observeRecipeDeletion() {
        viewModel.getDeleteRecipeObservable().observe(viewLifecycleOwner, Observer<Recipe?> { deletedRecipe ->
            if (deletedRecipe == null) {
                Toast.makeText(requireContext(), "Cannot Delete Recipe", Toast.LENGTH_SHORT).show()
            } else {
                showSuccessDialog()
                viewModel.getRecipeList()
            }
        })
    }

    private fun showSuccessDialog(){
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.custom_dialog_success)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation // Setting the animations to dialog

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
        viewModel.getRecipeList()
    }
}