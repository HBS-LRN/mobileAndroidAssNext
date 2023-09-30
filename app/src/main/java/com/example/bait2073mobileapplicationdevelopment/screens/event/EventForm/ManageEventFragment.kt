package com.example.bait2073mobileapplicationdevelopment.screens.event.EventForm


import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentManageEventBinding
import com.example.bait2073mobileapplicationdevelopment.entities.Event
import com.example.bait2073mobileapplicationdevelopment.screens.admin.EventForm.EventFormViewModelFactory
import com.example.bait2073mobileapplicationdevelopment.screens.admin.UserList.UserRatingViewModel
import com.example.bait2073mobileapplicationdevelopment.screens.admin.UserRating.UserRatingViewModelFactory
import com.example.bait2073mobileapplicationdevelopment.screens.event.EventNotificationReceiver
import com.example.bait2073mobileapplicationdevelopment.screens.event.EventParticipants.EventParticipantsViewModelFactory
import com.example.bait2073mobileapplicationdevelopment.screens.event.InternetConnectionError
import com.example.bait2073mobileapplicationdevelopment.screens.eventParticipants.EventParticipantsParticipants.EventParticipantsViewModel
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ManageEventFragment : Fragment() {

    private lateinit var viewModel: EventFormViewModel
    private lateinit var viewModelEventParticipants: EventParticipantsViewModel
    private lateinit var binding: FragmentManageEventBinding
    private val PICK_IMAGE_REQUEST = 1
    private val CAPTURE_IMAGE_REQUEST = 2
    private var selectedImageBitmap: Bitmap? = null
    private lateinit var dialog: Dialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        //val event_id = intent.getStringExtra("event_id")
        binding = FragmentManageEventBinding.inflate(inflater, container, false)

        setupTimePicker()
        setupDatePicker()
        initViewModel()
        createEventObservable()
        getInternetConnectionObservable()
//        createNotificationChannel()
        val args = ManageEventFragmentArgs.fromBundle(requireArguments())
        val event_id = args.eventId

        if (event_id != 0) {
            loadEventData(event_id)
        }


        binding.createEventButton.setOnClickListener {
            validateForm()

            createEvent(event_id,selectedImageBitmap)
        }


        binding.uploadButton.setOnClickListener {
//            pickImage()
            showImagePickerDialog()
        }


//        viewModelEventParticipants = ViewModelProvider(
//            this,
//            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
//        ).get(EventParticipantsViewModel::class.java)

        viewModelEventParticipants = ViewModelProvider(
            this,
            EventParticipantsViewModelFactory(requireActivity().application)
        ).get(EventParticipantsViewModel::class.java)


        viewModelEventParticipants.getEventPartSizeObserverable().observe(viewLifecycleOwner,Observer<Int?>{
                eventListResponse->
            if (eventListResponse==null){
            }else{

                if(args.add){
                    binding.numGoingAdminText.setText("")
                }else{
                    val eventListSize = eventListResponse
                    binding.numGoingAdminText.setText("Total user joined : "+eventListSize.toString())
                    Log.i("viewModelPart","initViewModel:\n"+"$eventListSize")
                }
            }
        })


        viewModelEventParticipants.getEventsPartSize(event_id)

        return binding.root
    }

//    private fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                EventNotificationReceiver.CHANNEL_ID,
//                "Event Notification Channel",
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//    }


    private fun getInternetConnectionObservable() {
        viewModel.networkErrorLiveData.observe(viewLifecycleOwner) { _ ->
            val internetConnectionError = InternetConnectionError(requireContext())
            internetConnectionError.showLostInternetDialog()
        }
    }

    private fun setupDatePicker() {
        val datePicker = binding.datePicker1

        // Get the current date
        val currentDate = Calendar.getInstance()

        // Set the minimum date for the DatePicker to today's date
        datePicker.minDate = currentDate.timeInMillis

        // Optionally, you can set the initial date to today
        datePicker.init(
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DAY_OF_MONTH)
        ) { _, year, month, dayOfMonth ->
            // Date selected listener (optional)
            // You can perform actions when the user selects a date
        }
    }

    private fun setupTimePicker() {
        val timePicker = binding.timePicker1

        val currentTime = Calendar.getInstance()

        // Set the minimum time for the TimePicker to the current time
        timePicker.hour = currentTime.get(Calendar.HOUR_OF_DAY)
        timePicker.minute = currentTime.get(Calendar.MINUTE)

        // Optionally, you can set a time selected listener (if needed)
        timePicker.setOnTimeChangedListener { _, _, _ ->
            // Time selected listener (optional)
            // You can perform actions when the user selects a time
        }
    }

    private fun pickImage() {
        val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
        startActivityForResult(intent, 101)
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Gallery", "Camera")
        AlertDialog.Builder(requireContext())
            .setTitle("Choose an option")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openGallery()
                    1 -> openCamera()
                }
            }
            .show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAPTURE_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    val selectedImageUri = data?.data
                    if (selectedImageUri != null) {
                        if (selectedImageUri.toString().endsWith(".jpg", true)) {
                            binding.eventImgAdmin.setImageURI(selectedImageUri)
                            val imageBitmap =
                                MediaStore.Images.Media.getBitmap(requireContext().contentResolver, selectedImageUri)
                            selectedImageBitmap = imageBitmap
                        } else {
                            Toast.makeText(requireContext(), "Please select a JPG image.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                CAPTURE_IMAGE_REQUEST -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap?
                    if (imageBitmap != null) {
                        selectedImageBitmap = imageBitmap
                    }
                }
            }
        }
    }

    private fun loadEventData(event_id: Int?) {
        viewModel.getLoadEventObservable().observe(viewLifecycleOwner, Observer<Event?> { event ->
            if (event != null) {
                binding.eTextTitle.setText(event.title)
                binding.eTextDetails.setText(event.details)
                binding.createEventButton.text = "Update"
                binding.createEventTitletv.text = "Update Event"
                binding.eTextAddress.setText(event.address.toString())

                if (!event.date.isNullOrEmpty()) {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val date = dateFormat.parse(event.date)

                    if (date != null) {
                        val calendar = Calendar.getInstance()
                        calendar.time = date

                        val year = calendar.get(Calendar.YEAR)
                        val month = calendar.get(Calendar.MONTH)
                        val day = calendar.get(Calendar.DAY_OF_MONTH)
                        val hour = calendar.get(Calendar.HOUR_OF_DAY)
                        val minute = calendar.get(Calendar.MINUTE)

                        // Set the date and time to your DatePicker and TimePicker
                        binding.datePicker1.updateDate(year, month, day)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            binding.timePicker1.hour = hour
                            binding.timePicker1.minute = minute
                        } else {
                            // For older Android versions
                            binding.timePicker1.currentHour = hour
                            binding.timePicker1.currentMinute = minute
                        }
                    }
                }

                val eventImageView = binding.eventImgAdmin
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
                    Log.e("noimage", "noimage")
                    Picasso.get().load(R.drawable.event_default).into(eventImageView)
                }
            }
        })
        viewModel.getEventData(event_id)
    }




    private fun createEvent(event_id: Int?, selectedImageBitmap: Bitmap?) {

        val imageData: String? = if (selectedImageBitmap != null) {
            encodeBitmapToBase64(selectedImageBitmap)
        } else {
            null
        }

        val datePicker = binding.datePicker1
        val timePicker = binding.timePicker1

        val year = datePicker.year
        val month = datePicker.month + 1
        val day = datePicker.dayOfMonth

        val hourOfDay: Int
        val minute: Int

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hourOfDay = timePicker.hour
            minute = timePicker.minute
        } else {
            hourOfDay = timePicker.currentHour
            minute = timePicker.currentMinute
        }

        val formattedDate = String.format("%04d-%02d-%02d %02d:%02d:00", year, month, day, hourOfDay, minute)

        val userData = retrieveUserDataFromSharedPreferences(requireContext())

        val userId = userData?.first
        val userName = userData?.second
        val event = Event(
            null,
            binding.eTextTitle.text.toString(),
            binding.eTextDetails.text.toString(),
            imageData,
            formattedDate,
            binding.eTextAddress.text.toString(),
            "Active",
            userId
        )


        if (event_id == 0)
            viewModel.createEvent(event)
        else
            viewModel.updateEvent(event_id ?: 0, event)
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


    private fun encodeBitmapToBase64(bitmap: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val byteArray = baos.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
    private fun initViewModel() {
//        viewModel = ViewModelProvider(
//            this,
//            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
//        ).get(EventFormViewModel::class.java)

        viewModel = ViewModelProvider(
            this,
            EventFormViewModelFactory(requireActivity().application)
        ).get(EventFormViewModel::class.java)

    }

    private fun createEventObservable() {
        viewModel.getCreateNewEventObservable().observe(viewLifecycleOwner, Observer<Event?> {
            if (it == null) {
                ManageEventFragmentDirections.actionManageEventFragmentToEventListFragment()
            } else {
                showSuccessDialog()
            }
        })
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
            val action = ManageEventFragmentDirections.actionManageEventFragmentToEventListFragment()
            findNavController().navigate(action)
        }

        cancel.setOnClickListener {
            Toast.makeText(requireContext(), "Cancel", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        dialog.show()


    }


    private fun validateForm() : Boolean{
        val editTextTitle: TextInputEditText = binding.eTextTitle
        val title = editTextTitle.text.toString()
            .trim { it <= ' ' }

        val editTextDesc: EditText = binding.eTextDetails
        val desc = editTextDesc.text.toString()
            .trim { it <= ' ' }

        val editTextAddress: EditText = binding.eTextAddress
        val add = editTextAddress.text.toString()
            .trim { it <= ' ' }

        if (title.isEmpty()) {
            binding.layoutTitle.error = "Title cannot be empty"
            return false
        } else{
            binding.layoutTitle.error = null
        }

         if(desc.isEmpty()) {
             binding.layoutDetails.error = "Details cannot be empty"
             return false
         }else{
             binding.layoutDetails.error = null
         }


        if (add.isEmpty()){
            binding.layoutAddress.error = "Address cannot be empty"
            return false
        }else{
            binding.layoutAddress.error = null
        }

            return true


    }

}