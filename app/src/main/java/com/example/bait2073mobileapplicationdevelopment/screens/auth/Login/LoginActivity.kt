package com.example.bait2073mobileapplicationdevelopment.screens.auth.Login


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bait2073mobileapplicationdevelopment.databinding.ActivityLoginBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.entities.LoginUser
import com.example.bait2073mobileapplicationdevelopment.screens.admin.AdminForm.AdminFormFragmentDirections
import com.example.bait2073mobileapplicationdevelopment.screens.auth.SignUp.SignUpActivity
import com.example.bait2073mobileapplicationdevelopment.screens.fragment.MainFragment
import com.example.bait2073mobileapplicationdevelopment.screens.fragment.StaffMainFragment
import com.example.bait2073mobileapplicationdevelopment.screens.password.RequestEmail.RequestEmailActivity

class LoginActivity : AppCompatActivity() {
//    var callSignUp: Button? = null
//    var login_btn: Button? = null
//    var image: ImageView? = null
//    var logoText: TextView? = null
//    var sloganText: TextView? = null
//    var username: TextInputLayout? = null
//    var password: TextInputLayout? = null

    private lateinit var binding: ActivityLoginBinding
    lateinit var viewModel: LoginViewModel
    private lateinit var dialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) //This Line will hide the status bar from the screen


        binding = ActivityLoginBinding.inflate(layoutInflater)


        setContentView(binding.root)


        initViewModel()
        getAuthenticateUserObservable()

        validateOnChangeEmail()
        validateOnChangePassword()

        getInternetConnectionObservable()
        binding.callSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)

        }
        binding.callForgetPassword.setOnClickListener {
            val intent = Intent(this, RequestEmailActivity::class.java)
            startActivity(intent)

        }

        binding.loginButton.setOnClickListener {




            if(validateForm()) {
                val user = LoginUser(
                    null,
                    binding.eTextEmail.text.toString(),
                    binding.eTextPassword.text.toString(),
                    "",
                    null
                )

                viewModel.authenticate(user)
            }
        }


    }


    private fun validateForm() :Boolean {
        var isValidate = true
        val passwordText = binding.eTextPassword.text.toString()
        val emailText = binding.eTextEmail.text.toString()
        val layoutPassword: TextInputLayout = binding.layoutPassword
        val layoutEmail: TextInputLayout = binding.layoutEmail

        if (passwordText == "") {
            layoutPassword.error = "Password Is Required"
            isValidate = false
        }
        if (emailText == "") {
            layoutEmail.error = "Email Is Required"
            isValidate = false
        }
        return isValidate


    }

    private fun getInternetConnectionObservable() {

        viewModel.networkErrorLiveData.observe(this) { _ ->
            // Show your network error dialog here
            showLostInternetDialog()
        }
    }

    private fun getAuthenticateUserObservable() {
        viewModel.getAuthenticateUserObservable().observe(this, Observer<LoginUser?> {
            if (it == null) {
                val layoutEmail: TextInputLayout = binding.layoutEmail
                val layoutPass: TextInputLayout = binding.layoutPassword
                layoutEmail.error = "Invalid Email And/Or Password"
                layoutPass.error = "Invalid Email And/Or Password"
            } else {
                saveUserDataToSharedPreferences(this, it.id ?: 0, it.name)

                var role = it.role
                Log.e("cutomerintent", "$role")
                if (it.role == 0) {
                    //customer intent
                    Log.e("cutomerintent", "cutomerintent")
                    val intent = Intent(this, MainFragment::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(this, StaffMainFragment::class.java)
                    startActivity(intent)
                }

            }
        })
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

    }


    private fun showLostInternetDialog(){
        dialog = Dialog(this)
        dialog.setContentView(R.layout.custom_dialog_no_internet)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(false) // Optional
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation // Setting the animations to dialog

        val okay: Button = dialog.findViewById(R.id.btn_okay)
        val cancel: Button = dialog.findViewById(R.id.btn_cancel)

        okay.setOnClickListener {

            dialog.dismiss()


        }

        cancel.setOnClickListener {

            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        dialog.show() // Showing the dialog here


    }
    private fun saveUserDataToSharedPreferences(context: Context, userId: Int, userName: String) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("UserId", userId)
        editor.putString("UserName", userName)
        editor.apply()
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



                    layoutEmail.error = ""
                }



            override fun afterTextChanged(editable: Editable?) {
                // This method is called to notify that the text has been changed and processed.
            }
        })
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


                layoutPassword.error = ""

            }


            override fun afterTextChanged(editable: Editable?) {
                // This method is called to notify that the text has been changed and processed.
            }
        })
    }
}
