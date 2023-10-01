package com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseHospitalForm

import DiseaseHospitalFormViewModelFactory
import DiseaseListViewModelFactory
import HospitalListViewModelFactory
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
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentDiseasehospitalFormBinding
import com.example.bait2073mobileapplicationdevelopment.entities.DiseaseHospital_Room
import com.example.bait2073mobileapplicationdevelopment.entities.Disease_Hospital
import com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseList.DiseaseListViewModel
import com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseHospitalForm.DiseaseHospitalFormFragmentDirections
import com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseHospitalForm.DiseaseHospitalFormViewModel
import com.example.bait2073mobileapplicationdevelopment.screens.hospital.hospitalList.HospitalListViewModel
import com.google.android.material.textfield.TextInputLayout

class DiseaseHospitalFormFragment : Fragment() {

    private lateinit var viewModel: DiseaseHospitalFormViewModel
    private lateinit var diseaseViewModel: DiseaseListViewModel
    private lateinit var hospitalViewModel: HospitalListViewModel
    private lateinit var binding: FragmentDiseasehospitalFormBinding
    private lateinit var dialog: Dialog
    private var selectedDiseaseId: Int? = null
    private var selectedDiseaseName: String = ""
    private var selectedHospitalId: Int? = null
    private  var selectedHospitalName: String = ""
    private  var selectedHospitalContact: String =""
    private  var selectedHospitalAddress: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDiseasehospitalFormBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, DiseaseHospitalFormViewModelFactory(requireActivity().application))
            .get(DiseaseHospitalFormViewModel::class.java)
        createDiseaseHospitalObservable()
        diseaseViewModel =
            ViewModelProvider(this, DiseaseListViewModelFactory(requireActivity().application))
                .get(DiseaseListViewModel::class.java)


        hospitalViewModel = ViewModelProvider(
            this,
            HospitalListViewModelFactory(requireActivity().application)
        ).get(HospitalListViewModel::class.java)

        val autoCompleteDiseaseTextView = binding.autoCompleteDiseaseTextView
        val autoCompleteHospitalTextView = binding.autoCompleteHospitalTextView

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
                    selectedDiseaseName = selectedDisease?.disease_name.toString()

                }
            }
        })
        hospitalViewModel.hospitalListMut.observe(viewLifecycleOwner, Observer { hospitals ->
            Log.i("Hospital list", "$hospitals")
            if (hospitals != null) {
                val adapter = ArrayAdapter(
                    requireContext(),
                    R.layout.simple_dropdown_item_1line,
                    hospitals.map { it?.hospital_name })
                autoCompleteHospitalTextView.setAdapter(adapter)
                // Set an item click listener to get the selected Hospital and its ID
                autoCompleteHospitalTextView.setOnItemClickListener { _, _, position, _ ->
                    val selectedHospital = hospitals[position]
                    selectedHospitalId = selectedHospital?.id
                    selectedHospitalName = selectedHospital?.hospital_name.toString()
                    selectedHospitalContact = selectedHospital?.hospital_contact.toString()
                    selectedHospitalAddress = selectedHospital?.hospital_address.toString()
                }
            }
        })

        // Fetch diseases and Hospital data from the ViewModel
        diseaseViewModel.getDiseaseList()
        hospitalViewModel.getHospitalList()

        binding.createDiseaseHospitalBtn.setOnClickListener {
            if (validateForm()) {
                createDiseaseHospital(0)
                createDiseaseHospitalRoom(0)    //
            }
        }

        return binding.root
    }

    private fun createDiseaseHospital(diseaseHospital_id: Int?) {
        Log.i("selectedDiseaseid", "{$selectedDiseaseId}")
        Log.i("selectedHospitalid", "{$selectedHospitalId}")

        val diseaseHospital =
            selectedDiseaseId?.let {
                selectedHospitalId?.let { it1 ->
                    Disease_Hospital(
                        null,
                        it,
                        it1,
                    )
                }
            }
        Log.i("createddiseasehospital", "{$diseaseHospital}")
        if (diseaseHospital_id == 0) {
            if (diseaseHospital != null) {
                viewModel.createDiseaseHospital(diseaseHospital)
            }
        }
    }

    private fun createDiseaseHospitalRoom(diseaseHospital_id:Int?){
            val diseaseHospitalRoom = selectedHospitalId?.let {
                selectedDiseaseId?.let { it1 ->
                    DiseaseHospital_Room(
                        null,
                        it1,
                        selectedDiseaseName,
                        it,
                        selectedHospitalName,
                        selectedHospitalContact,
                        selectedHospitalAddress,

                    )
                }
            }
        Log.i("createddiseasehospitalroom", "{$diseaseHospitalRoom}")
        if (diseaseHospital_id == 0) {
            if (diseaseHospitalRoom != null) {
                viewModel.insertDiseaseHospitalRoomDataIntoRoomDb(diseaseHospitalRoom)
            }
        }
    }


    private fun createDiseaseHospitalObservable() {
        viewModel.getCreateDiseaseHospitalObservable()
            .observe(viewLifecycleOwner, Observer<Disease_Hospital?> {
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
                DiseaseHospitalFormFragmentDirections.actionCreateDiseaseHospitalFragmentToDiseaseHospitalListFragment()
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

    private fun validHospitalName(autoCompleteHospitalTextView: AutoCompleteTextView): String? {
        val selectedHospital = autoCompleteHospitalTextView.text.toString()
        if (selectedHospital.isEmpty()) {
            return "Hospital Name Is Required"
        }
        return null
    }

    private fun validateForm(): Boolean {

        val diseaseName = binding.autoCompleteDiseaseTextView
        val hospitalName = binding.autoCompleteHospitalTextView

        val layoutDiseaseName: TextInputLayout = binding.layoutDiseasesName
        val layoutHospitalName: TextInputLayout = binding.layoutHospitalsName

        val diseaseNameError = validDiseaseName(diseaseName)
        val hospitalNameError = validHospitalName(hospitalName)

        var isValidate = true

        if (diseaseNameError != null) {
            layoutDiseaseName.error = diseaseNameError
            isValidate = false
        }
        if (hospitalNameError != null) {
            layoutHospitalName.error = hospitalNameError
            isValidate = false
        }
        return isValidate
    }
}