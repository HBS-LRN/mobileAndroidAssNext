import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseRecipeList.DiseaseRecipeListViewModel

class DiseaseRecipeListViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiseaseRecipeListViewModel::class.java)) {
            return DiseaseRecipeListViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}