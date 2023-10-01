package com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseHospitalList

import DiseaseHospitalListViewModelFactory
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
import com.example.bait2073mobileapplicationdevelopment.adapter.DiseaseHospitalListAdapter
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentDiseasehospitalListBinding
import com.example.bait2073mobileapplicationdevelopment.entities.Disease_Hospital
import com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseHospitalList.DiseaseHospitalListFragmentDirections
import com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseHospitalList.DiseaseHospitalListViewModel

class DiseaseHospitalListFragment : Fragment(), DiseaseHospitalListAdapter.DiseaseHospitalClickListener, PopupMenu.OnMenuItemClickListener {
    private lateinit var viewModel: DiseaseHospitalListViewModel
    private lateinit var adapter: DiseaseHospitalListAdapter
    private lateinit var binding: FragmentDiseasehospitalListBinding
    lateinit var selectedDiseaseHospital: Disease_Hospital
    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiseasehospitalListBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, DiseaseHospitalListViewModelFactory(requireActivity().application))
            .get(DiseaseHospitalListViewModel::class.java)

        // Observe disease Hospital list
        viewModel.getDiseaseHospitalListObservable().observe(viewLifecycleOwner, Observer<List<Disease_Hospital?>> { diseaseHospitalListResponse ->
            if (diseaseHospitalListResponse == null) {
                Toast.makeText(requireContext(), "no result found...", Toast.LENGTH_LONG).show()
            } else {
                val diseaseHospitalList = diseaseHospitalListResponse.filterNotNull().toMutableList()
                Log.i("hahaaa", "$diseaseHospitalList")
                adapter.setData(diseaseHospitalList)
                adapter.notifyDataSetChanged()
            }
        })


        binding.lifecycleOwner = viewLifecycleOwner
        binding.diseaseHospitalListViewModel= viewModel
        adapter = DiseaseHospitalListAdapter(requireContext(), this)
        viewModel.getDiseaseHospitalList()
        val diseaseHospitalRecyclerView = binding.recycleView
        diseaseHospitalRecyclerView.setHasFixedSize(true)
        diseaseHospitalRecyclerView.layoutManager = LinearLayoutManager(context)
        diseaseHospitalRecyclerView.adapter = adapter


        viewModel.diseaseHospitalListMut.observe(viewLifecycleOwner, Observer { data ->
            adapter.setData(data as List<Disease_Hospital>)
        })

        observeDiseaseHospitalDeletion()
        searchDiseaseHospital()

        binding.addDiseaseHospitalBtn.setOnClickListener {
            val action =
                DiseaseHospitalListFragmentDirections.actionDiseaseHospitalListFragmentToCreateDiseaseHospitalFragment()
            this.findNavController().navigate(action)
        }
        binding.deleteImg.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Long-press to delete a disease Hospital.",
                Toast.LENGTH_SHORT
            ).show()
        }

        return binding.root
    }

    private fun searchDiseaseHospital() {
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
            viewModel.getDiseaseHospitalList()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun popUpDisplay(cardView: CardView) {

        val popup = PopupMenu(requireContext(),cardView)
        popup.setOnMenuItemClickListener(this)
        popup.inflate(R.menu.pop_up_menu)
        popup.show()
    }

    override fun onItemClicked(diseaseHospital: Disease_Hospital) {

        val action =
            DiseaseHospitalListFragmentDirections.actionDiseaseHospitalListFragmentToCreateDiseaseHospitalFragment()

        this.findNavController().navigate(action)

    }

    override fun onLongItemClicked(diseaseHospital: Disease_Hospital, cardView: CardView) {
        selectedDiseaseHospital = diseaseHospital
        popUpDisplay(cardView)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.delete_note){
            viewModel.deleteDiseaseHospital(selectedDiseaseHospital)
        }
        return false
    }

    private fun observeDiseaseHospitalDeletion() {
        viewModel.getDeleteDiseaseHospitalObservable().observe(viewLifecycleOwner, Observer<Disease_Hospital?> { deletedDiseaseHospital ->
            if (deletedDiseaseHospital == null) {
                Toast.makeText(requireContext(), "Cannot Delete Disease Hospital", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show()
                viewModel.getDiseaseHospitalList()

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
        viewModel.getDiseaseHospitalList()
    }
}