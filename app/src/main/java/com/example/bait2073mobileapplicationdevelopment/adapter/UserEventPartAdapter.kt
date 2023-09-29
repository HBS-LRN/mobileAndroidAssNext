package com.example.bait2073mobileapplicationdevelopment.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.entities.Event
import com.example.bait2073mobileapplicationdevelopment.entities.EventParticipants
import com.example.bait2073mobileapplicationdevelopment.screens.fragment.EventFragment
import com.example.bait2073mobileapplicationdevelopment.screens.fragment.EventFragmentDirections
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.sql.DataSource

class UserEventPartListAdapter(private val context: Context, val listener: EventFragment,
                               val parentFragment: Fragment):
    RecyclerView.Adapter<UserEventPartListAdapter.EventViewHolder>(){


    private var ctx: Context?=null
    var eventList = mutableListOf<EventParticipants>()
    var eventPartList = mutableListOf<EventParticipants>()
    var fullList = mutableListOf<EventParticipants>()


    fun setData(arrData: List<EventParticipants>) {
        eventList = arrData as ArrayList<EventParticipants>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        ctx = parent.context
        return EventViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycleview_event,parent,false)
        )
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
//
//        }

        holder.btn.setText("Joined")
        holder.btn.setBackgroundColor(Color.WHITE)
        holder.btn.setTextColor(Color.BLACK) // Set text color to black

//        if (!currentEvent.image.isNullOrBlank()) {
//            Picasso.get().load(currentEvent.image).fit().into(eventImageView)
//        }else{
//            Log.e("EventListAdapter","event image")
//            Picasso.get().load(R.drawable.event_default)
//        }

        if (!currentEvent.image.isNullOrBlank()) {

            Glide.with(context)
                .load(currentEvent.image)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .fitCenter()
                .error(R.drawable.event_default)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable?>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e("Glide Error", "Error loading image: $e")
                        return false // Return false to allow Glide to handle the error as usual
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: com.bumptech.glide.load.DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                })
                .into(eventImageView)
        } else {
            Log.e("EventListAdapter", "event image")
            Glide.with(context)
                .load(currentEvent.image)
                .fitCenter()
                .error(R.drawable.event_default)
                .into(eventImageView)
        }


        val button =  holder.itemView.findViewById<Button>(R.id.component4)

        button.setOnClickListener {
            val action = currentEvent.id?.let { it1 ->
                EventFragmentDirections.actionEventFragmentToEventDetailsFragment(
                    it1,true
                )
            }
            if (action != null) {
                parentFragment.findNavController().navigate(action)
            }
        }


        holder.event_layout.setOnClickListener {
            val action = currentEvent.id?.let { it1 ->
                EventFragmentDirections.actionEventFragmentToEventDetailsFragment(
                    it1,true
                )
            }
            if (action != null) {
                parentFragment.findNavController().navigate(action)
            }
        }

//        holder.event_layout.setOnClickListener{
//            listener.onItemClicked(eventList[holder.adapterPosition])
//        }
//        holder.event_layout.setOnLongClickListener{
//            listener.onLongItemClicked(eventList[holder.adapterPosition],holder.event_layout)
//            true
//        }
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

//    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
//        val currentEvent = eventList[position]
//
//
//        holder.eventTitle.text = currentEvent.title?: ""
//
//        val (month, day) = extractMonthAndDay(currentEvent.date.toString())
//
//        holder.eventDate.text =  "${month}\n${day}"
//
//        holder.eventDetails.text = currentEvent.details?:""
//
//        Log.e("EventListAdapter","${currentEvent.image} event image")
//
//        val eventImageView =  holder.itemView.findViewById<ImageView>(R.id.item_image)
//
////        if (position < eventPartList.size) {
////            val currentEventPart = eventPartList[position]
////            if (currentEventPart.toString().equals(currentEvent.id.toString())) {
////                holder.btn.setText("Joined")
////            } else {
////                holder.btn.setText("Join")
////            }
////
////        }
//
//        if (!currentEvent.image.isNullOrBlank()) {
//            Picasso.get().load(currentEvent.image).fit().into(eventImageView)
//        }else{
//            Log.e("EventListAdapter","event image")
//            Picasso.get().load(R.drawable.event_default)
//        }
//
//        val button =  holder.itemView.findViewById<Button>(R.id.component4)
//
//        button.setOnClickListener {
//            val action = currentEvent.id?.let { it1 ->
//                EventFragmentDirections.actionEventFragmentToEventDetailsFragment(
//                    it1
//                )
//            }
//            if (action != null) {
//                parentFragment.findNavController().navigate(action)
//            }
//        }
//
//
//        holder.event_layout.setOnClickListener {
//            val action = currentEvent.id?.let { it1 ->
//                EventFragmentDirections.actionEventFragmentToEventDetailsFragment(
//                    it1
//                )
//            }
//            if (action != null) {
//                parentFragment.findNavController().navigate(action)
//            }
//        }
//
////        holder.event_layout.setOnClickListener{
////            listener.onItemClicked(eventList[holder.adapterPosition])
////        }
////        holder.event_layout.setOnLongClickListener{
////            listener.onLongItemClicked(eventList[holder.adapterPosition],holder.event_layout)
////            true
////        }
//    }


    fun updateList(newList: MutableList<EventParticipants>){
        fullList.clear()
        fullList.addAll(newList)
        eventList.clear()
        eventList.addAll(fullList)

        notifyDataSetChanged()
    }

//    fun updateEventPartList(newEventPartList:List<EventParticipants>){
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
        fun onItemClicked(event: EventParticipants)
        fun onLongItemClicked(event: EventParticipants, cardView: CardView)
    }


}