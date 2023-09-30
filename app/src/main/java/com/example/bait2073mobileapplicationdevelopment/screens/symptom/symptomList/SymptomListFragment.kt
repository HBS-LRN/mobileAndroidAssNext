package com.example.bait2073mobileapplicationdevelopment.screens.symptom.symptomList


import SymptomListViewModelFactory
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import android.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bait2073mobileapplicationdevelopment.adapter.SymptomListAdapter
import android.widget.PopupMenu
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentSymptomListBinding
import com.example.bait2073mobileapplicationdevelopment.entities.Symptom
import com.example.bait2073mobileapplicationdevelopment.screens.symptom.symptomList.SymptomListFragmentDirections


class SymptomListFragment: Fragment(),SymptomListAdapter.SymptomClickListener, PopupMenu.OnMenuItemClickListener {
    private lateinit var binding: FragmentSymptomListBinding
    private lateinit var viewModel: SymptomListViewModel
    private lateinit var adapter: SymptomListAdapter
    lateinit var selectedSymptom:Symptom
    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSymptomListBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, SymptomListViewModelFactory(requireActivity().application)
        ).get(SymptomListViewModel::class.java)

        viewModel.getSymptomListObservable().observe(viewLifecycleOwner, Observer<List<Symptom?>> {symptomListResponse ->
            if(symptomListResponse == null) {
                Toast.makeText(requireContext(), "no result found...", Toast.LENGTH_LONG).show()
            } else {
                val symptomList = symptomListResponse.filterNotNull().toMutableList()
                Log.i("haha", "$symptomList")
                adapter.setData(symptomList)
                adapter.notifyDataSetChanged()
            }
        })
        viewModel.getSymptomList()

        binding.lifecycleOwner = viewLifecycleOwner
        binding.symptomListViewModel= viewModel

        val symptomRecyclerView = binding.recycleView

        symptomRecyclerView.setHasFixedSize(true)
        symptomRecyclerView.layoutManager = LinearLayoutManager(context)
        adapter = SymptomListAdapter(requireContext(), this)
        symptomRecyclerView.adapter = adapter

        viewModel.symptomListMut.observe(viewLifecycleOwner, Observer { data ->
                adapter.setData(data as List<Symptom>)
        })
        observeSymptomDeletion()
        searchSymptom()

        binding.addSymptomBtn.setOnClickListener {
            val action =
                SymptomListFragmentDirections.actionSymptomListFragment2ToCreateSymptomFragment(0)
            this.findNavController().navigate(action)
        }
        binding.deleteImg.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Long-press to delete a symptom.",
                Toast.LENGTH_SHORT
            ).show()
        }
        return binding.root
    }

    private fun searchSymptom() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false;
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    adapter.filterList(newText)
                }
                return true
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getSymptomList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 1000) {
            viewModel.getSymptomList()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun popUpDisplay(cardView: CardView) {

        val popup = PopupMenu(requireContext(),cardView)
        popup.setOnMenuItemClickListener(this)
        popup.inflate(R.menu.pop_up_menu)
        popup.show()
    }

    override fun onItemClicked(symptom: Symptom) {

        val action =
            SymptomListFragmentDirections.actionSymptomListFragment2ToCreateSymptomFragment(
                symptom.id ?: 0
            )
        this.findNavController().navigate(action)

    }

    override fun onLongItemClicked(symptom: Symptom, cardView: CardView) {
        selectedSymptom = symptom
        popUpDisplay(cardView)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.delete_note){

            viewModel.deleteSymptom(selectedSymptom)
        }
        return false
    }

    private fun observeSymptomDeletion() {
        viewModel.getDeleteSymptomObservable().observe(viewLifecycleOwner, Observer<Symptom?> { deletedSymptom ->
            if (deletedSymptom == null) {
                Toast.makeText(requireContext(), "Cannot Delete Symptom", Toast.LENGTH_SHORT).show()
            } else {
                showSuccessDialog()
                viewModel.getSymptomList()

            }
        })
    }



    private fun showSuccessDialog(){
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.custom_dialog_success)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(false) // Optional
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation

        val okay: Button = dialog.findViewById(R.id.btn_okay)
        val cancel: Button = dialog.findViewById(R.id.btn_cancel)

        okay.setOnClickListener {
            dialog.dismiss()
        }

        cancel.setOnClickListener {
            Toast.makeText(requireContext(), "Cancel", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        dialog.show()
    }

}