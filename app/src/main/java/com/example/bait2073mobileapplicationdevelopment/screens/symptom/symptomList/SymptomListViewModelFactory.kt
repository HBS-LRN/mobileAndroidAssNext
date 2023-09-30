import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bait2073mobileapplicationdevelopment.screens.symptom.symptomList.SymptomListViewModel

class SymptomListViewModelFactory(private val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SymptomListViewModel::class.java)) {
            return SymptomListViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}