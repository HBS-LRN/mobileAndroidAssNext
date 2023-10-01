package com.example.bait2073mobileapplicationdevelopment.screens.disease;

import DiseaseListViewModelFactory
import DiseaseSymptomListViewModelFactory
import SymptomListViewModelFactory
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bait2073mobileapplicationdevelopment.adapter.DiseaseCauseSymptomAdapter
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentDiseaseCausesBinding
import com.example.bait2073mobileapplicationdevelopment.entities.Disease
import com.example.bait2073mobileapplicationdevelopment.entities.DiseaseSymptom_Room
import com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseList.DiseaseListViewModel
import com.example.bait2073mobileapplicationdevelopment.screens.disease.diseaseSymptomList.DiseaseSymptomListViewModel
import com.example.bait2073mobileapplicationdevelopment.screens.symptom.symptomList.SymptomListViewModel

class DiseaseCausesFragment : Fragment() {
    private lateinit var viewModel: DiseaseSymptomListViewModel
    private lateinit var adapter: DiseaseCauseSymptomAdapter
    private lateinit var binding: FragmentDiseaseCausesBinding
    private lateinit var diseaseViewModel : DiseaseListViewModel
    private lateinit var symptomViewModel : SymptomListViewModel
    private var diseaseName : String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiseaseCausesBinding.inflate(inflater, container, false)
        viewModel =ViewModelProvider(this, DiseaseSymptomListViewModelFactory(requireActivity().application)
        ).get(DiseaseSymptomListViewModel::class.java)

        diseaseViewModel = ViewModelProvider(this, DiseaseListViewModelFactory(requireActivity().application))
            .get(DiseaseListViewModel::class.java)

        symptomViewModel = ViewModelProvider(this, SymptomListViewModelFactory(requireActivity().application))
            .get(SymptomListViewModel::class.java)

        val args = DiseaseCausesFragmentArgs.fromBundle(requireArguments())
        val disease_id = args.diseaseId
        Log.i("lol is this disease id","$disease_id")
        adapter = DiseaseCauseSymptomAdapter(requireContext())

        val isNetworkAvailable = isNetworkAvailable(requireContext())

        if(isNetworkAvailable) {

            diseaseViewModel.getSpecificDisease(disease_id)
        diseaseViewModel.getSpecificDiseaseObservable().observe(viewLifecycleOwner, Observer<Disease?> { diseaseResponse ->
            if (diseaseResponse == null) {
                Toast.makeText(requireContext(), "no result found...", Toast.LENGTH_LONG).show()
            } else {
                val diseaseCauses = diseaseResponse.disease_causes
                Log.i("hahaaxxxx", "$diseaseCauses")

                binding.tvCauses.text = diseaseCauses
                diseaseName = diseaseResponse.disease_name

                adapter.notifyDataSetChanged()
            }
        })

        viewModel.getDiseaseSymptomData(disease_id)
        viewModel.removeDiseaseSymptomRoomFromLocalDatabase()

        viewModel.getLoadDiseaseSymptomObservable().observe(viewLifecycleOwner, Observer { diseaseSymptoms ->
            Log.i("finddaolemaaaaaa","$diseaseSymptoms")

            if (diseaseSymptoms != null) {
                for (diseaseSymptom in diseaseSymptoms) {
                    Log.i("specificsymptomid??","${diseaseSymptom.symptom_id}")
                    symptomViewModel.getSpecificSymptom(diseaseSymptom.symptom_id).observe(viewLifecycleOwner, Observer { specificSymptom ->
                        Log.i("specificsymptom??", "$specificSymptom")
                        val symptomId = specificSymptom?.id
                        val symptomName = specificSymptom?.symptom_name
                        val symptomImg = specificSymptom?.symptom_image
                        val symptomDesc = specificSymptom?.symptom_description

                        symptomId?.let {
                            if (symptomName != null) {
                                val diseaseSymptomRoom = DiseaseSymptom_Room(
                                    null,
                                    disease_id,
                                    diseaseName,
                                    it,
                                    symptomName,
                                    symptomImg,
                                    symptomDesc,
                                )
                                Log.i("finddaol??", "$diseaseSymptomRoom")
                                viewModel.insertDiseaseSymptomRoomDataIntoRoomDb(diseaseSymptomRoom)
                            }

                        }
                    })
                }

                // Update the adapter with the fetched data
                adapter.setData(diseaseSymptoms)
                adapter.notifyDataSetChanged()
            } else {
                Log.i("wandndn","$diseaseSymptoms")
                Toast.makeText(requireContext(), "No data found...", Toast.LENGTH_LONG).show()
            }
        })
        }else{
            viewModel.diseaseSymptomDataFromRoomDB.observe(viewLifecycleOwner, Observer { roomData ->
                if (roomData != null && roomData.isNotEmpty()) {
                    // Update the adapter with RoomDB data
                    adapter.setRoomData(roomData)
                    adapter.notifyDataSetChanged()
                } else {
                    // Handle the case where RoomDB data is not available
                    Log.i("roomdbdata", "No RoomDB data found...")
                }
        })
        }

        val causeSymptomRecyclerView = binding.recycleView
        causeSymptomRecyclerView.setHasFixedSize(true)
        causeSymptomRecyclerView.layoutManager = LinearLayoutManager(context)
        causeSymptomRecyclerView.adapter = adapter

        return binding.root
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 1000) {
            viewModel.getDiseaseSymptomList()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getDiseaseSymptomList()
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

}