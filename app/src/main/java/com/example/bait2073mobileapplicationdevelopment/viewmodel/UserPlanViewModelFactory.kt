import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bait2073mobileapplicationdevelopment.database.HealthyLifeDatabase
import com.example.bait2073mobileapplicationdevelopment.repository.UserPlanRepository
import com.example.bait2073mobileapplicationdevelopment.viewmodel.UserPlanViewModel

class UserPlanViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserPlanViewModel::class.java)) {
            val dao = HealthyLifeDatabase.getDatabase(application).userPlanDao()
            val repository = UserPlanRepository(dao)
            return UserPlanViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}