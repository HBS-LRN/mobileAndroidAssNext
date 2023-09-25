package com.example.bait2073mobileapplicationdevelopment.screens.auth.SignUp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.bait2073mobileapplicationdevelopment.databinding.ActivitySignUpBinding
import com.example.bait2073mobileapplicationdevelopment.entities.RegisterUser
import com.example.bait2073mobileapplicationdevelopment.screens.auth.Login.LoginActivity
import com.example.bait2073mobileapplicationdevelopment.screens.profile.Gender.RequestGenderActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var viewModel: SignUpViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) //This Line will hide the status bar from the screen

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initViewModel()
        createUserObservable()


        binding.callSignUp.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        binding.signupButton.setOnClickListener {

            if (validateForm()) {


                createUser()

            }
        }


        validateOnChangePassword()
        validateOnChangeEmail()
        validateOnChangeConfirmPassword()
        validateOnChangeUserName()
    }


    private fun createUser() {

        val user = RegisterUser(
            null,
            binding.eTextEmail.text.toString(),
            binding.eTextUserName.text.toString(),
            binding.eTextPassword.text.toString(),


        )
        viewModel.createUser(user)


    }


    private fun createUserObservable() {
        viewModel.getCreateNewUserObservable().observe(this, Observer<RegisterUser?> {
            if (it == null) {
                binding.layoutEmail.error = "Email Already Registered, Please Try Another Email"
                Toast.makeText(this,"Cannot Create User",Toast.LENGTH_SHORT)
            } else {

                saveUserDataToSharedPreferences(this, it.id ?:0, it.name)
                startRequestGenderActivity()
            }
        })
    }
    private fun initViewModel() {
        viewModel = ViewModelProvider(this, SignUpViewModelFactory())
            .get(SignUpViewModel::class.java)


    }

    private fun saveUserDataToSharedPreferences(context: Context, userId: Int, userName: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("UserId", userId)
        editor.putString("UserName", userName)
        editor.apply()
    }

    private fun validateForm(): Boolean {
        val passwordText = binding.eTextPassword.text.toString()
        val emailText = binding.eTextEmail.text.toString()
        val confirmPassText = binding.eTextConfirmPassword.text.toString()
        val usernameText = binding.eTextUserName.text.toString()
        val layoutPassword: TextInputLayout = binding.layoutPassword
        val layoutEmail: TextInputLayout = binding.layoutEmail
        val layoutConfirmPass: TextInputLayout = binding.layoutConfirmPassword
        val layoutUserName: TextInputLayout = binding.layoutUsername
        val passwordError = validPassword(passwordText)
        val emailError = validEmail(emailText)
        val confirmPasswordError = validConfirmPass(passwordText, confirmPassText)
        val nameError = validUserName(usernameText)
        var isValidate = true
        if (passwordError != null) {
            layoutPassword.error = passwordError
            isValidate = false
        }

        if (emailError != null) {
            layoutEmail.error = emailError
            isValidate = false
        }
        if (confirmPasswordError != null) {
            layoutConfirmPass.error = confirmPasswordError
            isValidate = false
        }
        if (nameError != null) {
            layoutUserName.error = nameError
            isValidate = false
        }

        return isValidate


    }

    private fun startRequestGenderActivity() {
        val intent = Intent(this, RequestGenderActivity::class.java)
        startActivity(intent)
    }


    private fun validateOnChangePassword() {

        val layoutPassword: TextInputLayout = binding.layoutPassword
        val eTextPassword: TextInputEditText = binding.eTextPassword
        eTextPassword.addTextChangedListener(object : TextWatcher {
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
                val error = validPassword(passwordText)

                if (error != null) {
                    layoutPassword.error = error
                } else {
                    layoutPassword.error = ""
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

    private fun validateOnChangeConfirmPassword() {
        val layoutConfirmPass: TextInputLayout = binding.layoutConfirmPassword
        val eTextConfirmPass: TextInputEditText = binding.eTextConfirmPassword
        val eTextPass: TextInputEditText = binding.eTextPassword
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


    private fun validPassword(passwordText: String): String? {

        if (passwordText.length < 8) {
            return "Minimum 8 Character Password"
        }
        if (!passwordText.matches(".*[A-Z].*".toRegex())) {
            return "Must Contain 1 Upper-case Character"
        }
        if (!passwordText.matches(".*[a-z].*".toRegex())) {
            return "Must Contain 1 Lower-case Character"
        }
        if (!passwordText.matches(".*[@#\$%^&+=].*".toRegex())) {
            return "Must Contain 1 Special Character (@#\$%^&+=)"
        }

        return null
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

    private fun validConfirmPass(passText: String, confirmPassText: String): String? {

        if (confirmPassText == "") {
            return "Confirm Password Is Required"
        } else if (passText != confirmPassText) {
            return "Confirm Password Must Same With Password"
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


}