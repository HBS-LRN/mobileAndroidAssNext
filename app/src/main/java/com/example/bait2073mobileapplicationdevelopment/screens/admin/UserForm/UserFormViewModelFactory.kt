package com.example.bait2073mobileapplicationdevelopment.screens.admin.UserForm



import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bait2073mobileapplicationdevelopment.screens.admin.UserForm.UserFormViewModel

class UserFormViewModelFactory : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserFormViewModel::class.java)) {
            return UserFormViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}