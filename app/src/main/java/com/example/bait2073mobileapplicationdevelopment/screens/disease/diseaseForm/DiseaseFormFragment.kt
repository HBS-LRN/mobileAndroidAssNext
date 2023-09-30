package com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseForm

import DiseaseFormViewModelFactory
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
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
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentDiseaseFormBinding
import com.example.bait2073mobileapplicationdevelopment.entities.Disease
import com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseForm.DiseaseFormFragmentArgs
import com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseForm.DiseaseFormFragmentDirections
import com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseForm.DiseaseFormViewModel
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class DiseaseFormFragment: Fragment() {
    private lateinit var viewModel: DiseaseFormViewModel
    private lateinit var binding: FragmentDiseaseFormBinding
    private val PICK_IMAGE_REQUEST = 1
    private val CAPTURE_IMAGE_REQUEST = 2
    private var selectedImageBitmap: Bitmap? = null
    private lateinit var dialog: Dialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiseaseFormBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this, DiseaseFormViewModelFactory())
            .get(DiseaseFormViewModel::class.java)

        createDiseaseObservable()
        val args = DiseaseFormFragmentArgs.fromBundle(requireArguments())
        val disease_id = args.diseaseId

        if (disease_id != 0) {
            loadDiseaseData(disease_id)
        }

        validateOnChangeDiseaseName()
        validateOnChangeDiseaseDescription()
        validateOnChangeDiseaseCauses()


        binding.createDiseaseBtn.setOnClickListener {
            if(validateForm()) {
                createDisease(disease_id, selectedImageBitmap)
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
                        binding.diseaseImg.setImageURI(selectedImageUri)
                        val imageBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, selectedImageUri)
                        selectedImageBitmap = imageBitmap
                    }else{
                        Toast.makeText(requireContext(),"Invalid image format", Toast.LENGTH_SHORT).show()
                    }
                }

                CAPTURE_IMAGE_REQUEST -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap?
                    if (imageBitmap != null) {
                        selectedImageBitmap = imageBitmap
                    }
                }
            }
        }
    }
    private fun loadDiseaseData(disease_id: Int?) {
        viewModel.getLoadDiseaseObservable().observe(viewLifecycleOwner, Observer<Disease?> {
            if (it != null) {
                binding.eTextDiseaseName.setText(it.disease_name)
                binding.eTextDiseaseDescription.setText(it.disease_description)
                binding.eTextDiseaseCauses.setText(it.disease_causes)
                binding.createDiseaseBtn.setText("Update")
                binding.createDiseaseTitletv.setText("Update Disease")
                val diseaseImageView = binding.diseaseImg

                if (!it.disease_image.isNullOrBlank()) {
                    Picasso.get().load(it.disease_image).fit().into(diseaseImageView)
                } else{
                    // If no image URL is available,  set a placeholder image or handle this case as needed.\
                    Log.e("noimage", "noimage")
                    Picasso.get().load(R.drawable.image_disease).into(diseaseImageView)
                }
            }
        })
        viewModel.getDiseaseData(disease_id)
    }

    private fun createDisease(disease_id: Int?,selectedImageBitmap: Bitmap?) {
        val imageData: String? = if (selectedImageBitmap != null) {
            encodeBitmapToBase64(selectedImageBitmap)

        } else {
            null
        }
        val disease = Disease(
            null,
            binding.eTextDiseaseName.text.toString(),
            imageData,
            binding.eTextDiseaseCauses.text.toString(),
            binding.eTextDiseaseDescription.text.toString(),
        )

        if (disease_id == 0)
            viewModel.createDisease(disease)
        else
            viewModel.updateDisease(disease_id ?: 0, disease)

    }

    private fun encodeBitmapToBase64(bitmap: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val byteArray = baos.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun createDiseaseObservable() {
        viewModel.getCreateDiseaseObservable().observe(viewLifecycleOwner, Observer<Disease?> {
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
            val action = DiseaseFormFragmentDirections.actionCreateDiseaseFragmentToDiseaseListFragment()
            findNavController().navigate(action)
        }

        cancel.setOnClickListener {
            Toast.makeText(requireContext(), "Cancel", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        dialog.show()
    }

    private fun validDiseaseName(diseaseNameText: String): String? {
        if (diseaseNameText == "") {
            return "Disease Name Is Required"
        } else if (diseaseNameText.length > 30) {
            return "The length of disease name is too long"
        }
        return null
    }

    private fun validDiseaseDescription(diseaseDescriptionText: String): String? {
        if (diseaseDescriptionText.length > 220) {
            return "The length of disease description is too long"
        }
        return null
    }

    private fun validDiseaseCauses(diseaseCausesText: String): String? {
        if (diseaseCausesText.length > 220) {
            return "The length of disease description is too long"
        }
        return null
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

    private fun validateForm(): Boolean {

        val diseaseNameText = binding.eTextDiseaseName.text.toString()
        val diseaseDescriptionText = binding.eTextDiseaseDescription.text.toString()
        val diseaseCausesText = binding.eTextDiseaseCauses.text.toString()

        val layoutDiseaseName: TextInputLayout = binding.layoutDiseaseName
        val layoutDiseaseDescription: TextInputLayout = binding.layoutDiseaseDescription
        val layoutDiseaseCauses: TextInputLayout = binding.layoutDiseaseCauses

        val diseaseNameError = validDiseaseName(diseaseNameText)
        val diseaseDescriptionError = validDiseaseDescription(diseaseDescriptionText)
        val diseaseCausesError = validDiseaseCauses(diseaseCausesText)

        //set default to true
        var isValidate = true

        //validate
        if (diseaseNameError != null) {
            layoutDiseaseName.error = diseaseNameError
            isValidate = false
        }
        if (diseaseDescriptionError != null) {
            layoutDiseaseDescription.error = diseaseDescriptionError
            isValidate = false
        }

        if (diseaseCausesError != null) {
            layoutDiseaseCauses.error = diseaseCausesError
            isValidate = false
        }
        return isValidate


    }

    private fun validateOnChangeDiseaseName() {
        val eTextDiseaseName = binding.eTextDiseaseName
        val layoutDiseaseName: TextInputLayout = binding.layoutDiseaseName
        eTextDiseaseName.addTextChangedListener(object : TextWatcher {
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
                val diseaseNameText = charSequence.toString()
                val error = validDiseaseName(diseaseNameText)
                if (error != null) {
                    layoutDiseaseName.error = error
                } else {
                    layoutDiseaseName.error = ""
                }

            }
            override fun afterTextChanged(editable: Editable?) {
                // This method is called to notify that the text has been changed and processed.
            }
        })
    }

    private fun validateOnChangeDiseaseDescription() {

        val layoutDescription: TextInputLayout = binding.layoutDiseaseDescription
        val eTextDescription = binding.eTextDiseaseDescription

        eTextDescription.addTextChangedListener(object : TextWatcher {
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
                val descriptionText = charSequence.toString()
                val error = validDiseaseDescription(descriptionText)

                if (error != null) {
                    layoutDescription.error = error
                } else {
                    layoutDescription.error = ""
                }

            }

            override fun afterTextChanged(editable: Editable?) {
                // This method is called to notify that the text has been changed and processed.
            }
        })
    }

    private fun validateOnChangeDiseaseCauses() {

        val layoutCauses: TextInputLayout = binding.layoutDiseaseCauses
        val eTextCauses = binding.eTextDiseaseCauses

        eTextCauses.addTextChangedListener(object : TextWatcher {
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
                val causesText = charSequence.toString()
                val error = validDiseaseCauses(causesText)

                if (error != null) {
                    layoutCauses.error = error
                } else {
                    layoutCauses.error = ""
                }

            }

            override fun afterTextChanged(editable: Editable?) {
                // This method is called to notify that the text has been changed and processed.
            }
        })
    }
}