import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bait2073mobileapplicationdevelopment.screens.personalized.StartPersonalizedViewModel

class StartPersonalizedViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StartPersonalizedViewModel::class.java)) {
            // Replace with the actual constructor parameters for StartPersonalizedViewModel
            return StartPersonalizedViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}