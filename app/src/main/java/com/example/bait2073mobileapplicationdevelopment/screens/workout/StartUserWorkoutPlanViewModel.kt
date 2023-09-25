package com.example.bait2073mobileapplicationdevelopment.screens.workout

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bait2073mobileapplicationdevelopment.entities.PersonalizedWorkout
import com.example.bait2073mobileapplicationdevelopment.entities.UserPlanList

class StartUserWorkoutPlanViewModel : ViewModel() {
    // The current word
    // The current word

    private val workoutList = ArrayList<UserPlanList>()


    private val _activityName = MutableLiveData<String>()

    private val _totalUserPlanListCount = MutableLiveData<Int>()
    val totalUserPlanListCount: LiveData<Int>
        get() = _totalUserPlanListCount

    val activityName: LiveData<String>
        get() = _activityName

    // The current score
    private val _activityCount = MutableLiveData<Int>()
    val activityCount: LiveData<Int>
        get() = _activityCount


    // Countdown time
    private val _currentTime = MutableLiveData<Long>()
    val currentTime: LiveData<Long>
        get() = _currentTime
    // The list of words - the front of the list is the next word to guess

    private val _gifImageUrl = MutableLiveData<String>()
    val gifImageUrl: LiveData<String>
        get() = _gifImageUrl


    private val _eventActivityFinish = MutableLiveData<Boolean>()
    val eventActivityFinish: LiveData<Boolean>
        get() = _eventActivityFinish

    private val _progressBar = MutableLiveData<Int>()
    val progressBar: LiveData<Int>
        get() = _progressBar

    private lateinit var activityList: MutableList<String>
    private lateinit var gifImageList: MutableList<String>

    private var timer: CountDownTimer? = null
    private val totaltime: Long = 30000 // 11 second in milliseconds
    private var timeProgress = 0
    private val timeSelected: Long = 30000
    private var pauseOffSet: Long = 0

//     The String version of the current time
//    val currentTimeString = Transformations.map(currentTime) { time ->
//        DateUtils.formatElapsedTime(time)
//    }
    /**
     * Resets the list of words and randomizes the order
     */
    private fun resetList() {

        Log.e("resetlist", "$workoutList")
        activityList.clear()
        for (workout in workoutList) {
            activityList.add(workout.name)
        }
        Log.e("activitylist", "$activityList")
        gifImageList.clear()

        for (workout in workoutList) {
            gifImageList.add(workout.gifimage!!)
        }


    }


    init {
        _activityName.value = ""
        _activityCount.value = 1
        activityList = mutableListOf()
        gifImageList = mutableListOf()
//        binding.pbTimer.progress = timeProgress
//        val progressBar = view.findViewById<ProgressBar>(R.id.pbTimer)
        Log.i("GameViewModel", "GameViewModel created!")

//         Creates a timer which triggers the end of the game when it finishes
        timer = object : CountDownTimer(timeSelected, 1000L) {

            override fun onTick(millisUntilFinished: Long) {
                val progress = ((millisUntilFinished.toFloat() / totaltime) * 100).toInt()
                // Update the progress bar (assuming it's from 0 to 100)
                _progressBar.value = progress
                _currentTime.value = (millisUntilFinished / 1000)
            }

            override fun onFinish() {
                _currentTime.value = DONE
//               _processBar.value = timeProgress

                Log.e("finish","finish")
                nextActivity()
                if (_activityCount.value != 5)
                    _activityCount.value = (activityCount.value)?.plus(1)

            }
        }

        timer?.start()
    }

    fun updateList(newList: List<UserPlanList>) {
        workoutList.clear()
        workoutList.addAll(newList)
        Log.e("update", "$workoutList")
        _totalUserPlanListCount.value = workoutList.size
        resetList()
        nextActivity()
    }

    /**
     * Moves to the next word in the list
     */
    private fun nextActivity() {

        // Shuffle the word list, if the list is empty
        if (activityList.isEmpty()) {
            onActivityFinish()
            return
        } else {
            timeProgress = 0
//            timeSelected=1100
            pauseOffSet = 0
            // Remove a word from the list
            _activityName.value = activityList.removeAt(0)
            _gifImageUrl.value = gifImageList.removeAt(0)
            timer?.start()
        }
    }


    companion object {

        // Time when the game is over
        private const val DONE = 0L

        // Countdown time interval
        private const val ONE_SECOND = 1000L

        // Total time for the game
        private const val COUNTDOWN_TIME = 60000L

    }

    /** Methods for buttons presses **/
    fun onSkip() {
        nextActivity()
        // Increment activityCount based on the available workout size
        _activityCount.value = (activityCount.value)?.plus(1)

        // Check if all activities are completed
        if (activityCount.value == workoutList.size) {
            onActivityFinish()
        }
    }


    // Event which triggers the end of the game


    override fun onCleared() {
        super.onCleared()
        // Cancel the timer
//        timer.cancel()
    }


    fun onActivityFinish() {
        _eventActivityFinish.value = true
    }

    fun onActivityFinishComplete() {
        _eventActivityFinish.value = false
        return
    }

}