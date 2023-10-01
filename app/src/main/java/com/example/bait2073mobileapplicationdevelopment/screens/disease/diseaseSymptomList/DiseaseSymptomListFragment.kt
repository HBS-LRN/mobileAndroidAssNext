package com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseSymptomList

import DiseaseSymptomListViewModelFactory
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.adapter.DiseaseListAdapter
import com.example.bait2073mobileapplicationdevelopment.adapter.DiseaseSymptomListAdapter
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentDiseasesymptomListBinding
import com.example.bait2073mobileapplicationdevelopment.entities.Disease
import com.example.bait2073mobileapplicationdevelopment.entities.Disease_Symptom
import com.example.bait2073mobileapplicationdevelopment.entities.Symptom
import com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseSymptomList.DiseaseSymptomListFragmentDirections
import com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseList.DiseaseListViewModel
import com.example.bait2073mobileapplicationdevelopment.screens.symptom.symptomList.SymptomListViewModel

class DiseaseSymptomListFragment : Fragment(), DiseaseSymptomListAdapter.DiseaseSymptomClickListener, PopupMenu.OnMenuItemClickListener {
    private lateinit var viewModel: DiseaseSymptomListViewModel
    private lateinit var adapter: DiseaseSymptomListAdapter
    private lateinit var binding: FragmentDiseasesymptomListBinding
    lateinit var selectedDiseaseSymptom: Disease_Symptom
    private lateinit var dialog: Dialog
//    private lateinit var diseaseViewModel: DiseaseListViewModel
//    private lateinit var symptomViewModel: SymptomListViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiseasesymptomListBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, DiseaseSymptomListViewModelFactory(requireActivity().application)
        ).get(DiseaseSymptomListViewModel::class.java)
//        diseaseViewModel = ViewModelProvider(this,
//            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)).get(
//            DiseaseListViewModel::class.java)
//        symptomViewModel = ViewModelProvider(this,
//            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)).get(
//            SymptomListViewModel::class.java)

        // Observe disease symptom list
        viewModel.getDiseaseSymptomListObservable().observe(viewLifecycleOwner, Observer<List<Disease_Symptom?>> { diseaseSymptomListResponse ->
            if (diseaseSymptomListResponse == null) {
                Toast.makeText(requireContext(), "no result found...", Toast.LENGTH_LONG).show()
            } else {
                val diseaseSymptomList = diseaseSymptomListResponse.filterNotNull().toMutableList()
                Log.i("hahaaa", "$diseaseSymptomList")
                adapter.setData(diseaseSymptomList)
                adapter.notifyDataSetChanged()
            }
        })

//        // Observe disease list
//        diseaseViewModel.getDiseaseListObservable().observe(viewLifecycleOwner, Observer<List<Disease?>> { diseaseListResponse ->
//            if (diseaseListResponse != null) {
//                val diseaseList = diseaseListResponse.filterNotNull().toMutableList()
//                Log.i("DiseaseListFragment", "$diseaseList")
//                adapter.setDiseaseData(diseaseList)
//                adapter.notifyDataSetChanged()
//            }
//        })
//
//        // Observe symptom list
//        symptomViewModel.getSymptomListObservable().observe(viewLifecycleOwner, Observer<List<Symptom?>> { symptomListResponse ->
//            if (symptomListResponse != null) {
//                val symptomList = symptomListResponse.filterNotNull().toMutableList()
//                Log.i("SymptomListFragment", "$symptomList")
//                adapter.setSymptomData(symptomList)
//                adapter.notifyDataSetChanged()
//            }
//        })

//        diseaseViewModel.getDiseaseListObservable().observe(viewLifecycleOwner,Observer<List<Disease?>> { diseaseListResponse ->
//
//            val diseaseList = diseaseListResponse.filterNotNull().toMutableList()
//            Log.i("DiseaseListFragment", "$diseaseList")
//
//            symptomViewModel.getSymptomListObservable().observe(viewLifecycleOwner,Observer<List<Symptom?>> { symptomListResponse ->
//                val symptomList = symptomListResponse.filterNotNull().toMutableList()
//                Log.i("SymptomListFragment", "${symptomList}")
//                adapter.setBothData(diseaseList, symptomList)
//                adapter.notifyDataSetChanged()
//
//            })
//        })

        binding.lifecycleOwner = viewLifecycleOwner
        binding.diseaseSymptomListViewModel= viewModel
        adapter = DiseaseSymptomListAdapter(requireContext(), this)
        viewModel.getDiseaseSymptomList()
        val diseaseSymptomRecyclerView = binding.recycleView
        diseaseSymptomRecyclerView.setHasFixedSize(true)
        diseaseSymptomRecyclerView.layoutManager = LinearLayoutManager(context)
        diseaseSymptomRecyclerView.adapter = adapter


        viewModel.diseaseSymptomListMut.observe(viewLifecycleOwner, Observer { data ->
            adapter.setData(data as List<Disease_Symptom>)
        })
//        diseaseViewModel.diseaseListMut.observe(viewLifecycleOwner, Observer { diseaseData ->
//            symptomViewModel.symptomListMut.observe(viewLifecycleOwner, Observer { symptomData ->
//                adapter.setBothData(diseaseData as List<Disease>,symptomData as List<Symptom>)
//            })
//        })

        observeDiseaseSymptomDeletion()
        searchDiseaseSymptom()

        binding.addDiseaseSymptomBtn.setOnClickListener {
            val action =
                DiseaseSymptomListFragmentDirections.actionDiseaseSymptomListFragmentToCreateDiseaseSymptomFragment()
            this.findNavController().navigate(action)
        }
        binding.deleteImg.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Long-press to delete a disease symptom.",
                Toast.LENGTH_SHORT
            ).show()
        }

        return binding.root
    }

    private fun searchDiseaseSymptom() {
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
            viewModel.getDiseaseSymptomList()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun popUpDisplay(cardView: CardView) {

        val popup = PopupMenu(requireContext(),cardView)
        popup.setOnMenuItemClickListener(this)
        popup.inflate(R.menu.pop_up_menu)
        popup.show()
    }

    override fun onItemClicked(diseaseSymptom: Disease_Symptom) {

        val action =
            DiseaseSymptomListFragmentDirections.actionDiseaseSymptomListFragmentToCreateDiseaseSymptomFragment()

        this.findNavController().navigate(action)

    }

    override fun onLongItemClicked(diseaseSymptom: Disease_Symptom, cardView: CardView) {
        selectedDiseaseSymptom = diseaseSymptom
        popUpDisplay(cardView)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.delete_note){
            viewModel.deleteDiseaseSymptom(selectedDiseaseSymptom)
        }
        return false
    }

    private fun observeDiseaseSymptomDeletion() {
        viewModel.getDeleteDiseaseSymptomObservable().observe(viewLifecycleOwner, Observer<Disease_Symptom?> { deletedDiseaseSymptom ->
            if (deletedDiseaseSymptom == null) {
                Toast.makeText(requireContext(), "Cannot Delete Disease Symptom", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show()
                viewModel.getDiseaseSymptomList()

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
        viewModel.getDiseaseSymptomList()
    }
}