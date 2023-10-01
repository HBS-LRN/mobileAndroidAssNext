import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bait2073mobileapplicationdevelopment.repository.DiseaseRepository
import com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseForm.DiseaseFormViewModel

class DiseaseFormViewModelFactory(
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiseaseFormViewModel::class.java)) {
            return DiseaseFormViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}