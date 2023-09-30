import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseHospitalList.DiseaseHospitalListViewModel

class DiseaseHospitalListViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiseaseHospitalListViewModel::class.java)) {
            return DiseaseHospitalListViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}