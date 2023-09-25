package com.example.bait2073mobileapplicationdevelopment.screens.password.ChangePassword

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentChangePasswordBinding
import com.example.bait2073mobileapplicationdevelopment.entities.UpdateBmiUser
import com.example.bait2073mobileapplicationdevelopment.entities.UpdatePasswordUser
import com.example.bait2073mobileapplicationdevelopment.entities.User
import com.example.bait2073mobileapplicationdevelopment.screens.admin.UserForm.UserFormFragmentDirections
import com.example.bait2073mobileapplicationdevelopment.screens.profile.Profile.ProfileFragmentViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ChangePasswordFragment : Fragment() {


    private lateinit var dialog: Dialog
    private lateinit var binding: FragmentChangePasswordBinding
    private lateinit var viewModel: ChangePasswordViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)


        //initialized method
        initViewModel()
        updatePasswordObservable()
        validateOnChangeNewPassword()
        validateOnChangeNewConfirmPassword()
        validateCurrentPassword()


        binding.saveButton.setOnClickListener {

            if (validateForm()) {
                updatePassword()
            }
        }

        return binding.root
    }


    private fun updatePasswordObservable() {
        viewModel.getUpdatePasswordObservable()
            .observe(viewLifecycleOwner, Observer<UpdatePasswordUser?> {
                if (it == null) {
                    binding.layoutCurrentPassword.error = "Invalid Current Password"
                } else {
                    showSuccessDialog()
                }
            })
    }

    private fun retrieveUserDataFromSharedPreferences(context: Context): Pair<Int, String>? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt(
            "UserId",
            -1
        ) // -1 is a default value if the key is not found
        val userName = sharedPreferences.getString(
            "UserName",
            null
        ) // null is a default value if the key is not found
        if (userId != -1 && userName != null) {
            return Pair(userId, userName)
        }
        return null
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
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.changePasswordFragment, true)
                .build()
            findNavController().navigate(
                R.id.action_changePasswordFragment_to_homeFragment,
                null,
                navOptions
            )

        }

        cancel.setOnClickListener {
            Toast.makeText(requireContext(), "Cancel", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        dialog.show() // Showing the dialog here


    }

    private fun updatePassword() {


        val userData = retrieveUserDataFromSharedPreferences(requireContext())
        val userId = userData?.first
        val user = UpdatePasswordUser(
            userId,
            binding.eTextCurrentPassword.text.toString(),
            binding.eTextNewPassword.text.toString()
        )


        viewModel.updatePassword(userId ?: 0, user)


    }


    private fun initViewModel() {
        viewModel = ViewModelProvider(this, ChangePasswordViewModelFactory())
            .get(ChangePasswordViewModel::class.java)

    }

    private fun validateForm(): Boolean {
        val passwordText = binding.eTextNewPassword.text.toString()
        val confirmPassText = binding.eTextConfirmPassword.text.toString()
        val currentPasswordText = binding.eTextCurrentPassword.text.toString()
        val layoutNewPassword: TextInputLayout = binding.layoutNewPassword
        val layoutConfirmPass: TextInputLayout = binding.layoutConfirmPassword
        val layoutCurrentPass: TextInputLayout = binding.layoutCurrentPassword
        val newPasswordError = validNewPassword(passwordText)
        val confirmPasswordError = validConfirmPass(passwordText, confirmPassText)

        var isValidate = true
        if (newPasswordError != null) {
            layoutNewPassword.error = newPasswordError
            isValidate = false
        }

        if (confirmPasswordError != null) {
            layoutConfirmPass.error = confirmPasswordError
            isValidate = false
        }

        if (confirmPasswordError != null) {
            layoutConfirmPass.error = confirmPasswordError
            isValidate = false
        }

        if (currentPasswordText == "") {
            layoutCurrentPass.error = "Current Password Is Required"
            isValidate = false
        }


        return isValidate


    }


    private fun validNewPassword(passwordText: String): String? {


        if (passwordText == "") {
            return "New Password Is Required"
        } else if (passwordText.length < 8) {
            return "Minimum 8 Character Password"
        } else if (!passwordText.matches(".*[A-Z].*".toRegex())) {
            return "Must Contain 1 Upper-case Character"
        } else if (!passwordText.matches(".*[a-z].*".toRegex())) {
            return "Must Contain 1 Lower-case Character"
        } else if (!passwordText.matches(".*[@#\$%^&+=].*".toRegex())) {
            return "Must Contain 1 Special Character (@#\$%^&+=)"
        }

        return null
    }

    private fun validConfirmPass(passText: String, confirmPassText: String): String? {

        if (confirmPassText == "") {
            return "Confirm Password Is Required"
        } else if (passText != confirmPassText) {
            return "Confirm Password Must Same With Password"
        }
        return null
    }



    private fun validateCurrentPassword() {

        val layoutCurrentPassword: TextInputLayout = binding.layoutCurrentPassword
        val eTextCurrentPassword: TextInputEditText = binding.eTextCurrentPassword
        eTextCurrentPassword.addTextChangedListener(object : TextWatcher {
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

                layoutCurrentPassword.error = ""

            }

            override fun afterTextChanged(editable: Editable?) {
                // This method is called to notify that the text has been changed and processed.
            }
        })
    }
    private fun validateOnChangeNewPassword() {

        val layoutNewPassword: TextInputLayout = binding.layoutNewPassword
        val eTextNewPassword: TextInputEditText = binding.eTextNewPassword
        eTextNewPassword.addTextChangedListener(object : TextWatcher {
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
                val passwordText = charSequence.toString()
                val error = validNewPassword(passwordText)

                if (error != null) {
                    layoutNewPassword.error = error
                } else {
                    layoutNewPassword.error = ""
                }
            }

            override fun afterTextChanged(editable: Editable?) {
                // This method is called to notify that the text has been changed and processed.
            }
        })
    }

    private fun validateOnChangeNewConfirmPassword() {
        val layoutConfirmPass: TextInputLayout = binding.layoutConfirmPassword
        val eTextConfirmPass: TextInputEditText = binding.eTextConfirmPassword
        val eTextPass: TextInputEditText = binding.eTextNewPassword
        eTextConfirmPass.addTextChangedListener(object : TextWatcher {
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
                val confirmPassText = charSequence.toString()
                val error = validConfirmPass(eTextPass.text.toString(), confirmPassText)

                if (error != null) {
                    layoutConfirmPass.error = error
                } else {
                    layoutConfirmPass.error = ""
                }

            }

            override fun afterTextChanged(editable: Editable?) {
                // This method is called to notify that the text has been changed and processed.
            }
        })
    }


}