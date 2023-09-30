import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bait2073mobileapplicationdevelopment.screens.hospital.hospitalList.HospitalListViewModel

class HospitalListViewModelFactory(private val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HospitalListViewModel::class.java)) {
            return HospitalListViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}