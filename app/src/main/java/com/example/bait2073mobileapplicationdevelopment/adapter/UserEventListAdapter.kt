package com.example.bait2073mobileapplicationdevelopment.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.entities.Event


import com.example.bait2073mobileapplicationdevelopment.screens.admin.AdminList.AdminListFragmentDirections
import com.example.bait2073mobileapplicationdevelopment.screens.event.EventList.EventListFragmentDirections
import com.example.bait2073mobileapplicationdevelopment.screens.eventParticipants.EventParticipantsParticipants.EventParticipantsViewModel
import com.example.bait2073mobileapplicationdevelopment.screens.fragment.EventFragmentDirections
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserEventListAdapter (private val context: Context, val listener:EventClickListerner,val parentFragment: Fragment):
    RecyclerView.Adapter<UserEventListAdapter.EventViewHolder>(){


    private var ctx: Context?=null
    var eventList = mutableListOf<Event>()
    var eventPartList = mutableListOf<Event>()
    var fullList = mutableListOf<Event>()
    lateinit var viewModelEventParticipants: EventParticipantsViewModel


    fun setData(arrData: List<Event>) {
        eventList = arrData as ArrayList<Event>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        ctx = parent.context
        return EventViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycleview_event,parent,false)
        )
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    inner class EventViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val event_layout= itemView.findViewById<CardView>(R.id.event_list_user_layout)
        val eventTitle = itemView.findViewById<TextView>(R.id.item_title)
//        val eventDetails = itemView.findViewById<TextView>(R.id.item_details)
        val eventDate = itemView.findViewById<TextView>(R.id.textView4)
        val btn = itemView.findViewById<Button>(R.id.component4)
    }

    fun extractMonthAndDay(dateString: String): Pair<String, String> {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val date: Date = inputFormat.parse(dateString) ?: Date()

        val monthFormat = SimpleDateFormat("MMM", Locale.US)
        val dayFormat = SimpleDateFormat("dd", Locale.US)

        val month = monthFormat.format(date)
        val day = dayFormat.format(date)

        return Pair(month, day)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val currentEvent = eventList[position]


        holder.eventTitle.text = currentEvent.title?: ""

        val (month, day) = extractMonthAndDay(currentEvent.date.toString())

        holder.eventDate.text =  "${month}\n${day}"

//        holder.eventDetails.text = currentEvent.details?:""

        Log.e("EventListAdapter","${currentEvent.image} event image")

        val eventImageView =  holder.itemView.findViewById<ImageView>(R.id.item_image_list_event)

//        if (position < eventPartList.size) {
//            val currentEventPart = eventPartList[position]
//            if (currentEventPart.toString().equals(currentEvent.id.toString())) {
//                holder.btn.setText("Joined")
//            } else {
//                holder.btn.setText("Join")
//            }
//        }

        if (!currentEvent.image.isNullOrBlank()) {
            Picasso.get().load(currentEvent.image).fit().into(eventImageView)
        }else{
            Log.e("EventListAdapter","event image")
            Picasso.get().load(R.drawable.event_default)
        }

        val button =  holder.itemView.findViewById<Button>(R.id.component4)

        button.setOnClickListener {
            val action = currentEvent.id?.let { it1 ->
                EventFragmentDirections.actionEventFragmentToEventDetailsFragment(
                    it1,false
                )
            }
            if (action != null) {
                parentFragment.findNavController().navigate(action)
            }
        }


        holder.event_layout.setOnClickListener {
            val action = currentEvent.id?.let { it1 ->
                EventFragmentDirections.actionEventFragmentToEventDetailsFragment(
                    it1,false
                )
            }
            if (action != null) {
                parentFragment.findNavController().navigate(action)
            }
        }



//        val args = EventDetailsFragmentArgs.fromBundle(requireArguments())
//        val eventId = args.eventId
//        viewModelEventParticipants.getEventsPartSize(eventId)

//        holder.event_layout.setOnClickListener{
//            listener.onItemClicked(eventList[holder.adapterPosition])
//        }
//        holder.event_layout.setOnLongClickListener{
//            listener.onLongItemClicked(eventList[holder.adapterPosition],holder.event_layout)
//            true
//        }
    }


    fun updateList(newList:List<Event>){
        fullList.clear()
        fullList.addAll(newList)
        eventList.clear()
        eventList.addAll(fullList)

        notifyDataSetChanged()
    }

//    fun updateEventPartList(newEventPartList:List<Event>){
//
//        eventPartList.addAll(newEventPartList)
//        notifyDataSetChanged()
//    }


    fun filterList(search : String){
        eventList.clear()
        for(item in fullList){
            if (item.title?.lowercase()?.contains(search.lowercase())==true||item.date?.lowercase()?.contains(search.lowercase())==true){
                eventList.add(item)
            }
        }
        Log.i("eventList","$eventList")
        notifyDataSetChanged()
    }


    interface EventClickListerner{
        fun onItemClicked(event: Event)
        fun onLongItemClicked(event: Event, cardView: CardView)
    }


}
