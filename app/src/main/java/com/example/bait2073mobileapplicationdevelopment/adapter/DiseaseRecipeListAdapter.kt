package com.example.bait2073mobileapplicationdevelopment.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.data.DiseasedataClass
import com.example.bait2073mobileapplicationdevelopment.entities.Disease
import com.example.bait2073mobileapplicationdevelopment.entities.Disease_Recipe
import com.example.bait2073mobileapplicationdevelopment.entities.Recipe
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetDiseaseDataService
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetRecipeDataService
import com.example.bait2073mobileapplicationdevelopment.retrofitclient.RetrofitClientInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiseaseRecipeListAdapter (private val context : Context, val listener: DiseaseRecipeClickListener) : RecyclerView.Adapter<DiseaseRecipeListAdapter.DiseaseRecipeListViewHolder>() {

    private val apiService = RetrofitClientInstance.retrofitInstance!!.create(
        GetDiseaseDataService::class.java
    )
    private val apiService2 = RetrofitClientInstance.retrofitInstance!!.create(
        GetRecipeDataService::class.java
    )
    var diseaseRecipeList = mutableListOf<Disease_Recipe>()
    var fullList = mutableListOf<Disease_Recipe>()
    val diseaseList = mutableListOf<DiseasedataClass>()

    private var ctx: Context? = null

    fun setData(newData: List<Disease_Recipe>) {
        fullList.clear()
        fullList.addAll(newData)
        diseaseRecipeList.clear()
        diseaseRecipeList.addAll(fullList)
        notifyDataSetChanged()
    }
//    fun setDiseaseData(newDData: List<Disease>) {
//        diseaseFullList.clear()
//        diseaseFullList.addAll(newDData)
//        diseaseList.clear()
//        diseaseList.addAll(diseaseFullList)
//        notifyDataSetChanged()
//    }
//
//fun setRecipeData(newSData:List<Symptom>){
//    symptomFullList.clear()
//    symptomFullList.addAll(newSData)
//    symptomList.clear()
//    symptomList.addAll(symptomFullList)
//    notifyDataSetChanged()
//}

    inner class DiseaseRecipeListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val diseaseRecipeListCard = itemView.findViewById<CardView>(R.id.subs_layout)
        val diseaseRecipeId = itemView.findViewById<TextView>(R.id.tv_subsID)
        val diseaseName = itemView.findViewById<TextView>(R.id.tv_DiseaseName)
        val recipeName = itemView.findViewById<TextView>(R.id.tv_subsName)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiseaseRecipeListViewHolder {
        ctx = parent.context
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycleview_disease_subs, parent, false)
        return DiseaseRecipeListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DiseaseRecipeListViewHolder, position: Int) {
        val currentDiseaseRecipe = diseaseRecipeList[position]
        holder.diseaseRecipeId.text = currentDiseaseRecipe.id.toString()
        Log.i("diseaseRecipelist", "$diseaseRecipeList")
//        Log.i("diseaselist", "$diseaseList")
//        Log.i("Recipelist", "$RecipeList")
        // Lookup disease name based on disease ID

        val diseaseId = currentDiseaseRecipe.disease_id

        apiService.getDisease(diseaseId).enqueue(object : Callback<Disease> {
            override fun onResponse(call: Call<Disease>, response: Response<Disease>) {
                if (response.isSuccessful) {
                    val disease = response.body()
                    Log.i("diseasematch", "$disease")
                    if (disease != null) {
                        // Access the disease_name from the disease object
                        val diseaseName = disease.disease_name
                        val diseaseData = DiseasedataClass(disease.id, disease.disease_name)
                        diseaseList.add(diseaseData)
                        holder.diseaseName.text = diseaseName
                    } else {
                        holder.diseaseName.text = "Unknown Disease"
                    }
                } else {
                    // Handle the case where the API request for disease details is not successful
                    holder.diseaseName.text = "Unknown Disease"
                }
            }

            override fun onFailure(call: Call<Disease>, t: Throwable) {
                // Handle network failures here
                holder.diseaseName.text = "Unknown Disease"
            }
        })

        val recipeId = currentDiseaseRecipe.recipe_id

        apiService2.getRecipe(recipeId).enqueue(object : Callback<Recipe> {
            override fun onResponse(call: Call<Recipe>, response: Response<Recipe>) {
                if (response.isSuccessful) {
                    val recipe = response.body()
                    Log.i("Recipematch", "$recipe")
                    if (recipe != null) {
                        // Access the recipe_name from the disease object
                        val recipeName = recipe.recipe_name
                        holder.recipeName.text = recipeName
                    } else {
                        holder.recipeName.text = "Unknown recipe"
                    }
                } else {
                    // Handle the case where the API request for disease details is not successful
                    holder.recipeName.text = "Unknown recipe"
                }
            }

            override fun onFailure(call: Call<Recipe>, t: Throwable) {
                // Handle network failures here
                holder.recipeName.text = "Unknown recipe"
            }
        })


        holder.diseaseRecipeListCard.setOnLongClickListener {

            listener.onLongItemClicked(
                diseaseRecipeList[holder.adapterPosition],
                holder.diseaseRecipeListCard
            )
            true
        }
    }

    fun filterList(search: String) {
        diseaseRecipeList.clear()

        for (item in fullList) {
            // Lookup disease name based on disease ID
            val diseaseId = item.disease_id
            val matchingDisease = diseaseList.find { it.diseaseId == diseaseId }

            if (matchingDisease != null) {
                val diseaseName = matchingDisease.diseaseName

                // Check if the disease name contains the search query
                if (diseaseName.lowercase().contains(search.lowercase())) {
                    diseaseRecipeList.add(item)
                }
            }
        }

        Log.i("dsymptomlist", "$diseaseRecipeList")
        notifyDataSetChanged()
    }


    interface DiseaseRecipeClickListener {
        fun onItemClicked(diseaseRecipe: Disease_Recipe)
        fun onLongItemClicked(diseaseRecipe: Disease_Recipe, cardView: CardView)
    }

    override fun getItemCount(): Int {
        return diseaseRecipeList.size
    }
}