import androidx.lifecycle.LiveData
import com.example.bait2073mobileapplicationdevelopment.dao.EventParticipantsDao
import com.example.bait2073mobileapplicationdevelopment.entities.Event
import com.example.bait2073mobileapplicationdevelopment.entities.EventParticipants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EventParticipantsRepository (private val eventPartDao: EventParticipantsDao) {

    //    var allEvents : LiveData<List<Event>> = eventPartDao.getAllEventsForUser()
     fun retrieve(): LiveData<List<EventParticipants>>{
        return eventPartDao.getAllEventsParticipants()
    }

    suspend fun retrieveId(eventId: Int) :Event{
        return eventPartDao.getEventParticipantsById(eventId)
    }

    suspend fun retrieveCount(eventId: Int): Int {
        return withContext(Dispatchers.IO) {
            eventPartDao.getEventParticipantsCountByEventId(eventId)
        }
    }

    suspend fun insert(event: EventParticipants){
        return eventPartDao.insertEventsParticipants(event)
    }

    suspend fun deleteAll(){
        eventPartDao.clearAllEventsParticipants()
    }


}
