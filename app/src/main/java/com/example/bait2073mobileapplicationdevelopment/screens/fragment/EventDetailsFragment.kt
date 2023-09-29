package com.example.bait2073mobileapplicationdevelopment.screens.fragment

import android.R.attr.height
import android.R.attr.width
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentEventDetailsBinding
import com.example.bait2073mobileapplicationdevelopment.entities.Event
import com.example.bait2073mobileapplicationdevelopment.entities.EventParticipants
import com.example.bait2073mobileapplicationdevelopment.entities.EventParticipantsTable
import com.example.bait2073mobileapplicationdevelopment.entities.User
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetEventDataService
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetEventParticipantsDataService
import com.example.bait2073mobileapplicationdevelopment.retrofitclient.RetrofitClientInstance
import com.example.bait2073mobileapplicationdevelopment.screens.admin.AdminForm.AdminFormViewModel
import com.example.bait2073mobileapplicationdevelopment.screens.admin.AdminForm.AdminFormViewModelFactory
import com.example.bait2073mobileapplicationdevelopment.screens.admin.EventForm.EventFormViewModelFactory
import com.example.bait2073mobileapplicationdevelopment.screens.event.EventForm.EventFormViewModel
import com.example.bait2073mobileapplicationdevelopment.screens.event.EventForm.ManageEventFragmentDirections
import com.example.bait2073mobileapplicationdevelopment.screens.event.EventParticipants.EventParticipantsViewModelFactory
import com.example.bait2073mobileapplicationdevelopment.screens.eventParticipants.EventParticipantsParticipants.EventParticipantsViewModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Response
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Locale


class EventDetailsFragment : Fragment() {
    private lateinit var dialog: Dialog
    private lateinit var binding: FragmentEventDetailsBinding
    private lateinit var viewModel: EventFormViewModel
    private lateinit var viewModelUser: AdminFormViewModel
    private lateinit var viewModelEventParticipants: EventParticipantsViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_event_details, container, false)


        val linkText = binding.getDestinationText.text
        val mSpannableString = SpannableString(linkText)
        mSpannableString.setSpan(UnderlineSpan(), 0, mSpannableString.length, 0)
        binding.getDestinationText.text = mSpannableString

        val args = EventDetailsFragmentArgs.fromBundle(requireArguments())
         val eventId = args.eventId

        Log.e("eventId", "${eventId}")

        initViewModel()
        if (eventId != 0) {
            loadEventData(eventId)
        }


        viewModel.observeCountdown(viewLifecycleOwner, Observer { countdownValue ->
            binding.txtStatus.text = countdownValue

        })

        if(args.joined){
            binding.submitButton.setBackgroundColor(Color.parseColor("#ca403e"))
            binding.submitButton.setText("Cancel")
//                viewModelEventParticipants.deleteEventPart(selectedEvent)
        }

        binding.submitButton.setOnClickListener{
            if(args.joined){
                deleteEventParticipantsData()
            }else{
                addEventParticipantsData()
            }

        }


        return binding.root
    }

    private fun initViewModel() {
//        viewModel = ViewModelProvider(
//            this,
//            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
//        ).get(EventFormViewModel::class.java)
//
//        viewModelUser = ViewModelProvider(
//            this,
//            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
//        ).get(AdminFormViewModel::class.java)
//
//        viewModelEventParticipants = ViewModelProvider(
//            this,
//            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
//        ).get(EventParticipantsViewModel::class.java)


        viewModel = ViewModelProvider(
            this,
            EventFormViewModelFactory(requireActivity().application)
        ).get(EventFormViewModel::class.java)

        viewModelUser = ViewModelProvider(
            this,
            AdminFormViewModelFactory()
        ).get(AdminFormViewModel::class.java)

        viewModelEventParticipants= ViewModelProvider(
            this,
            EventParticipantsViewModelFactory(requireActivity().application)
        ).get(EventParticipantsViewModel::class.java)


        viewModelEventParticipants.getEventPartSizeObserverable().observe(viewLifecycleOwner,Observer<Int?>{
                eventListResponse->
            if (eventListResponse==null){
            }else{

                val eventListSize = eventListResponse
                binding.numberGoing.text = eventListSize.toString()
                Log.i("viewModelPart","initViewModel:\n"+"$eventListSize")
            }
        })

        val args = EventDetailsFragmentArgs.fromBundle(requireArguments())
        val eventId = args.eventId
        viewModelEventParticipants.getEventsPartSize(eventId)

    }


    private fun addEventParticipantsData() {

        val userData = retrieveUserDataFromSharedPreferences(requireContext())
        val args = EventDetailsFragmentArgs.fromBundle(requireArguments())
        val eventId = args.eventId
        val userId = userData?.first
        val eventPart = EventParticipantsTable(
            null,
            eventId,
            userId,
            "Joined",
            )

        viewModelEventParticipants.createEventParticipants(eventPart) { isSuccess ->
            if (isSuccess) {
                showSuccessDialog()
            } else {
                Log.e("EventDetail","addEventParticipantsData")
            }
        }
    }


    private fun deleteEventParticipantsData() {

        val userData = retrieveUserDataFromSharedPreferences(requireContext())
        val args = EventDetailsFragmentArgs.fromBundle(requireArguments())
        val eventId = args.eventId
        val userId = userData?.first


        if (userId != null) {
            viewModelEventParticipants.deleteEventPart(args.eventId,userId)
            showSuccessDialog()
        }
    }
    private fun retrieveUserDataFromSharedPreferences(context: Context): Pair<Int, String>? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt(
            "UserId",
            -1
        ) // -1 is a default value if the key is not found
        val userName = sharedPreferences.getString(
            "UserName",
            null
        ) // null is a default value if the key is not found
        if (userId != -1 && userName != null) {
            return Pair(userId, userName)
        }
        return null
    }

    private fun showSuccessDialog(){
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.custom_dialog_success)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation

        val okay: Button = dialog.findViewById(R.id.btn_okay)
        val cancel: Button = dialog.findViewById(R.id.btn_cancel)

        okay.setOnClickListener {
            dialog.dismiss()
            val action = EventDetailsFragmentDirections.actionEventDetailsFragmentToEventFragment()
            findNavController().navigate(action)
        }

        cancel.setOnClickListener {
            Toast.makeText(requireContext(), "Cancel", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        dialog.show()


    }

//    private fun loadEventParticipantsData() {
//        viewModelUser.getLoadUserObservable().observe(viewLifecycleOwner, Observer<User?> {
//            if (it != null) {
//
//                binding.OrganizerName.setText(it.name)
//
//                val custImageView = binding.imageView5
//
//                if (!it.image.isNullOrBlank()) {
//
//                    Picasso.get().load(it.image).fit().into(custImageView)
//                } else{
//                    // If no image URL is available,  set a placeholder image or handle this case as needed.\
//                    Log.e("noimage", "noimage")
//                    Picasso.get().load(R.drawable.img_person).into(custImageView)
//                }
//
//            }
//        })
//        viewModelUser.getUserData()
//    }



    private fun loadUserWhoMadeEventData(user_id: Int?) {
        viewModelUser.getLoadUserObservable().observe(viewLifecycleOwner, Observer<User?> {
            if (it != null) {

                binding.OrganizerName.setText(it.name)

                val custImageView = binding.imageView5

                if (!it.image.isNullOrBlank()) {

                    Picasso.get().load(it.image).fit().into(custImageView)
                } else {
                    // If no image URL is available,  set a placeholder image or handle this case as needed.\
                    Log.e("noimage", "noimage")
                    Picasso.get().load(R.drawable.img_person).into(custImageView)
                }

            }
        })
        viewModelUser.getUserData(user_id)
    }

    private fun loadEventData(event_id: Int?) {
        viewModel.getLoadEventObservable().observe(viewLifecycleOwner, Observer<Event?> { event ->
            Log.e("eventId", "${event}")

            if (event != null) {
                binding.Title.setText(event.title)
                binding.textView2.setText(event.details)

                var date: String = formatDateTimeRange(event.date.toString())
                var monthAndDay: String = formatMonthAndDay(event.date.toString())
                binding.calDate.setText(date)
                binding.dateContainer.setText(monthAndDay)

                binding.textViewText.setText(event.address.toString())

                loadUserWhoMadeEventData(event.user_id)

                viewModel.startCountdown(event.date.toString())
                val eventImageView = binding.itemImage

                binding.getDestinationText.setOnClickListener {
                    searchGoogleWithAddress(requireContext(), event.address.toString())
                }


                if (!event.image.isNullOrBlank()) {
                    Picasso.get()
                        .load(event.image)
                        .fit()
                        .error(R.drawable.event_default)
                        .into(eventImageView, object : Callback {
                            override fun onSuccess() {
                                Log.e("NICE", "image")
                            }

                            override fun onError(e: Exception?) {
                                Log.e("NotNice", "${e}")

                            }
                        })
                } else {
                    Log.e("noimagesdad", "noimage")
                    Picasso.get().load(R.drawable.event_default).into(eventImageView)
                }
            }
        })



        viewModel.getEventData(event_id)
    }

    fun formatDateTimeRange(dateTimeString: String): String {
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat =
                SimpleDateFormat("EEE, MMMM d, yyyy 'at' hh:mm a", Locale.getDefault())
            val date = inputFormat.parse(dateTimeString)

            if (date != null) {
                val formattedDate = outputFormat.format(date)
                return formattedDate
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return dateTimeString
    }

    fun formatMonthAndDay(dateTimeString: String): String {
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMM\nd", Locale.getDefault())
            val date = inputFormat.parse(dateTimeString)

            if (date != null) {
                val formattedDate = outputFormat.format(date)
                return formattedDate
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return dateTimeString
    }

    private fun searchGoogleWithAddress(context: Context, address: String) {
        try {
            val escapedQuery = URLEncoder.encode(address, "UTF-8")
            val uri = Uri.parse("http://www.google.com/search?q=$escapedQuery")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}
