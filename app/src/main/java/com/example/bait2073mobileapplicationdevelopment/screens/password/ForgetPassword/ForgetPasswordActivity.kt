package com.example.bait2073mobileapplicationdevelopment.screens.password.ForgetPassword

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.databinding.ActivityLoginBinding
import com.example.bait2073mobileapplicationdevelopment.databinding.ActivityRequestEmailBinding
import com.example.bait2073mobileapplicationdevelopment.databinding.ActivityResetPasswordBinding
import com.example.bait2073mobileapplicationdevelopment.entities.AutenticateEmailUser
import com.example.bait2073mobileapplicationdevelopment.entities.LoginUser
import com.example.bait2073mobileapplicationdevelopment.entities.ResetPasswordUser
import com.example.bait2073mobileapplicationdevelopment.entities.UpdatePasswordUser
import com.example.bait2073mobileapplicationdevelopment.screens.auth.Login.LoginActivity
import com.example.bait2073mobileapplicationdevelopment.screens.auth.Login.LoginViewModel
import com.example.bait2073mobileapplicationdevelopment.screens.auth.SignUp.SignUpActivity
import com.example.bait2073mobileapplicationdevelopment.screens.fragment.MainFragment
import com.example.bait2073mobileapplicationdevelopment.screens.fragment.StaffMainFragment
import com.example.bait2073mobileapplicationdevelopment.screens.password.ChangePassword.ChangePasswordViewModel
import com.example.bait2073mobileapplicationdevelopment.screens.password.RequestEmail.RequestEmailViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ForgetPasswordActivity : AppCompatActivity() {


    private lateinit var binding: ActivityResetPasswordBinding
    lateinit var viewModel: ForgetPasswordViewModel
    private lateinit var dialog: Dialog
    private  var email: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) //This Line will hide the status bar from the screen


        binding = ActivityResetPasswordBinding.inflate(layoutInflater)


        setContentView(binding.root)

        try{
            email = intent.getStringExtra("email")
        }catch(e :Exception){
            e.printStackTrace()
        }

        initViewModel()
        validateOnChangeNewPassword()
        validateOnChangeNewConfirmPassword()
        updatePasswordObservable()
        binding.submitButton.setOnClickListener {

            if (validateForm()) {
                resetPassword()
            }
        }


    }

    private fun resetPassword() {



        val user = ResetPasswordUser(
            email,
            binding.eTextNewPassword.text.toString()
        )
        viewModel.updatePassword(user)


    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this, ForgetPasswordViewModelFactory())
            .get(ForgetPasswordViewModel::class.java)
    }
    private fun updatePasswordObservable() {
        viewModel.getUpdatePasswordObservable()
            .observe(this, Observer<ResetPasswordUser?> {
                if (it == null) {
                   Toast.makeText(this,"Password Incorrect",Toast.LENGTH_SHORT)
                } else {
                    showSuccessDialog()
                }
            })
    }

    private fun showSuccessDialog() {
        dialog = Dialog(this)
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
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }

        cancel.setOnClickListener {
            Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        dialog.show() // Showing the dialog here


    }

    private fun validateForm(): Boolean {
        val passwordText = binding.eTextNewPassword.text.toString()
        val confirmPassText = binding.eTextConfirmPassword.text.toString()
        val layoutNewPassword: TextInputLayout = binding.layoutNewPassword
        val layoutConfirmPass: TextInputLayout = binding.layoutConfirmPassword
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