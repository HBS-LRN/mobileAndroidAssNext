package com.example.bait2073mobileapplicationdevelopment.adapter

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
import com.example.bait2073mobileapplicationdevelopment.entities.Symptom
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetDiseaseDataService
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetDiseaseSymptomDataService
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetSymptomDataService
import com.example.bait2073mobileapplicationdevelopment.retrofitclient.RetrofitClientInstance
import com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseList.DiseaseListViewModel
import com.example.bait2073mobileapplicationdevelopment.screens.symptom.symptomForm.SymptomFormViewModel
import com.example.bait2073mobileapplicationdevelopment.screens.symptom.symptomList.SymptomListViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiseaseSymptomListAdapter(private val context : Context, val listener: DiseaseSymptomClickListener) : RecyclerView.Adapter<DiseaseSymptomListAdapter.DiseaseSymptomListViewHolder>() {

    private val apiService = RetrofitClientInstance.retrofitInstance!!.create(
        GetDiseaseDataService::class.java)
    private val apiService2 = RetrofitClientInstance.retrofitInstance!!.create(
        GetSymptomDataService::class.java)
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

    inner class DiseaseSymptomListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val diseaseSymptomListCard = itemView.findViewById<CardView>(R.id.subs_layout)
        val diseaseSymptomId = itemView.findViewById<TextView>(R.id.tv_subsID)
        val diseaseName = itemView.findViewById<TextView>(R.id.tv_DiseaseName)
        val symptomName = itemView.findViewById<TextView>(R.id.tv_subsName)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiseaseSymptomListViewHolder {
        ctx = parent.context
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.recycleview_disease_subs, parent, false)
        return DiseaseSymptomListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DiseaseSymptomListViewHolder, position: Int) {
        val currentDiseaseSymptom = diseaseSymptomList[position]
        holder.diseaseSymptomId.text = currentDiseaseSymptom.id.toString()
        Log.i("diseasesymptomlist", "$diseaseSymptomList")
//        Log.i("diseaselist", "$diseaseList")
//        Log.i("symptomlist", "$symptomList")
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
                        val diseaseData = DiseasedataClass(disease.id,disease.disease_name)
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

        val symptomId = currentDiseaseSymptom.symptom_id

        apiService2.getSymptom(symptomId).enqueue(object : Callback<Symptom> {
            override fun onResponse(call: Call<Symptom>, response: Response<Symptom>) {
                if (response.isSuccessful) {
                    val symptom = response.body()
                    Log.i("symptommatch", "$symptom")
                    if (symptom != null) {
                        // Access the symptom_name from the disease object
                        val symptomName = symptom.symptom_name
                        holder.symptomName.text = symptomName
                    } else {
                        holder.symptomName.text = "Unknown Symptom"
                    }
                } else {
                    // Handle the case where the API request for disease details is not successful
                    holder.symptomName.text = "Unknown Symptom"
                }
            }

            override fun onFailure(call: Call<Symptom>, t: Throwable) {
                // Handle network failures here
                holder.symptomName.text = "Unknown Symptom"
            }
        })


        holder.diseaseSymptomListCard.setOnLongClickListener {

            listener.onLongItemClicked(diseaseSymptomList[holder.adapterPosition], holder.diseaseSymptomListCard)
            true
        }
    }

    fun filterList(search: String) {
        diseaseSymptomList.clear()

        for (item in fullList) {
            // Lookup disease name based on disease ID
            val diseaseId = item.disease_id
            val matchingDisease = diseaseList.find { it.diseaseId == diseaseId }

            if (matchingDisease != null) {
                val diseaseName = matchingDisease.diseaseName

                // Check if the disease name contains the search query
                if (diseaseName.lowercase().contains(search.lowercase())) {
                    diseaseSymptomList.add(item)
                }
            }
        }

        Log.i("dsymptomlist", "$diseaseSymptomList")
        notifyDataSetChanged()
    }


    interface DiseaseSymptomClickListener{
        fun onItemClicked(diseaseSymptom: Disease_Symptom)
        fun onLongItemClicked(diseaseSymptom: Disease_Symptom,cardView: CardView)
    }
    override fun getItemCount(): Int {
        return diseaseSymptomList.size
    }

}



