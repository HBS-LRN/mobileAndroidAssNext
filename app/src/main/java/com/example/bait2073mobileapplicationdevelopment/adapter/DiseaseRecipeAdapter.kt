package com.example.bait2073mobileapplicationdevelopment.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.entities.Disease_Recipe
import com.example.bait2073mobileapplicationdevelopment.entities.Recipe
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetDiseaseRecipeDataService
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetRecipeDataService
import com.example.bait2073mobileapplicationdevelopment.retrofitclient.RetrofitClientInstance
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiseaseRecipeAdapter(private val context : Context) : RecyclerView.Adapter<DiseaseRecipeAdapter.DiseaseRecipeViewHolder>() {

    private val apiService = RetrofitClientInstance.retrofitInstance!!.create(
        GetDiseaseRecipeDataService::class.java
    )
    private val apiService2 = RetrofitClientInstance.retrofitInstance!!.create(
        GetRecipeDataService::class.java
    )
    var diseaseRecipeList = mutableListOf<Disease_Recipe>()
    var fullList = mutableListOf<Disease_Recipe>()

    private var currentPopupWindow: PopupWindow? = null

    private var ctx: Context? = null

    fun setData(newData: List<Disease_Recipe>) {
        fullList.clear()
        fullList.addAll(newData)
        diseaseRecipeList.clear()
        diseaseRecipeList.addAll(fullList)
        notifyDataSetChanged()
    }

    inner class DiseaseRecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recipeCard = itemView.findViewById<CardView>(R.id.disease_recipe_layout)
        val recipeName = itemView.findViewById<TextView>(R.id.recipe_name)
        val recipeImage = itemView.findViewById<ImageView>(R.id.recipe_image)
    }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiseaseRecipeViewHolder {
            ctx = parent.context
            val itemView =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycleview_disease_recipe, parent, false)
            return DiseaseRecipeViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: DiseaseRecipeViewHolder, position: Int) {
            val currentRecipe = diseaseRecipeList[position]
            val recipeId = currentRecipe.recipe_id
            var recipeDescription: String = ""
            var recipeServings: Int = 0
            Log.i("diseasesymptomlist", "$diseaseRecipeList")

            apiService2.getRecipe(recipeId).enqueue(object : Callback<Recipe> {
                override fun onResponse(call: Call<Recipe>, response: Response<Recipe>) {
                    if (response.isSuccessful) {
                        val recipe = response.body()
                        Log.i("recipematch", "$recipe")
                        if (recipe != null) {

                            val recipeName = recipe.recipe_name
                            recipeDescription = recipe.recipe_description.toString()
                            recipeServings = recipe.recipe_servings!!
                            val recipeImage = recipe.recipe_image
                            if (recipeImage.isNullOrBlank()) {
                                Log.e("noimage", "noimage")
                                Picasso.get().load(R.drawable.diseases_recipe)
                                    .into(holder.recipeImage)
                            } else {
                                Glide.with(ctx!!)
                                    .load(recipeImage)
                                    .fitCenter()
                                    .into(holder.recipeImage)

                            }
                            holder.recipeName.text = recipeName
                        } else {
                            holder.recipeName.text = "Unknown Recipe"
                        }
                    } else {
                        // Handle the case where the API request for recipe details is not successful
                        holder.recipeName.text = "Unknown Recipe"
                    }
                }

                override fun onFailure(call: Call<Recipe>, t: Throwable) {
                    // Handle network failures here
                    holder.recipeName.text = "Unknown Recipe"
                }
            })

            holder.recipeCard.setOnClickListener {
                currentPopupWindow?.dismiss()

                //create a popup window
                //create a popup window
                val inflater =
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val popupView = inflater.inflate(R.layout.fragment_popup_recipe, null)
                val popupWindow = PopupWindow(
                    popupView,
                    WindowManager.LayoutParams.WRAP_CONTENT, // Width
                    WindowManager.LayoutParams.WRAP_CONTENT
                )
                // Set the content of the popup
                val popupDescription = popupView.findViewById<TextView>(R.id.popupRecipeDescription)
                val popupServings = popupView.findViewById<TextView>(R.id.popupRecipeServings)
                popupDescription.text = recipeDescription
                val recipeServingsText =
                    "Recipe Servings : " + "${recipeServings.toString()}" + "pax"
                popupServings.text = recipeServingsText
                popupWindow.showAsDropDown(holder.recipeCard)

                currentPopupWindow = popupWindow
                popupWindow.isTouchable = true
                popupWindow.isOutsideTouchable = true
                popupWindow.setOnDismissListener {
                    currentPopupWindow = null
                }

                popupView.setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        popupWindow.dismiss()
                        return@setOnTouchListener true
                    } else {
                        false
                    }
                }

            }
        }

        override fun getItemCount(): Int {
            return diseaseRecipeList.size
        }
    }


