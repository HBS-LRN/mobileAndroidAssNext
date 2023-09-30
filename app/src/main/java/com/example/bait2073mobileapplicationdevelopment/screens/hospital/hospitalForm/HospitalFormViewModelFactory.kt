import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bait2073mobileapplicationdevelopment.screens.hospital.hospitalForm.HospitalFormViewModel

class HospitalFormViewModelFactory() :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HospitalFormViewModel::class.java)) {
            return HospitalFormViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}