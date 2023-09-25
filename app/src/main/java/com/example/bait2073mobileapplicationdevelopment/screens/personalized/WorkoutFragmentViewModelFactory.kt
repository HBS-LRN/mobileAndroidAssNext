import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bait2073mobileapplicationdevelopment.screens.personalized.WorkoutFragmentViewModel

class WorkoutFragmentViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkoutFragmentViewModel::class.java)) {
            // Replace with the actual constructor parameters for WorkoutFragmentViewModel
            return WorkoutFragmentViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}