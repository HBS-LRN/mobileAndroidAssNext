package com.example.bait2073mobileapplicationdevelopment.screens.hospital.hospitalList

import HospitalListViewModelFactory
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
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.adapter.HospitalListAdapter
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentHospitalListBinding
import com.example.bait2073mobileapplicationdevelopment.entities.Hospital
import com.example.bait2073mobileapplicationdevelopment.screens.hospital.hospitalList.HospitalListViewModel
import com.example.bait2073mobileapplicationdevelopment.screens.hospital.hospitalList.HospitalListFragmentDirections


class HospitalListFragment : Fragment(), HospitalListAdapter.HospitalClickListener, PopupMenu.OnMenuItemClickListener {
    private lateinit var binding: FragmentHospitalListBinding
    private lateinit var viewModel: HospitalListViewModel
    private lateinit var adapter: HospitalListAdapter
    lateinit var selectedHospital: Hospital
    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHospitalListBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this,
            HospitalListViewModelFactory(requireActivity().application)).get(HospitalListViewModel::class.java)

        viewModel.getHospitalListObservable().observe(viewLifecycleOwner, Observer<List<Hospital?>> { hospitalListResponse ->
            if(hospitalListResponse == null) {
                Toast.makeText(requireContext(), "no result found...", Toast.LENGTH_LONG).show()
            } else {
//               adapter.setData(it.toList().get(1))
                val hospitalList = hospitalListResponse.filterNotNull().toMutableList()
                Log.i("haha", "$hospitalList")
                adapter.setData(hospitalList)
                adapter.notifyDataSetChanged()
            }
        })
        viewModel.getHospitalList()

        binding.lifecycleOwner = viewLifecycleOwner
        binding.hospitalListViewModel= viewModel

        val hospitalRecyclerView = binding.recycleView

        hospitalRecyclerView.setHasFixedSize(true)
        hospitalRecyclerView.layoutManager = LinearLayoutManager(context)
        adapter = HospitalListAdapter(requireContext(), this)
        hospitalRecyclerView.adapter = adapter

        viewModel.hospitalListMut.observe(viewLifecycleOwner, Observer { data ->
            adapter.setData(data as List<Hospital>)
        })
        observeHospitalDeletion()
        searchHospital()

        binding.addHospitalBtn.setOnClickListener {
            val action =
                HospitalListFragmentDirections.actionHospitalListFragment2ToCreateHospitalFragment(0)
            this.findNavController().navigate(action)
        }
        binding.deleteImg.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Long-press to delete a hospital.",
                Toast.LENGTH_SHORT
            ).show()
        }
        return binding.root
    }

    private fun searchHospital() {
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
        viewModel.getHospitalList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 1000) {
            viewModel.getHospitalList()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun popUpDisplay(cardView: CardView) {

        val popup = PopupMenu(requireContext(),cardView)
        popup.setOnMenuItemClickListener(this)
        popup.inflate(R.menu.pop_up_menu)
        popup.show()
    }

    override fun onItemClicked(hospital: Hospital) {

        val action =
            HospitalListFragmentDirections.actionHospitalListFragment2ToCreateHospitalFragment(
               hospital.id ?: 0
            )
        this.findNavController().navigate(action)

    }

    override fun onLongItemClicked(hospital: Hospital, cardView: CardView) {
        selectedHospital = hospital
        popUpDisplay(cardView)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.delete_note){

            viewModel.deleteHospital(selectedHospital)
        }
        return false
    }

    private fun observeHospitalDeletion() {
        viewModel.getDeleteHospitalObservable().observe(viewLifecycleOwner, Observer<Hospital?> { deletedHospital ->
            if (deletedHospital == null) {
                Toast.makeText(requireContext(), "Cannot Delete Hospital", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show()
                viewModel.getHospitalList()

            }
        })
    }



    private fun showSuccessDialog(){
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.custom_dialog_success)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(false) // Optional
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation // Setting the animations to dialog

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