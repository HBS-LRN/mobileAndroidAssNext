package com.example.bait2073mobileapplicationdevelopment.screens.symptom.symptomForm

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.bait2073mobileapplicationdevelopment.adapter.SymptomListAdapter
import com.example.bait2073mobileapplicationdevelopment.database.HealthyLifeDatabase
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentSymptomListBinding
import com.example.bait2073mobileapplicationdevelopment.entities.Symptom
import com.example.bait2073mobileapplicationdevelopment.viewmodel.SymptomViewModel


class SymptomFragment : Fragment(), SymptomListAdapter.SymptomClickListener, PopupMenu.OnMenuItemClickListener {

    private lateinit var dialog: Dialog
    private lateinit var binding:FragmentSymptomListBinding
    private lateinit var database: HealthyLifeDatabase
    lateinit var viewModel: SymptomViewModel
    lateinit var adapter: SymptomListAdapter
    lateinit var selectedSymptom: Symptom

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentSymptomListBinding.inflate(inflater, container, false)
        initUi()
        viewModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)).get(SymptomViewModel::class.java)
        viewModel.allSymptoms.observe(viewLifecycleOwner){list->
            list?.let{
                adapter.setData(list)
            }
        }

        database = HealthyLifeDatabase.getDatabase(requireContext())
        return binding.root
    }

    private fun initUi() {
        binding.recycleView.setHasFixedSize(true)
        binding.recycleView.layoutManager = StaggeredGridLayoutManager(1, LinearLayout.VERTICAL)
        adapter = SymptomListAdapter(requireContext(), this)
        binding.recycleView.adapter = adapter

        //register for activity
//        val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
//                result -> if(result.resultCode == Activity.RESULT_OK ){
//            val note = result.data?.getSerializableExtra("note") as? Note
//            if(note != null ){
//                viewModel.insertNote(note)
//            }
//
//        }
//        }
        binding.addSymptomBtn.setOnClickListener {
            //navigate user to add
//            val intent = Intent(this,AddNote::class.java)
//            getContent.launch(intent)
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false;
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    adapter.filterList(newText)
                }
                return true;
            }
        })
    }

    override fun onItemClicked(symptom: Symptom) {
        TODO("Not yet implemented")
    }

    override fun onLongItemClicked(symptom: Symptom, cardView: CardView) {
        TODO("Not yet implemented")
    }

    override fun onMenuItemClick(p0: MenuItem?): Boolean {
        TODO("Not yet implemented")
    }
}
