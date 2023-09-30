package com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseSymptomForm

import DiseaseHospitalFormViewModelFactory
import DiseaseListViewModelFactory
import DiseaseSymptomFormViewModelFactory
import SymptomListViewModelFactory
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
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
import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentDiseasesymptomFormBinding
import com.example.bait2073mobileapplicationdevelopment.entities.Disease
import com.example.bait2073mobileapplicationdevelopment.entities.Disease_Symptom
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetDiseaseDataService
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetDiseaseSymptomDataService
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetSymptomDataService
import com.example.bait2073mobileapplicationdevelopment.retrofitclient.RetrofitClientInstance
import com.example.bait2073mobileapplicationdevelopment.screens.admin.AdminForm.AdminFormViewModel
import com.example.bait2073mobileapplicationdevelopment.screens.admin.AdminForm.AdminFormViewModelFactory
import com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseList.DiseaseListViewModel
import com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseSymptomForm.DiseaseSymptomFormFragmentDirections
import com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseSymptomForm.DiseaseSymptomFormViewModel
import com.example.bait2073mobileapplicationdevelopment.screens.symptom.symptomList.SymptomListViewModel
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class DiseaseSymptomFormFragment : Fragment() {

    private lateinit var viewModel: DiseaseSymptomFormViewModel
    private lateinit var diseaseViewModel: DiseaseListViewModel
    private lateinit var symptomViewModel: SymptomListViewModel
    private lateinit var binding: FragmentDiseasesymptomFormBinding
    private lateinit var dialog: Dialog
    private var selectedDiseaseId: Int? = null
    private var selectedSymptomId: Int? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDiseasesymptomFormBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this,
            DiseaseSymptomFormViewModelFactory()
        ).get(DiseaseSymptomFormViewModel::class.java)

        createDiseaseSymptomObservable()
        diseaseViewModel = ViewModelProvider(this, DiseaseListViewModelFactory(requireActivity().application))
            .get(DiseaseListViewModel::class.java)

       symptomViewModel =  ViewModelProvider(this, SymptomListViewModelFactory(requireActivity().application)
       ).get(SymptomListViewModel::class.java)

        val autoCompleteDiseaseTextView = binding.autoCompleteDiseaseTextView
        val autoCompleteSymptomTextView = binding.autoCompleteSymptomTextView

        diseaseViewModel.diseaseListMut.observe(viewLifecycleOwner, Observer { diseases ->
            Log.i("disease list" , "$diseases" )
            if (diseases != null) {
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, diseases.map { it?.disease_name
                     })
                autoCompleteDiseaseTextView.setAdapter(adapter)

                // Set an item click listener to get the selected disease and its ID
                autoCompleteDiseaseTextView.setOnItemClickListener { _, _, position, _ ->
                    val selectedDisease = diseases[position]
                   selectedDiseaseId = selectedDisease?.id


                }
            }
        })
        symptomViewModel.symptomListMut.observe(viewLifecycleOwner, Observer { symptoms ->
            Log.i("symptom list" , "$symptoms" )
            if (symptoms != null) {
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, symptoms.map { it?.symptom_name })
                autoCompleteSymptomTextView.setAdapter(adapter)
                // Set an item click listener to get the selected symptom and its ID
                autoCompleteSymptomTextView.setOnItemClickListener { _, _, position, _ ->
                    val selectedSymptom = symptoms[position]
                   selectedSymptomId = selectedSymptom?.id

                }
            }
        })


        // Fetch diseases and symptom data from the ViewModel
        diseaseViewModel.getDiseaseList()
        symptomViewModel.getSymptomList()

        binding.createDiseaseSymptomBtn.setOnClickListener {
            if(validateForm()) {
                createDiseaseSymptom(0)
            }
        }

        return binding.root
    }

    private fun createDiseaseSymptom(diseaseSymptom_id: Int?) {
        Log.i("selectedDiseaseid","{$selectedDiseaseId}")
        Log.i("selectedsymptomid","{$selectedSymptomId}")

        val diseaseSymptom =
            selectedDiseaseId?.let {
                selectedSymptomId?.let { it1 ->
                    Disease_Symptom(
                        null,
                        it,
                        it1,
                    )
                }
            }
        Log.i("createddiseasesymptom","{$diseaseSymptom}")
        if (diseaseSymptom_id == 0){
            if (diseaseSymptom != null) {
                viewModel.createDiseaseSymptom(diseaseSymptom)
            }
        }
    }

    private fun createDiseaseSymptomObservable() {
        viewModel.getCreateDiseaseSymptomObservable().observe(viewLifecycleOwner, Observer<Disease_Symptom?> {
            if (it == null) {
                Log.e("error", "error")
            } else {
                showSuccessDialog()

            }
        })
    }

    private fun showSuccessDialog(){
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.custom_dialog_success)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(false) // Optional
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation

        val okay: Button = dialog.findViewById(R.id.btn_okay)
        val cancel: Button = dialog.findViewById(R.id.btn_cancel)

        okay.setOnClickListener {
            dialog.dismiss()
            val action = DiseaseSymptomFormFragmentDirections.actionCreateDiseaseSymptomFragmentToDiseaseSymptomListFragment()
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

    private fun validSymptomName(autoCompleteSymptomTextView: AutoCompleteTextView): String? {
        val selectedSymptom = autoCompleteSymptomTextView.text.toString()
        if (selectedSymptom.isEmpty()) {
            return "Symptom Name Is Required"
        }
        return null
    }

    private fun validateForm(): Boolean {

        val diseaseName = binding.autoCompleteDiseaseTextView
        val symptomName = binding.autoCompleteSymptomTextView

        val layoutDiseaseName: TextInputLayout = binding.layoutDiseasesName
        val layoutSymptomName: TextInputLayout = binding.layoutSymptomsName

        val diseaseNameError = validDiseaseName(diseaseName)
        val symptomNameError = validSymptomName(symptomName)

        var isValidate = true

        if (diseaseNameError != null) {
            layoutDiseaseName.error = diseaseNameError
            isValidate = false
        }
        if (symptomNameError != null) {
            layoutSymptomName.error = symptomNameError
            isValidate = false
        }
        return isValidate
    }

}