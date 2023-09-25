package com.example.bait2073mobileapplicationdevelopment.adapter

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.data.DiseasedataClass
import com.example.bait2073mobileapplicationdevelopment.entities.Disease
import com.example.bait2073mobileapplicationdevelopment.entities.Disease_Symptom
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetDiseaseDataService
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetDiseaseSymptomDataService
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetSymptomDataService
import com.example.bait2073mobileapplicationdevelopment.retrofitclient.RetrofitClientInstance

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiseaseResultAdapter(private val context : Context) :  RecyclerView.Adapter<DiseaseResultAdapter.DiseaseResultViewHolder>() {

    private val apiService = RetrofitClientInstance.retrofitInstance!!.create(
        GetDiseaseDataService::class.java)

    var diseaseSymptomList = mutableListOf<Disease_Symptom>()
    var fullList = mutableListOf<Disease_Symptom>()
    val diseaseList = mutableListOf<DiseasedataClass>()

    private var ctx: Context? = null

    fun setData(newData: List<Disease_Symptom>) {
        fullList.clear()
        fullList.addAll(newData)
        diseaseSymptomList.clear()
        diseaseSymptomList.addAll(fullList)
        notifyDataSetChanged()
    }

    inner class DiseaseResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val diseaseName = itemView.findViewById<TextView>(R.id.checker_symptom_name)
        val diseaseImageView = itemView.findViewById<ImageView>(R.id.checker_symptom_img)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiseaseResultViewHolder {
        ctx = parent.context
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_symptoms_row, parent, false)
        return DiseaseResultViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: DiseaseResultViewHolder, position: Int) {
        val currentDiseaseSymptom = diseaseSymptomList[position]

        Log.i("diseasesymptomlist", "$diseaseSymptomList")

        // Lookup disease name based on disease ID
        val diseaseId = currentDiseaseSymptom.disease_id
        apiService.getDisease(diseaseId).enqueue(object : Callback<Disease> {
            override fun onResponse(call: Call<Disease>, response: Response<Disease>) {
                if (response.isSuccessful) {
                    val disease = response.body()
                    Log.i("diseasematch", "$disease")
                    if (disease != null) {
                        // Access the disease_name from the disease object
                        val diseaseName = disease.disease_name
                        val diseaseImage = disease.disease_image
                        if (diseaseImage.isNullOrBlank()) {
                            Log.e("noimage", "noimage")
                            Picasso.get().load(R.drawable.image_disease)
                                .into(holder.diseaseImageView)
                        } else {
                            Glide.with(ctx!!)
                                .load(diseaseImage)
                                .fitCenter()
                                .into(holder.diseaseImageView)
                        }
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
    }

    override fun getItemCount(): Int {
        return diseaseSymptomList.size
    }
}





