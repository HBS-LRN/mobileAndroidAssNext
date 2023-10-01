import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bait2073mobileapplicationdevelopment.screens.symptom.symptomForm.SymptomFormViewModel

class SymptomFormViewModelFactory() :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SymptomFormViewModel::class.java)) {
            return SymptomFormViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}