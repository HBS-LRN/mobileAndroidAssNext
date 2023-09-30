import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseHospitalForm.DiseaseHospitalFormViewModel

class DiseaseHospitalFormViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiseaseHospitalFormViewModel::class.java)) {
            return DiseaseHospitalFormViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}