import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseSymptomList.DiseaseSymptomListViewModel

class DiseaseSymptomListViewModelFactory(private val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiseaseSymptomListViewModel::class.java)) {
            return DiseaseSymptomListViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}