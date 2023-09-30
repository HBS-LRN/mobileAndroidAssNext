package com.example.bait2073mobileapplicationdevelopment.screens.recipe.recipeForm

import RecipeFormViewModelFactory
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
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentRecipeFormBinding
import com.example.bait2073mobileapplicationdevelopment.entities.Recipe
import com.example.bait2073mobileapplicationdevelopment.screens.recipe.recipeForm.RecipeFormFragmentArgs
import com.example.bait2073mobileapplicationdevelopment.screens.recipe.recipeForm.RecipeFormFragmentDirections
import com.example.bait2073mobileapplicationdevelopment.screens.recipe.recipeForm.RecipeFormViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class RecipeFormFragment: Fragment() {

    private lateinit var viewModel: RecipeFormViewModel
    private lateinit var binding: FragmentRecipeFormBinding
    private val PICK_IMAGE_REQUEST = 1
    private val CAPTURE_IMAGE_REQUEST = 2
    private var selectedImageBitmap: Bitmap? = null
    private lateinit var dialog: Dialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRecipeFormBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(
            this, RecipeFormViewModelFactory()
        ).get(RecipeFormViewModel::class.java)
        createRecipeObservable()
        val args = RecipeFormFragmentArgs.fromBundle(requireArguments())
        val recipe_id = args.recipeId

        if (recipe_id != 0) {
            loadRecipeData(recipe_id)
        }

        validateOnChangeRecipeName()
        validateOnChangeRecipeDescription()
        validateOnChangeRecipeServings()

        binding.createRecipeBtn.setOnClickListener {
            if(validateForm()) {
                createRecipe(recipe_id, selectedImageBitmap)
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
                        binding.recipeImg.setImageURI(selectedImageUri)
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

    private fun loadRecipeData(recipe_id: Int?) {
        viewModel.getLoadRecipeObservable().observe(viewLifecycleOwner, Observer<Recipe?> {
            if (it != null) {

                binding.eTextRecipeName.setText(it.recipe_name)
                binding.eTextRecipeDescription.setText(it.recipe_description)
                binding.eTextRecipeServings.setText(it.recipe_servings.toString())
                binding.createRecipeBtn.setText("Update")
                binding.createRecipeTitletv.setText("Update Recipe")

                val recipeImageView = binding.recipeImg

                if (!it.recipe_image.isNullOrBlank()) {

                    Picasso.get().load(it.recipe_image).fit().into(recipeImageView)
                } else{
                    // If no image URL is available,  set a placeholder image or handle this case as needed.\
                    Log.e("noimage", "noimage")
                    Picasso.get().load(R.drawable.diseases_recipe).into(recipeImageView)
                }

            }
        })
        viewModel.getRecipeData(recipe_id)
    }

    private fun createRecipe(recipe_id: Int?,selectedImageBitmap: Bitmap?) {

        val imageData: String? = if (selectedImageBitmap != null) {
            encodeBitmapToBase64(selectedImageBitmap)
        } else {
            null
        }
        Log.e("imageData","$imageData")

        val recipe = Recipe(
            null,
            binding.eTextRecipeName.text.toString(),
            imageData,
            binding.eTextRecipeDescription.text.toString(),
            binding.eTextRecipeServings.text.toString().toIntOrNull(),
        )

        if (recipe_id == 0)
            viewModel.createRecipe(recipe)
        else
            viewModel.updateRecipe(recipe_id ?: 0, recipe)

    }


    private fun encodeBitmapToBase64(bitmap: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val byteArray = baos.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun createRecipeObservable() {
        viewModel.getCreateRecipeObservable().observe(viewLifecycleOwner, Observer<Recipe?> {
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
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation // Setting the animations to dialog

        val okay: Button = dialog.findViewById(R.id.btn_okay)
        val cancel: Button = dialog.findViewById(R.id.btn_cancel)

        okay.setOnClickListener {

            dialog.dismiss()
            val action = RecipeFormFragmentDirections.actionCreateRecipeFragmentToRecipeListFragment2()
            findNavController().navigate(action)

        }

        cancel.setOnClickListener {
            Toast.makeText(requireContext(), "Cancel", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        dialog.show() // Showing the dialog here
    }

    private fun validRecipeName(recipeNameText: String): String? {
        if (recipeNameText == "") {
            return "Recipe Name Is Required"
        } else if (recipeNameText.length > 50) {
            return "The length of recipe name is too long"
        }
        return null
    }

    private fun validRecipeDescription(recipeDescriptionText: String): String? {
        if (recipeDescriptionText == "") {
            return "Recipe Description is required"
        } else if (recipeDescriptionText.length > 200) {
            return "The length of recipe description is too long"
        }
        return null
    }

    private fun validRecipeServings(recipeServingsText: String): String? {
        if (recipeServingsText.isBlank()) {
            return "Recipe Servings is required"
        } else if (!recipeServingsText.matches("^(\\d{1,2})$".toRegex())) {
            return "Invalid recipe servings format. Enter in the format 'X', 'XX'."
        }
        return null
    }


    private fun validateForm(): Boolean {

        //get the input
        val recipeNameText = binding.eTextRecipeName.text.toString()
        val recipeDescriptionText = binding.eTextRecipeDescription.text.toString()
        val recipeServingsText = binding.eTextRecipeServings.text.toString()

        //get the layout component
        val layoutRecipeName: TextInputLayout = binding.layoutRecipeName
        val layoutRecipeDescription: TextInputLayout = binding.layoutRecipeDescription
        val layoutServings: TextInputLayout = binding.layoutRecipeServings

        //get the error
        val recipeNameError = validRecipeName(recipeNameText)
        val recipeDescriptionError = validRecipeDescription(recipeDescriptionText)
        val recipeServingsError = validRecipeServings(recipeServingsText)

        //set default to true
        var isValidate = true

        //validate
        if (recipeNameError != null) {
            layoutRecipeName.error = recipeNameError
            isValidate = false
        }
        if (recipeDescriptionError != null) {
            layoutRecipeDescription.error = recipeDescriptionError
            isValidate = false
        }

        if (recipeServingsError != null) {
            layoutServings.error = recipeServingsError
            isValidate = false
        }

        return isValidate

    }

    private fun validateOnChangeRecipeName() {

        val layoutName: TextInputLayout = binding.layoutRecipeName
        val eTextRecipeName = binding.eTextRecipeName

        eTextRecipeName.addTextChangedListener(object : TextWatcher {
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
                val recipeNameText = charSequence.toString()
                val error = validRecipeName(recipeNameText)

                if (error != null) {
                    layoutName.error = error
                } else {
                    layoutName.error = ""
                }

            }

            override fun afterTextChanged(editable: Editable?) {
                // This method is called to notify that the text has been changed and processed.
            }
        })
    }

    private fun validateOnChangeRecipeDescription() {

        val layoutDescription: TextInputLayout = binding.layoutRecipeDescription
        val eTextRecipeDescription = binding.eTextRecipeDescription

        eTextRecipeDescription.addTextChangedListener(object : TextWatcher {
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
                val recipeDescriptionText = charSequence.toString()
                val error = validRecipeDescription(recipeDescriptionText)

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

    private fun validateOnChangeRecipeServings() {

        val layoutServings: TextInputLayout = binding.layoutRecipeServings
        val eTextRecipeServings = binding.eTextRecipeServings

        eTextRecipeServings.addTextChangedListener(object : TextWatcher {
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
                val recipeServingsText = charSequence.toString()
                val error = validRecipeServings(recipeServingsText)

                if (error != null) {
                    layoutServings.error = error
                } else {
                    layoutServings.error = ""
                }
            }

            override fun afterTextChanged(editable: Editable?) {
                // This method is called to notify that the text has been changed and processed.
            }
        })
    }
}