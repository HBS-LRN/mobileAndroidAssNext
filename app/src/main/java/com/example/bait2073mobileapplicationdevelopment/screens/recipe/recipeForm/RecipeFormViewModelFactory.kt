import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bait2073mobileapplicationdevelopment.screens.recipe.recipeForm.RecipeFormViewModel

class RecipeFormViewModelFactory() :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeFormViewModel::class.java)) {
            return RecipeFormViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}