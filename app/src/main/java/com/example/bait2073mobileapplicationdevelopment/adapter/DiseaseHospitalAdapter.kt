package com.example.bait2073mobileapplicationdevelopment.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.data.DiseasedataClass
import com.example.bait2073mobileapplicationdevelopment.entities.Disease_Hospital
import com.example.bait2073mobileapplicationdevelopment.entities.Hospital
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetDiseaseHospitalDataService
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetHospitalDataService
import com.example.bait2073mobileapplicationdevelopment.retrofitclient.RetrofitClientInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

class DiseaseHospitalAdapter (private val context : Context) : RecyclerView.Adapter<DiseaseHospitalAdapter.DiseaseHospitalViewHolder>() {

    private val apiService = RetrofitClientInstance.retrofitInstance!!.create(
        GetDiseaseHospitalDataService::class.java)
    private val apiService2 = RetrofitClientInstance.retrofitInstance!!.create(
        GetHospitalDataService::class.java)
    var diseaseHospitalList = mutableListOf<Disease_Hospital>()
    var fullList = mutableListOf<Disease_Hospital>()
    var hospitalAddress :String =""
    private var ctx: Context? = null

    fun setData(newData: List<Disease_Hospital>) {
        fullList.clear()
        fullList.addAll(newData)
        diseaseHospitalList.clear()
        diseaseHospitalList.addAll(fullList)
        notifyDataSetChanged()
    }

    inner class DiseaseHospitalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val hospitalSeqNum = itemView.findViewById<TextView>(R.id.sequence_num)
        val diseaseHospitalName = itemView.findViewById<TextView>(R.id.disease_hospital_name)
        val diseaseHospitalContact = itemView.findViewById<TextView>(R.id.disease_hospital_contact)
        val hospitalHowToGo = itemView.findViewById<Button>(R.id.disease_go_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiseaseHospitalViewHolder {
        ctx = parent.context
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.recycleview_disease_hospital, parent, false)
        return DiseaseHospitalViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DiseaseHospitalViewHolder, position: Int) {
        val currentHospital = diseaseHospitalList[position]
        val hospitalId= currentHospital.hospital_id
        val position2 = position +1
        Log.i("diseaseHospitallist", "$diseaseHospitalList")
        holder.hospitalSeqNum.text = position2.toString()
        apiService2.getHospital(hospitalId).enqueue(object : Callback<Hospital> {
            override fun onResponse(call: Call<Hospital>, response: Response<Hospital>) {
                if (response.isSuccessful) {
                    val hospital = response.body()
                    Log.i("hospitalmatch", "$hospital")
                    if (hospital != null) {
                        // Access the hospital info from the disease object
                        val hospitalName = hospital.hospital_name
                         hospitalAddress = hospital.hospital_address.toString()
                        val hospitalContact = hospital.hospital_contact

                        holder.diseaseHospitalName.text = hospitalName
                        holder.diseaseHospitalContact.text = hospitalContact
                    } else {
                        holder.diseaseHospitalName.text = "Unknown Hospital"

                    }
                } else {
                    // Handle the case where the API request for disease details is not successful
                    holder.diseaseHospitalName.text = "Unknown Hospital"
                }
            }

            override fun onFailure(call: Call<Hospital>, t: Throwable) {
                // Handle network failures here
                holder.diseaseHospitalName.text = "Unknown Hospital"
            }
        })

        holder.hospitalHowToGo.setOnClickListener{
            val hospitalAddress = hospitalAddress.replace(" ","+")
            Log.i("hospitaladdress", "$hospitalAddress")
//            // Create a web URI for the hospital's location
//            val mapUrl = "https://www.google.com/maps?q=$hospitalAddress"
//            Log.i("hospitaladdressURL", "$mapUrl")
//            // Create an intent with ACTION_VIEW and the geo URI
//            val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl))
//            Log.i("mapIntent", "$mapIntent")
//            // Check if there's an app that can handle this intent
//            if (mapIntent.resolveActivity(context.packageManager) != null) {
//                context.startActivity(mapIntent)
//            } else {
//                // Handle the case where no app can handle the intent
//                Toast.makeText(context, "No app or browser available.", Toast.LENGTH_SHORT).show()
//            }
            try {
                val escapedQuery = URLEncoder.encode(hospitalAddress, "UTF-8")
                val uri = Uri.parse("http://www.google.com/search?q=$escapedQuery")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                context.startActivity(intent)
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
        }

    }

    override fun getItemCount(): Int {
        return diseaseHospitalList.size
    }

}
