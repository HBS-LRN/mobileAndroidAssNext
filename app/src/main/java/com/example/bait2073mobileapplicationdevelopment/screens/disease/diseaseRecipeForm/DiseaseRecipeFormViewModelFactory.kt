import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseRecipeForm.DiseaseRecipeFormViewModel

class DiseaseRecipeFormViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiseaseRecipeFormViewModel::class.java)) {
            return DiseaseRecipeFormViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}