package com.example.bait2073mobileapplicationdevelopment.screens.symptom.symptomForm

import SymptomFormViewModelFactory
import android.app.Activity
import android.app.AlertDialog
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
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentSymptomFormBinding
import com.example.bait2073mobileapplicationdevelopment.entities.Recipe
import com.example.bait2073mobileapplicationdevelopment.entities.Symptom
import com.example.bait2073mobileapplicationdevelopment.screens.symptom.symptomForm.SymptomFormFragmentArgs
import com.example.bait2073mobileapplicationdevelopment.screens.symptom.symptomForm.SymptomFormFragmentDirections
import com.example.bait2073mobileapplicationdevelopment.screens.symptom.symptomForm.SymptomFormViewModel
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class SymptomFormFragment: Fragment() {
    private lateinit var viewModel: SymptomFormViewModel
    private lateinit var binding: FragmentSymptomFormBinding
    private val PICK_IMAGE_REQUEST = 1
    private val CAPTURE_IMAGE_REQUEST = 2
    private var selectedImageBitmap: Bitmap? = null
    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSymptomFormBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, SymptomFormViewModelFactory()).get(SymptomFormViewModel::class.java)

        createSymptomObservable()
        val args = SymptomFormFragmentArgs.fromBundle(requireArguments())
        val symptom_id = args.symptomId

        if (symptom_id != 0) {
            loadSymptomData(symptom_id)
        }
        validateOnChangeSymptomName()
        validateOnChangeSymptomDescription()

        binding.createSymptomBtn.setOnClickListener {
            if(validateForm()) {
                createSymptom(symptom_id, selectedImageBitmap)
            }
        }

        binding.uploadButton.setOnClickListener {
           openGallery()
        }

        return binding.root
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    val selectedImageUri = data?.data
                    if (isImageValid(selectedImageUri)) {
                        binding.symptomImg.setImageURI(selectedImageUri)
                        val imageBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, selectedImageUri)
                        selectedImageBitmap = imageBitmap // Store the selected image in the variable
                    }else{
                        Toast.makeText(requireContext(),"Invalid image format", Toast.LENGTH_SHORT).show()
                    }
                }

                CAPTURE_IMAGE_REQUEST -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap?
                    // Handle the captured image bitmap (e.g., display it, store it, etc.)
                    if (imageBitmap != null) {
                        selectedImageBitmap = imageBitmap // Store the captured image in the variable
                    }
                }
            }
        }
    }

    private fun isImageValid(imageUri: Uri?): Boolean {
        if (imageUri == null) {
            return false
        }
        val contentResolver = requireContext().contentResolver
        val mimeType = contentResolver.getType(imageUri)

        return when (mimeType) {
            "image/jpeg", "image/jpg", "image/png" -> true
            else -> false
        }
    }

    private fun loadSymptomData(symptom_id: Int?) {
        viewModel.getLoadSymptomObservable().observe(viewLifecycleOwner, Observer<Symptom?> {
            if (it != null) {
                binding.eTextSymptomName.setText(it.symptom_name)
                binding.eTextSymptomDescription.setText(it.symptom_description)
                binding.createSymptomBtn.setText("Update")
                binding.createSymptomTitletv.setText("Update Symptom")
                val symptomImageView = binding.symptomImg

                if (!it.symptom_image.isNullOrBlank()) {
                    Picasso.get().load(it.symptom_image).fit().into(symptomImageView)
                } else{
                    // If no image URL is available,  set a placeholder image or handle this case as needed.\
                    Log.e("noimage", "noimage")
                    Picasso.get().load(R.drawable.image_symptom).into(symptomImageView)
                }
            }
        })
        viewModel.getSymptomData(symptom_id)
    }

    private fun createSymptom(symptom_id: Int?,selectedImageBitmap: Bitmap?) {
        val imageData: String? = if (selectedImageBitmap != null) {
            encodeBitmapToBase64(selectedImageBitmap)

        } else {
            null
        }

        Log.e("imageData","$imageData")
        val symptom = Symptom(
            null,
            binding.eTextSymptomName.text.toString(),
            imageData,
            binding.eTextSymptomDescription.text.toString(),
        )

        if (symptom_id == 0)
            viewModel.createSymptom(symptom)
        else
            viewModel.updateSymptom(symptom_id ?: 0, symptom)

    }

    private fun encodeBitmapToBase64(bitmap: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val byteArray = baos.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun createSymptomObservable() {
        viewModel.getCreateSymptomObservable().observe(viewLifecycleOwner, Observer<Symptom?> {
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
            val action = SymptomFormFragmentDirections.actionCreateSymptomFragmentToSymptomListFragment2()
            findNavController().navigate(action)
        }

        cancel.setOnClickListener {
            Toast.makeText(requireContext(), "Cancel", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        dialog.show()
    }

    private fun validSymptomName(symptomNameText: String): String? {
        if (symptomNameText == "") {
            return "Symptom Name Is Required"
        } else if (symptomNameText.length > 30) {
            return "The length of name is too long"
        }
        return null
    }

    private fun validSymptomDescription(symptomDescriptionText: String): String? {
         if (symptomDescriptionText.length > 200) {
            return "The length of description is too long"
        }
        return null
    }

    private fun validateForm(): Boolean {

        //get the input
        val symptomNameText = binding.eTextSymptomName.text.toString()
        val symptomDescriptionText = binding.eTextSymptomDescription.text.toString()

        //get the layout component
        val layoutSymptomName: TextInputLayout = binding.layoutSymptomName
        val layoutSymptomDescription: TextInputLayout = binding.layoutSymptomDescription

        //get the error
        val symptomNameError = validSymptomName(symptomNameText)
        val symptomDescriptionError = validSymptomDescription(symptomDescriptionText)

        //set default to true
        var isValidate = true

        //validate
        if (symptomNameError != null) {
            layoutSymptomName.error = symptomNameError
            isValidate = false
        }
        if (symptomDescriptionError != null) {
            layoutSymptomDescription.error = symptomDescriptionError
            isValidate = false
        }

        return isValidate

    }
    private fun validateOnChangeSymptomName() {

        val layoutSymptomName: TextInputLayout = binding.layoutSymptomName
        val eTextSymptomName = binding.eTextSymptomName

        eTextSymptomName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }
            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {

                val symptomNameText = charSequence.toString()
                val error = validSymptomName(symptomNameText)

                if (error != null) {
                    layoutSymptomName.error = error
                } else {
                    layoutSymptomName.error = ""
                }

            }

            override fun afterTextChanged(editable: Editable?) {

            }
        })
    }

    private fun validateOnChangeSymptomDescription() {

        val layoutSymptomDescription: TextInputLayout = binding.layoutSymptomDescription
        val eTextSymptomDescription = binding.eTextSymptomDescription

        eTextSymptomDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }
            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {

                val symptomDescriptionText = charSequence.toString()
                val error = validSymptomDescription(symptomDescriptionText)

                if (error != null) {
                    layoutSymptomDescription.error = error
                } else {
                    layoutSymptomDescription.error = ""
                }

            }

            override fun afterTextChanged(editable: Editable?) {

            }
        })
    }



}