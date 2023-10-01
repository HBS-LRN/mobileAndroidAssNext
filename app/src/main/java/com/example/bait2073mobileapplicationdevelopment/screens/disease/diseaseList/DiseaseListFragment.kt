package com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseList

import DiseaseListViewModelFactory
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
import com.example.bait2073mobileapplicationdevelopment.adapter.DiseaseListAdapter
import com.example.bait2073mobileapplicationdevelopment.adapter.SymptomListAdapter
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentDiseaseListBinding
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentSymptomListBinding
import com.example.bait2073mobileapplicationdevelopment.entities.Disease
import com.example.bait2073mobileapplicationdevelopment.entities.Symptom
import com.example.bait2073mobileapplicationdevelopment.screens.symptom.symptomList.SymptomListFragmentDirections



class DiseaseListFragment : Fragment(),DiseaseListAdapter.DiseaseClickListener, PopupMenu.OnMenuItemClickListener {
    private lateinit var viewModel: DiseaseListViewModel
    private lateinit var adapter: DiseaseListAdapter
    private lateinit var binding: FragmentDiseaseListBinding
    lateinit var selectedDisease: Disease
    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiseaseListBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, DiseaseListViewModelFactory(requireActivity().application))
            .get(DiseaseListViewModel::class.java)

        viewModel.getDiseaseListObservable().observe(viewLifecycleOwner, Observer<List<Disease?>> {diseaseListResponse ->
            if(diseaseListResponse == null) {
                Toast.makeText(requireContext(), "no result found...", Toast.LENGTH_LONG).show()
            } else {

                val diseaseList = diseaseListResponse.filterNotNull().toMutableList()
                Log.i("haha", "$diseaseList")
                adapter.setData(diseaseList)
                adapter.notifyDataSetChanged()
            }
        })
        viewModel.getDiseaseList()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.diseaseListViewModel= viewModel

        val diseaseRecyclerView = binding.recycleView

       diseaseRecyclerView.setHasFixedSize(true)
       diseaseRecyclerView.layoutManager = LinearLayoutManager(context)
        adapter = DiseaseListAdapter(requireContext(), this)
        diseaseRecyclerView.adapter = adapter

        viewModel.diseaseListMut.observe(viewLifecycleOwner, Observer { data ->
            adapter.setData(data as List<Disease>)
        })
        observeDiseaseDeletion()
        searchDisease()

        binding.addSelectChoicesBtn.setOnClickListener {
            val action =
                DiseaseListFragmentDirections.actionDiseaseListFragmentToDiseaseChoicesFragment()
            this.findNavController().navigate(action)
        }
        binding.deleteImg.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Long-press to delete a disease.",
                Toast.LENGTH_SHORT
            ).show()
        }

        return binding.root
    }

    private fun searchDisease() {
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
            viewModel.getDiseaseList()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun popUpDisplay(cardView: CardView) {

        val popup = PopupMenu(requireContext(),cardView)
        popup.setOnMenuItemClickListener(this)
        popup.inflate(R.menu.pop_up_menu)
        popup.show()
    }

    override fun onItemClicked(disease: Disease) {

        val action =
            DiseaseListFragmentDirections.actionDiseaseListFragmentToCreateDiseaseFragment(
                disease.id ?: 0
            )
        this.findNavController().navigate(action)

    }

    override fun onLongItemClicked(disease:Disease, cardView: CardView) {
        selectedDisease = disease
        popUpDisplay(cardView)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.delete_note){
            viewModel.deleteDisease(selectedDisease)
        }
        return false
    }

    private fun observeDiseaseDeletion() {
        viewModel.getDeleteDiseaseObservable().observe(viewLifecycleOwner, Observer<Disease?> { deletedDisease ->
            if (deletedDisease == null) {
                Toast.makeText(requireContext(), "Cannot Delete Disease", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show()
                viewModel.getDiseaseList()

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
        dialog.setCancelable(false) // Optional
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        dialog.window?.attributes?.windowAnimations =
            R.style.DialogAnimation // Setting the animations to dialog

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
        viewModel.getDiseaseList()
    }
}