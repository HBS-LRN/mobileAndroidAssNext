package com.example.bait2073mobileapplicationdevelopment.screens.hospital.hospitalForm

import HospitalFormViewModelFactory
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentHospitalFormBinding
import com.example.bait2073mobileapplicationdevelopment.entities.Hospital
import com.example.bait2073mobileapplicationdevelopment.entities.Recipe
import com.example.bait2073mobileapplicationdevelopment.screens.hospital.hospitalForm.HospitalFormFragmentArgs
import com.example.bait2073mobileapplicationdevelopment.screens.hospital.hospitalForm.HospitalFormFragmentDirections

import com.google.android.material.textfield.TextInputLayout

import java.io.ByteArrayOutputStream

class HospitalFormFragment : Fragment() {
    private lateinit var viewModel: HospitalFormViewModel
    private lateinit var binding: FragmentHospitalFormBinding
    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHospitalFormBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, HospitalFormViewModelFactory()).get(HospitalFormViewModel::class.java)

        createHospitalObservable()
        val args = HospitalFormFragmentArgs.fromBundle(requireArguments())
        val hospital_id = args.hospitalId

        if (hospital_id != 0) {
            loadHospitalData(hospital_id)
        }

        validateOnChangeHospitalName()
        validateOnChangeHospitalContact()
        validateOnChangeHospitalAddress()


        binding.createHospitalBtn.setOnClickListener {
            if(validateForm()) {
                createHospital(hospital_id)
            }
        }

        return binding.root
    }

    private fun loadHospitalData(hospital_id: Int?) {
        viewModel.getLoadHospitalObservable().observe(viewLifecycleOwner, Observer<Hospital?> {
            if (it != null) {
                binding.eTextHospitalName.setText(it.hospital_name)
                binding.eTextHospitalContact.setText(it.hospital_contact)
                binding.eTextHospitalAddress.setText(it.hospital_address)
                binding.createHospitalBtn.setText("Update")
                binding.createHospitalTitletv.setText("Update Hospital")
                val hospitalImageView = binding.hospitalImg
                hospitalImageView.setImageResource(R.drawable.hospital_icon)

            }
        })
        viewModel.getHospitalData(hospital_id)
    }

    private fun createHospital(hospital_id: Int?) {

        val hospital = Hospital(
            null,
            binding.eTextHospitalName.text.toString(),
            binding.eTextHospitalContact.text.toString(),
            binding.eTextHospitalAddress.text.toString(),
        )

        if (hospital_id == 0)
            viewModel.createHospital(hospital)
        else
            viewModel.updateHospital(hospital_id ?: 0, hospital)

    }

    private fun createHospitalObservable() {
        viewModel.getCreateHospitalObservable().observe(viewLifecycleOwner, Observer<Hospital?> {
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
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation // Setting the animations to dialog

        val okay: Button = dialog.findViewById(R.id.btn_okay)
        val cancel: Button = dialog.findViewById(R.id.btn_cancel)

        okay.setOnClickListener {
            dialog.dismiss()
            val action = HospitalFormFragmentDirections.actionCreateHospitalFragmentToHospitalListFragment2()
            findNavController().navigate(action)
        }

        cancel.setOnClickListener {
            Toast.makeText(requireContext(), "Cancel", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        dialog.show() // Showing the dialog here
    }

    private fun validHospitalName(hospitalNameText: String): String? {
        if (hospitalNameText == "") {
            return "Hospital Name Is Required"
        } else if (hospitalNameText.length > 30) {
            return "The length of hospital is too long"
        }
        return null
    }

    private fun validHospitalContact(hospitalContactText: String): String? {
        if (hospitalContactText.length > 20) {
            return "The length of hospital contact is too long"
        }else if(hospitalContactText.matches("^(\\d{2,3}-\\d{7}|\\d{2,3}-\\d{1,7})$".toRegex())){
            return "The format of the contact is wrong. eg: xxx-xxxxxxx or xx-xxxxxxxx"
        }
        return null
    }

    private fun validHospitalAddress(hospitalAddressText: String): String? {
        if (hospitalAddressText.length > 200) {
            return "The length of hospital address is too long"
        }
        return null
    }

    private fun validateForm(): Boolean {

        //get the input
        val hospitalNameText = binding.eTextHospitalName.text.toString()
        val hospitalContactText = binding.eTextHospitalContact.text.toString()
        val hospitalAddressText = binding.eTextHospitalAddress.text.toString()

        //get the layout component
        val layoutHospitalName: TextInputLayout = binding.layoutHospitalName
        val layoutHospitalContact: TextInputLayout = binding.layoutHospitalContact
        val layoutHospitalAddress: TextInputLayout = binding.layoutHospitalAddress

        //get the error
        val hospitalNameError = validHospitalName(hospitalNameText)
        val hospitalContactError = validHospitalContact(hospitalContactText)
        val hospitalAddressError = validHospitalAddress(hospitalAddressText)

        //set default to true
        var isValidate = true

        //validate
        if (hospitalNameError != null) {
            layoutHospitalName.error = hospitalNameError
            isValidate = false
        }
        if (hospitalContactError != null) {
            layoutHospitalContact.error = hospitalContactError
            isValidate = false
        }
        if (hospitalAddressError != null) {
            layoutHospitalAddress.error = hospitalAddressError
            isValidate = false
        }

        return isValidate


    }

    private fun validateOnChangeHospitalName() {

        val layoutHospitalName: TextInputLayout = binding.layoutHospitalName
        val eTextHospitalName = binding.eTextHospitalName

        eTextHospitalName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // This method is called to notify that the text is about to be changed.
            }
            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                // This method is called to notify that the text has been changed.
                val hospitalNameText = charSequence.toString()
                val error = validHospitalName(hospitalNameText)

                if (error != null) {
                    layoutHospitalName.error = error
                } else {
                    layoutHospitalName.error = ""
                }

            }

            override fun afterTextChanged(editable: Editable?) {
                // This method is called to notify that the text has been changed and processed.
            }
        })
    }

    private fun validateOnChangeHospitalContact() {

        val layoutHospitalContact: TextInputLayout = binding.layoutHospitalContact
        val eTextHospitalContact = binding.eTextHospitalContact

        eTextHospitalContact.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // This method is called to notify that the text is about to be changed.
            }
            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                // This method is called to notify that the text has been changed.
                val hospitalContactText = charSequence.toString()
                val error = validHospitalContact(hospitalContactText)

                if (error != null) {
                    layoutHospitalContact.error = error
                } else {
                    layoutHospitalContact.error = ""
                }

            }

            override fun afterTextChanged(editable: Editable?) {
                // This method is called to notify that the text has been changed and processed.
            }
        })
    }
    private fun validateOnChangeHospitalAddress() {

        val layoutHospitalAddress: TextInputLayout = binding.layoutHospitalAddress
        val eTextHospitalAddress = binding.eTextHospitalAddress

        eTextHospitalAddress.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // This method is called to notify that the text is about to be changed.
            }
            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                // This method is called to notify that the text has been changed.
                val hospitalAddressText = charSequence.toString()
                val error = validHospitalAddress(hospitalAddressText)

                if (error != null) {
                    layoutHospitalAddress.error = error
                } else {
                    layoutHospitalAddress.error = ""
                }

            }

            override fun afterTextChanged(editable: Editable?) {
                // This method is called to notify that the text has been changed and processed.
            }
        })
    }
}