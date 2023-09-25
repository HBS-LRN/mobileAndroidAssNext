package com.example.bait2073mobileapplicationdevelopment.screens.admin.AdminForm


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
import android.util.Patterns
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
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentAdminFormBinding
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentUserFormBinding
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentUserListBinding
import com.example.bait2073mobileapplicationdevelopment.entities.User
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream


class AdminFormFragment : Fragment() {


    private lateinit var viewModel: AdminFormViewModel
    private lateinit var binding: FragmentAdminFormBinding
    private val PICK_IMAGE_REQUEST = 1
    private val CAPTURE_IMAGE_REQUEST = 2
    private var selectedImageBitmap: Bitmap? = null
    private lateinit var dialog: Dialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        //val user_id = intent.getStringExtra("user_id")
        binding = FragmentAdminFormBinding.inflate(inflater, container, false)

        initViewModel()
        createUserObservable()
        val args = AdminFormFragmentArgs.fromBundle(requireArguments())
        val user_id = args.userId

        if (user_id != 0) {
            loadUserData(user_id)
        }

        validateOnChangeUserName()
        validateOnChangeEmail()
        validateOnChangePhone()


        binding.createButton.setOnClickListener {

            if(validateForm()) {
                createUser(user_id, selectedImageBitmap)
            }
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
        openGallery()
    }
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"  // Set the MIME type to images only
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/jpg", "image/png"))
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // Function to open the camera for image capture


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


                    binding.profileImg.setImageURI(selectedImageUri)
                        val imageBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, selectedImageUri)
                        selectedImageBitmap = imageBitmap // Store the selected image in the variable
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
    private fun loadUserData(user_id: Int?) {
        viewModel.getLoadUserObservable().observe(viewLifecycleOwner, Observer<User?> {
            if (it != null) {

                binding.eTextUserName.setText(it.name)
                binding.eTextEmail.setText(it.email)
                binding.createButton.setText("Update")
                binding.createTitletv.setText("Update Admin")
                binding.eTextPhone.setText(it.phone.toString())

                val custImageView = binding.profileImg

                if (!it.image.isNullOrBlank()) {

                    Picasso.get().load(it.image).fit().into(custImageView)
                } else{
                    // If no image URL is available,  set a placeholder image or handle this case as needed.\
                    Log.e("noimage", "noimage")
                    Picasso.get().load(R.drawable.img_person).into(custImageView)
                }
                if (it.gender == "Male") {
                    binding.radioMale.isChecked = true
                } else {
                    // Optionally, you can uncheck it here if "it.gender" is not "Male"
                    binding.radioFemale.isChecked = true
                }
            }
        })
        viewModel.getUserData(user_id)
    }

    private fun createUser(user_id: Int?,selectedImageBitmap: Bitmap?) {




        val imageData: String? = if (selectedImageBitmap != null) {
            encodeBitmapToBase64(selectedImageBitmap)
        } else {
            null
        }

        val selectedGender = when (binding.radioGroupGender.checkedRadioButtonId) {
            R.id.radioMale -> "Male"
            R.id.radioFemale -> "Female"
            else -> null
        }
        val user = User(
            null,
            binding.eTextUserName.text.toString(),
            binding.eTextEmail.text.toString(),
            selectedGender,
            imageData,
            binding.eTextPhone.text.toString(),
            "",
            0.0,
            0.0,
            null,
        )

        if (user_id == 0)
            viewModel.createUser(user)
        else
            viewModel.updateUser(user_id ?: 0, user)

    }

    private fun isImageFile(uri: Uri): Boolean {
        val mimeType = requireContext().contentResolver.getType(uri)
        return mimeType?.startsWith("image/") == true
    }


    private fun encodeBitmapToBase64(bitmap: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val byteArray = baos.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
    private fun initViewModel() {
//        viewModel = ViewModelProvider(
//            this,
//            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
//        ).get(AdminFormViewModel::class.java)
        viewModel = ViewModelProvider(
            this,
            AdminFormViewModelFactory()
        ).get(AdminFormViewModel::class.java)
    }

    private fun createUserObservable() {
        viewModel.getCreateNewUserObservable().observe(viewLifecycleOwner, Observer<User?> {
            if (it == null) {
                binding.layoutEmail.error = "Email Already Registered, Please Try Another Email"
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
            val action =  AdminFormFragmentDirections.actionAdminFormFragmentToAdminListFragment()
            findNavController().navigate(action)

        }

        cancel.setOnClickListener {
            Toast.makeText(requireContext(), "Cancel", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        dialog.show() // Showing the dialog here


    }

    private fun validEmail(emailText: String): String? {
        if (emailText == "") {
            return "Email Is Required"
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            return "Invalid Email Address"
        }
        return null
    }


    private fun validUserName(nameText: String): String? {

        if (nameText == "") {
            return "Name Is Required"
        } else if (nameText.length > 10) {
            return "Invalid Name"
        }
        return null
    }





    private fun validateForm(): Boolean {

        //get the input
        val emailText = binding.eTextEmail.text.toString()
        val usernameText = binding.eTextUserName.text.toString()

        val phoneText = binding.eTextPhone.text.toString()

        //get the layout component
        val layoutEmail: TextInputLayout = binding.layoutEmail
        val layoutUserName: TextInputLayout = binding.layoutUsername
        val layoutPhone: TextInputLayout = binding.layoutPhone



        val selectedGender = when (binding.radioGroupGender.checkedRadioButtonId) {
            R.id.radioMale -> "Male"
            R.id.radioFemale -> "Female"
            else -> null
        }
        //get the error
        val emailError = validEmail(emailText)
        val nameError = validUserName(usernameText)
        val phoneError = validPhone(phoneText)
        val genderError = if (selectedGender == null) "Gender is required" else null

        //set default to true
        var isValidate = true



        //validate
        if (emailError != null) {
            layoutEmail.error = emailError
            isValidate = false
        }
        if (nameError != null) {
            layoutUserName.error = nameError
            isValidate = false
        }
        if (phoneError != null) {
            layoutPhone.error = nameError
            isValidate = false
        }


        if (genderError != null) {
            // Show the error for gender
            binding.genderError.text = genderError
            binding.genderError.visibility = View.VISIBLE
            isValidate = false
        } else {
            binding.genderError.visibility = View.GONE
        }


        return isValidate


    }



//    private fun emailFocusListener() {
//        binding.eTextEmail.setOnFocusChangeListener { _, focused ->
//            if (!focused) {
//                val emailError = validEmail(binding.eTextEmail.text.toString())
//                if (emailError != null) {
//                    binding.layoutEmail.error = emailError
//                }else{
//                    binding.layoutEmail.error = null
//                }
//
//
//            }
//        }
//    }


    private fun validPhone(phoneText: String): String? {
        if (phoneText.isBlank()) {
            return "Phone number is required"
        } else if (!phoneText.matches("^[0-9]{10,11}$".toRegex())) {
            return "Invalid phone number format. Enter a 10 or 11-digit number."
        }
        return null
    }


    private fun validateOnChangePhone() {
        val layoutPhone: TextInputLayout = binding.layoutPhone
        val eTextPhone = binding.eTextPhone

        eTextPhone.addTextChangedListener(object : TextWatcher {
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
                val phoneText = charSequence.toString()
                val error = validPhone(phoneText)

                if (error != null) {
                    layoutPhone.error = error
                } else {
                    layoutPhone.error = ""
                }
            }

            override fun afterTextChanged(editable: Editable?) {
                // This method is called to notify that the text has been changed and processed.
            }
        })
    }

    private fun validateOnChangeEmail() {

        val layoutEmail: TextInputLayout = binding.layoutEmail
        val eTextEmail: TextInputEditText = binding.eTextEmail
        eTextEmail.addTextChangedListener(object : TextWatcher {
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
                val emailText = charSequence.toString()
                val error = validEmail(emailText)

                if (error != null) {
                    layoutEmail.error = error
                } else {
                    layoutEmail.error = ""
                }

            }

            override fun afterTextChanged(editable: Editable?) {
                // This method is called to notify that the text has been changed and processed.
            }
        })
    }


    private fun validateOnChangeUserName() {
        val eTextUserName = binding.eTextUserName
        val layoutUserName: TextInputLayout = binding.layoutUsername
        eTextUserName.addTextChangedListener(object : TextWatcher {
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
                val userNameText = charSequence.toString()
                val error = validUserName(userNameText)

                if (error != null) {
                    layoutUserName.error = error
                } else {
                    layoutUserName.error = ""
                }

            }

            override fun afterTextChanged(editable: Editable?) {
                // This method is called to notify that the text has been changed and processed.
            }
        })
    }




}
