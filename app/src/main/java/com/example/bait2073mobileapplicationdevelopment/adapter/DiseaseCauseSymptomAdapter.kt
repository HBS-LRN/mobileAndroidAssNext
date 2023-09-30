package com.example.bait2073mobileapplicationdevelopment.adapter

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.view.Gravity
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
import com.example.bait2073mobileapplicationdevelopment.data.DiseasedataClass
import com.example.bait2073mobileapplicationdevelopment.entities.Disease
import com.example.bait2073mobileapplicationdevelopment.entities.DiseaseHospital_Room
import com.example.bait2073mobileapplicationdevelopment.entities.DiseaseSymptom_Room
import com.example.bait2073mobileapplicationdevelopment.entities.Disease_Symptom
import com.example.bait2073mobileapplicationdevelopment.entities.Symptom
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetDiseaseDataService
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetDiseaseSymptomDataService
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetSymptomDataService
import com.example.bait2073mobileapplicationdevelopment.retrofitclient.RetrofitClientInstance
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiseaseCauseSymptomAdapter(private val context : Context) : RecyclerView.Adapter<DiseaseCauseSymptomAdapter.DiseaseCauseSymptomViewHolder>() {

    private val apiService = RetrofitClientInstance.retrofitInstance!!.create(
        GetDiseaseSymptomDataService::class.java)
    private val apiService2 = RetrofitClientInstance.retrofitInstance!!.create(
        GetSymptomDataService::class.java)
    var diseaseSymptomList = mutableListOf<Disease_Symptom>()
    var fullList = mutableListOf<Disease_Symptom>()

    var diseaseSymptomRoomList = mutableListOf<DiseaseSymptom_Room>()
    var fullRoomList = mutableListOf<DiseaseSymptom_Room>()

    var symptomDescription : String = ""
    private var currentPopupWindow: PopupWindow? = null

    private var ctx: Context? = null

    fun setData(newData: List<Disease_Symptom>) {
        fullList.clear()
        fullList.addAll(newData)
        diseaseSymptomList.clear()
        diseaseSymptomList.addAll(fullList)
        notifyDataSetChanged()
    }
    fun setRoomData(newData: List<DiseaseSymptom_Room>) {
        fullRoomList.clear()
        fullRoomList.addAll(newData)
        diseaseSymptomRoomList.clear()
        diseaseSymptomRoomList.addAll(fullRoomList)
        notifyDataSetChanged()
    }

    inner class DiseaseCauseSymptomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val causeSymptomCard = itemView.findViewById<CardView>(R.id.cause_symptom_layout)
        val symptomName = itemView.findViewById<TextView>(R.id.cause_symptom_name)
        val symptomImage = itemView.findViewById<ImageView>(R.id.cause_symptom_img)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiseaseCauseSymptomViewHolder {
        ctx = parent.context
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.recycleview_cause_symptom, parent, false)
        return DiseaseCauseSymptomViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DiseaseCauseSymptomViewHolder, position: Int) {
        if(isNetworkAvailable(context)){
        val currentSymptom = diseaseSymptomList[position]
        val symptomId= currentSymptom.symptom_id
        Log.i("diseasesymptomlist", "$diseaseSymptomList")

        apiService2.getSymptom(symptomId).enqueue(object : Callback<Symptom> {
            override fun onResponse(call: Call<Symptom>, response: Response<Symptom>) {
                if (response.isSuccessful) {
                    val symptom = response.body()
                    Log.i("symptommatch", "$symptom")
                    if (symptom != null) {

                        val symptomName = symptom.symptom_name
                        symptomDescription = symptom.symptom_description.toString()
                        val symptomImage = symptom.symptom_image
                        if (symptomImage.isNullOrBlank()) {
                            Log.e("noimage", "noimage")
                            Picasso.get().load(R.drawable.image_symptom).into(holder.symptomImage)
                        } else {
                            Glide.with(ctx!!)
                                .load(symptomImage)
                                .fitCenter()
                                .into(holder.symptomImage)

                        }
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
                holder.symptomName.text = "Unknown Symptom"
            }
        })
        }else{
            val currentSymptomRoom = diseaseSymptomRoomList[position]
            val symptomName = currentSymptomRoom.symptom_name
            holder.symptomName.text = symptomName
            symptomDescription = currentSymptomRoom.symptom_description.toString()
            val symptomImage = currentSymptomRoom.symptom_image
            if (symptomImage.isNullOrBlank()) {
                Log.e("noimage", "noimage")
                Picasso.get().load(R.drawable.image_symptom).into(holder.symptomImage)
            } else {
                Glide.with(ctx!!)
                    .load(symptomImage)
                    .fitCenter()
                    .into(holder.symptomImage)
            }

        }

        holder.causeSymptomCard.setOnClickListener{
            currentPopupWindow?.dismiss()
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val popupView = inflater.inflate(R.layout.fragment_popup_symptom, null)
            val popupWindow = PopupWindow(
                popupView,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )

            val popupDescription = popupView.findViewById<TextView>(R.id.popupSymptomDescription)
            popupDescription.text = symptomDescription
            popupWindow.showAsDropDown(holder.causeSymptomCard)

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
        return diseaseSymptomList.size
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

}



