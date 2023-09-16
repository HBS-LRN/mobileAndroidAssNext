package com.example.bait2073mobileapplicationdevelopment.screens.admin.WorkoutForm

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentUserFormBinding
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentWorkoutFormBinding
import com.example.bait2073mobileapplicationdevelopment.entities.User
import com.example.bait2073mobileapplicationdevelopment.entities.Workout
import com.example.bait2073mobileapplicationdevelopment.screens.admin.UserForm.UserFormFragmentArgs
import com.example.bait2073mobileapplicationdevelopment.screens.admin.UserForm.UserFormFragmentDirections
import com.example.bait2073mobileapplicationdevelopment.screens.admin.UserForm.UserFormViewModel
import com.example.bait2073mobileapplicationdevelopment.screens.personalized.WorkoutFragmentViewModel
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

class WorkoutFormFragment : Fragment() {


    private lateinit var viewModel: WorkoutFormViewModel
    private lateinit var binding: FragmentWorkoutFormBinding
    private val PICK_IMAGE_REQUEST = 1
    private val CAPTURE_IMAGE_REQUEST = 2
    private var selectedGifUri: Uri? = null
    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        //val user_id = intent.getStringExtra("user_id")
        binding = FragmentWorkoutFormBinding.inflate(inflater, container, false)

        initViewModel()
        createWorkoutObservable()

        val adapter = ArrayAdapter.createFromResource(requireContext(), R.array.bmi_status,android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerBmiStatus.adapter = adapter

        binding.createButton.setOnClickListener {
            createWorkout(selectedGifUri)
        }


        binding.uploadButton.setOnClickListener {
//            pickImage()
            showImagePickerDialog()
        }

        return binding.root
    }

    private fun pickImage() {
        val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
        startActivityForResult(intent, 101)

    }

    // Function to show a dialog for choosing between gallery and camera
    private fun showImagePickerDialog() {
        val options = arrayOf("Gallery", "Camera")

        AlertDialog.Builder(requireContext())
            .setTitle("Choose an option")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openGallery()
                    1 -> openCamera()
                }
            }
            .show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // Function to open the camera for image capture
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAPTURE_IMAGE_REQUEST)
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (resultCode == Activity.RESULT_OK) {
//
//                if(requestCode == 101){
//                    val url = data?.data
//                    binding.profileImg.setImageURI(url)
//                }
//            }
//        }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    val selectedImageUri = data?.data
                    if (selectedImageUri != null) {

                        binding.gifImageView.setImageURI(selectedImageUri)
                        selectedGifUri = selectedImageUri
                    }
                }

//                CAPTURE_IMAGE_REQUEST -> {
//                    val imageBitmap = data?.extras?.get("data") as Bitmap?
//                    // Handle the captured image bitmap (e.g., display it, store it, etc.)
//                    if (imageBitmap != null) {
//                        selectedGifUri =
//                            imageBitmap // Store the captured image in the variable
//                    }
//                }
            }
        }
    }


    private fun createWorkout(selectedGifUri: Uri?) {


        val imageData: String? = if (selectedGifUri != null) {
            encodeGifToBase64(requireContext(), selectedGifUri)
        } else {
            null
        }

        val workout = Workout(
            null,
            binding.eTextName.text.toString(),
            binding.eTextDescription.text.toString(),
            binding.eTextLink.text.toString(),
            imageData,
            binding.eTextCalorie.text.toString().toDoubleOrNull(),
            binding.spinnerBmiStatus.selectedItem.toString()
        )


            viewModel.createWorkout(workout)


    }


    fun encodeGifToBase64(context: Context, gifUri: Uri): String? {
        var inputStream: InputStream? = null
        val contentResolver: ContentResolver = context.contentResolver

        try {
            inputStream = contentResolver.openInputStream(gifUri)
            if (inputStream != null) {
                val buffer = ByteArray(1024)
                val outputStream = ByteArrayOutputStream()
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
                val gifBytes: ByteArray = outputStream.toByteArray()
                return android.util.Base64.encodeToString(gifBytes, android.util.Base64.DEFAULT)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                inputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }
    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(WorkoutFormViewModel::class.java)

    }

    private fun createWorkoutObservable() {
        viewModel.getCreateNewWorkOutObservable().observe(viewLifecycleOwner, Observer<Workout?> {
            if (it == null) {
                UserFormFragmentDirections.actionCreateUserFragementToUserListFragment()
            } else {


                showSuccessDialog()


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
            val action = UserFormFragmentDirections.actionCreateUserFragementToUserListFragment()
            findNavController().navigate(action)

        }

        cancel.setOnClickListener {
            Toast.makeText(requireContext(), "Cancel", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        dialog.show() // Showing the dialog here


    }
}