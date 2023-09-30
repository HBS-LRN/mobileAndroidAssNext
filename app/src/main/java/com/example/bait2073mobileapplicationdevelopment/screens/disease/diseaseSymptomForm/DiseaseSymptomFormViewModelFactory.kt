import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseSymptomForm.DiseaseSymptomFormViewModel

class DiseaseSymptomFormViewModelFactory() :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiseaseSymptomFormViewModel::class.java)) {
            return DiseaseSymptomFormViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}