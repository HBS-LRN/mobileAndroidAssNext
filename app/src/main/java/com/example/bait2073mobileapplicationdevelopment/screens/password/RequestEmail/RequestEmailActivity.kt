package com.example.bait2073mobileapplicationdevelopment.screens.password.RequestEmail

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.databinding.ActivityRequestEmailBinding
import com.example.bait2073mobileapplicationdevelopment.entities.AutenticateEmailUser
import com.example.bait2073mobileapplicationdevelopment.screens.auth.Login.LoginActivity
import com.example.bait2073mobileapplicationdevelopment.screens.password.RequestCode.RequestCodeActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class RequestEmailActivity  : AppCompatActivity() {
//    var callSignUp: Button? = null
//    var login_btn: Button? = null
//    var image: ImageView? = null
//    var logoText: TextView? = null
//    var sloganText: TextView? = null
//    var username: TextInputLayout? = null
//    var password: TextInputLayout? = null

    private lateinit var binding: ActivityRequestEmailBinding
    lateinit var viewModel: RequestEmailViewModel
    private lateinit var dialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) //This Line will hide the status bar from the screen


        binding = ActivityRequestEmailBinding.inflate(layoutInflater)


        setContentView(binding.root)


        initViewModel()
        loadEmailObservable()

        validateOnChangeEmail()

        binding.callLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }
        binding.submitButton.setOnClickListener {

            //customer intent
//            val intent = Intent(this, MainFragment::class.java)
//            startActivity(intent)


//            staff intent
//            val intent = Intent(this, StaffMainFragment::class.java)
//            startActivity(intent)


            if (validateForm()) {
                val user = AutenticateEmailUser(
                    null,
                    binding.eTextEmail.text.toString(),

                )

                viewModel.sendEmail(user)
            }
        }


    }


    private fun validateForm(): Boolean {
        var isValidate = true
        val emailText = binding.eTextEmail.text.toString()
        val layoutEmail: TextInputLayout = binding.layoutEmail
        if (emailText == "") {
            layoutEmail.error = "Email Is Required"
            isValidate = false
        }
        return isValidate


    }


    private fun initViewModel() {
        viewModel = ViewModelProvider(this, RequestEmailViewModelFactory())
            .get(RequestEmailViewModel::class.java)


    }

    private fun loadEmailObservable() {
        viewModel.getloadEmailObservable().observe(this, Observer<AutenticateEmailUser?> {
                if (it == null) {
                    binding.layoutEmail.error = "Email Does Not Exist"
                } else {
                    showSuccessDialog(it)
                }
            })
    }

    private fun showSuccessDialog(user:AutenticateEmailUser) {
        dialog = Dialog(this)
        dialog.setContentView(R.layout.custom_dialog_success_email)
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
            val intent = Intent(this, RequestCodeActivity::class.java)
            intent.putExtra("user",user)
            startActivity(intent)

            dialog.dismiss()

        }

        cancel.setOnClickListener {

            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        dialog.show() // Showing the dialog here


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
}
