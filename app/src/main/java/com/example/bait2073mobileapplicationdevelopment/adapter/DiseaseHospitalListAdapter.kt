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
import com.example.bait2073mobileapplicationdevelopment.entities.Disease_Hospital
import com.example.bait2073mobileapplicationdevelopment.entities.Hospital
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetDiseaseDataService
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetHospitalDataService
import com.example.bait2073mobileapplicationdevelopment.retrofitclient.RetrofitClientInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiseaseHospitalListAdapter  (private val context : Context, val listener: DiseaseHospitalClickListener) : RecyclerView.Adapter<DiseaseHospitalListAdapter.DiseaseHospitalListViewHolder>() {

    private val apiService = RetrofitClientInstance.retrofitInstance!!.create(
        GetDiseaseDataService::class.java
    )
    private val apiService2 = RetrofitClientInstance.retrofitInstance!!.create(
        GetHospitalDataService::class.java
    )
    var diseaseHospitalList = mutableListOf<Disease_Hospital>()
    var fullList = mutableListOf<Disease_Hospital>()
    val diseaseList = mutableListOf<DiseasedataClass>()

    private var ctx: Context? = null

    fun setData(newData: List<Disease_Hospital>) {
        fullList.clear()
        fullList.addAll(newData)
        diseaseHospitalList.clear()
        diseaseHospitalList.addAll(fullList)
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

    inner class DiseaseHospitalListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val diseaseHospitalListCard = itemView.findViewById<CardView>(R.id.subs_layout)
        val diseaseHospitalId = itemView.findViewById<TextView>(R.id.tv_subsID)
        val diseaseName = itemView.findViewById<TextView>(R.id.tv_DiseaseName)
        val hospitalName = itemView.findViewById<TextView>(R.id.tv_subsName)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiseaseHospitalListViewHolder {
        ctx = parent.context
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycleview_disease_subs, parent, false)
        return DiseaseHospitalListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DiseaseHospitalListViewHolder, position: Int) {
        val currentDiseaseHospital = diseaseHospitalList[position]
        holder.diseaseHospitalId.text = currentDiseaseHospital.id.toString()
        Log.i("diseaseHospitallist", "$diseaseHospitalList")
//        Log.i("diseaselist", "$diseaseList")
//        Log.i("Hospitallist", "$HospitalList")
        // Lookup disease name based on disease ID

        val diseaseId = currentDiseaseHospital.disease_id

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

        val hospitalId = currentDiseaseHospital.hospital_id

        apiService2.getHospital(hospitalId).enqueue(object : Callback<Hospital> {
            override fun onResponse(call: Call<Hospital>, response: Response<Hospital>) {
                if (response.isSuccessful) {
                    val hospital = response.body()
                    Log.i("Hospitalmatch", "$hospital")
                    if (hospital != null) {
                        // Access the Hospital_name from the disease object
                        val hospitalName = hospital.hospital_name
                        holder.hospitalName.text = hospitalName
                    } else {
                        holder.hospitalName.text = "Unknown hospital"
                    }
                } else {
                    // Handle the case where the API request for disease details is not successful
                    holder.hospitalName.text = "Unknown hospital"
                }
            }

            override fun onFailure(call: Call<Hospital>, t: Throwable) {
                // Handle network failures here
                holder.hospitalName.text = "Unknown hospital"
            }
        })


        holder.diseaseHospitalListCard.setOnLongClickListener {

            listener.onLongItemClicked(
                diseaseHospitalList[holder.adapterPosition],
                holder.diseaseHospitalListCard
            )
            true
        }
    }

    fun filterList(search: String) {
        diseaseHospitalList.clear()

        for (item in fullList) {
            // Lookup disease name based on disease ID
            val diseaseId = item.disease_id
            val matchingDisease = diseaseList.find { it.diseaseId == diseaseId }

            if (matchingDisease != null) {
                val diseaseName = matchingDisease.diseaseName

                // Check if the disease name contains the search query
                if (diseaseName.lowercase().contains(search.lowercase())) {
                    diseaseHospitalList.add(item)
                }
            }
        }

        Log.i("dsymptomlist", "$diseaseHospitalList")
        notifyDataSetChanged()
    }


    interface DiseaseHospitalClickListener {
        fun onItemClicked(diseaseHospital: Disease_Hospital)
        fun onLongItemClicked(diseaseHospital: Disease_Hospital, cardView: CardView)
    }

    override fun getItemCount(): Int {
        return diseaseHospitalList.size
    }
}