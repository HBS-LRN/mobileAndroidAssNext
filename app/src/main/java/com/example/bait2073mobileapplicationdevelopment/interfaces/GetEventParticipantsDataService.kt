package com.example.bait2073mobileapplicationdevelopment.interfaces

import com.example.bait2073mobileapplicationdevelopment.entities.Event
import com.example.bait2073mobileapplicationdevelopment.entities.EventParticipants
import com.example.bait2073mobileapplicationdevelopment.entities.EventParticipantsTable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface GetEventParticipantsDataService {

    @GET("eventParticipants")
    fun getEventParticipantsList(): Call<List<EventParticipants>>

//    @GET("events/{event_id}")
//    @Headers("Accept:application/json","Content-Type:application/json")
//    fun getEventParticipants(@Path("event_id") event_id: Int): Call<Event>

    @GET("eventsParticipants/getEventParticipantsCount/{event_id}")
    @Headers("Accept: application/json", "Content-Type: application/json")
    fun getEventParticipantsCount(@Path("event_id") event_id: Int): Call<Int>


    @GET("eventsParticipants/displayUserHaventPart/{user_id}")
    @Headers("Accept:application/json","Content-Type:application/json")
    fun getEventUserHaventParticipantsList(@Path("user_id") user_id: Int?): Call<List<Event>>

    @GET("eventsParticipants/update-status/{user_id}")
    @Headers("Accept:application/json","Content-Type:application/json")
    fun getEventUserParticipantsList(@Path("user_id") user_id: Int?): Call<List<EventParticipants>>


    @POST("eventsParticipants")
    @Headers("Accept:application/json", "Content-Type:application/json",
        "Authorization: Bearer 73668350bdf06c66f3388408c1a712b378c3e25da599753b21b664a6261e246c")
    fun addEventParticipants(@Body params: EventParticipantsTable): Call<EventParticipantsTable>

    @PATCH("eventsParticipants/{event_id}")
    @Headers("Accept:application/json", "Content-Type:application/json",
        "Authorization: Bearer 73668350bdf06c66f3388408c1a712b378c3e25da599753b21b664a6261e246c")
    fun updateEventParticipants(@Path("event_id") event_id: Int, @Body params: EventParticipants): Call<EventParticipants>


    @DELETE("eventsParticipants/{event_id}/{user_id}")
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
        "Authorization: Bearer 73668350bdf06c66f3388408c1a712b378c3e25da599753b21b664a6261e246c"
    )
    fun deleteEventParticipants(
        @Path("event_id") eventId: Int,
        @Path("user_id") userId: Int
    ): Call<EventParticipants>



}