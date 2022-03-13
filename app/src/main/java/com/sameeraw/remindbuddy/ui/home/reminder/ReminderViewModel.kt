package com.sameeraw.remindbuddy.ui.home.reminder

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.content.ContextCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.google.android.gms.maps.model.LatLng
import com.sameeraw.remindbuddy.data.entity.Reminder
import com.sameeraw.remindbuddy.repository.ReminderRepository
import com.sameeraw.remindbuddy.util.NotificationWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val repository: ReminderRepository,
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val application: Context,
) : ViewModel() {


    sealed class ReminderEvent {
        object AddSuccess : ReminderEvent()
        object MarkedDone : ReminderEvent()
        object AddError : ReminderEvent()
        object TitleNotGivenError : ReminderEvent()
        object Permission : ReminderEvent()
    }


    private val eventChannel = Channel<ReminderEvent>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    val reminderIcons = listOf<ImageVector>(
        Icons.Default.SupervisedUserCircle,
        Icons.Default.House,
        Icons.Default.CarRepair,
        Icons.Default.TravelExplore,
        Icons.Default.Medication,
        Icons.Default.Train,
        Icons.Default.Language,
        Icons.Default.Book,
        Icons.Default.Phone
    )

    var reminder by mutableStateOf<Reminder?>(null)
    var title by mutableStateOf<String>("")
        private set
    var description by mutableStateOf<String>("")
        private set
    var calendar by mutableStateOf<Calendar?>(Calendar.getInstance())
        private set
    var location by mutableStateOf<LatLng?>(null)
        private set
    var image by mutableStateOf<Uri?>(null)
        private set
    var icon by mutableStateOf<String>(reminderIcons.first().name)
        private set
    var enableNotification by mutableStateOf<Boolean>(true)
        private set

    var onTime by mutableStateOf<Boolean>(true)
        private set
    var fiveMins by mutableStateOf<Boolean>(false)
        private set
    var tenMins by mutableStateOf<Boolean>(false)
        private set
    var fifteenMins by mutableStateOf<Boolean>(false)
        private set
    var thirtyMins by mutableStateOf<Boolean>(false)
        private set

    fun setNewCalendar() {
        calendar = Calendar.getInstance()
        calendar!!.time = Date()
    }

    fun onChangeTitle(newTitle: String) {
        title = newTitle
    }

    fun onChangeDescription(newDesc: String) {
        description = newDesc
    }

    fun onChangeDateTime(newCalendar: Calendar?) {
        calendar = newCalendar
    }

    fun onChangeLocation(newLocation: LatLng?) {
        location = newLocation
    }

    fun onChangeImage(newImage: Uri?) {
        image = newImage
    }

    fun onChangeIcon(newIcon: String) {
        icon = newIcon
    }

    fun onNotificationChange(
        notificationStatus: Boolean,
        onTimeStatus: Boolean,
        fiveMinsStatus: Boolean,
        tenMinsStatus: Boolean,
        fifteenMinsStatus: Boolean,
        thirtyMInsStatus: Boolean
    ) {
        enableNotification = notificationStatus
        onTime = onTimeStatus
        fiveMins = fiveMinsStatus
        tenMins = tenMinsStatus
        fifteenMins = fifteenMinsStatus
        thirtyMins = thirtyMInsStatus
    }

    fun markAsDone() {
        viewModelScope.launch {
            reminder?.let {
                val rem2 = it.copy(reminderSeen = true);
                repository.insert(rem2)
                eventChannel.send(ReminderEvent.MarkedDone)
            }
        }
    }

    fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    application,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

    }

    fun onSaveReminder() {
        viewModelScope.launch {

            val id: Long = repository.insert(
                Reminder(
                    id = reminder?.id,
                    title = title,
                    message = description,
                    locationX = when {
                        location != null -> location?.latitude.toString()
                        else -> null
                    },
                    locationY = when {
                        location != null -> location?.longitude.toString()
                        else -> null
                    },
                    reminderTime = when {
                        calendar != null -> calendar!!.time.time
                        else -> null
                    },
                    creationTime = Date().time,
                    imageURL = when {
                        image != null -> image.toString()
                        else -> null
                    },
                    icon = icon,
                    creatorId = 1,
                    reminderSeen = false,
                    notify = enableNotification,
                    onTime = onTime,
                    fiveMin = fiveMins,
                    tenMin = tenMins,
                    fifteenMin = fifteenMins,
                    thirtyMin = thirtyMins
                )

            )
            handleNotifications(id, title, location)
            eventChannel.send(ReminderEvent.AddSuccess)
        }
    }


    fun handleNotifications(reminderId: Long, title: String, location: LatLng?) {
        val now: Date = Date()

        if (enableNotification && calendar != null) {

            if (onTime && calendar!!.time.after(now)) {
                val delay = (calendar!!.time.time - now.time) / 1000
                createWorkRequest(
                    title,
                    delay,
                    "ONTIME${reminderId}",
                    reminderId = reminderId,
                    state = "ONTIME",
                    location = location
                )
            } else {
                cancelWorkRequest("ONTIME${reminderId}")
            }

            if (fiveMins && calendar!!.time.after(Date(now.time + 300000))) {
                val delay = (calendar!!.time.time - Date(now.time + 300000).time) / 1000
                createWorkRequest(
                    title,
                    delay,
                    "FIVEMIN${reminderId}",
                    reminderId = reminderId,
                    state = "FIVEMIN",
                    location = null
                )

            } else {
                cancelWorkRequest("FIVEMIN${reminderId}")
            }

            if (tenMins && calendar!!.time.after(Date(now.time + 600000))) {
                val delay = (calendar!!.time.time - Date(now.time + 600000).time) / 1000
                createWorkRequest(
                    title,
                    delay,
                    "TENMIN${reminderId}",
                    reminderId = reminderId,
                    state = "TENMIN",
                    location = null
                )

            } else {
                cancelWorkRequest("TENMIN${reminderId}")
            }

            if (fifteenMins && calendar!!.time.after(Date(now.time + 900000))) {
                val delay = (calendar!!.time.time - Date(now.time + 900000).time) / 1000
                createWorkRequest(
                    title,
                    delay,
                    "FIFTEENMIN${reminderId}",
                    reminderId = reminderId,
                    state = "FIFTEENMIN",
                    location = null
                )

            } else {
                cancelWorkRequest("FIFTEENMIN${reminderId}")
            }

            if (fifteenMins && calendar!!.time.after(Date(now.time + 1800000))) {
                val delay = (calendar!!.time.time - Date(now.time + 1800000).time) / 1000
                createWorkRequest(
                    title,
                    delay,
                    "THIRTYMIN${reminderId}",
                    reminderId = reminderId,
                    state = "THIRTYMIN",
                    location = null
                )

            } else {
                cancelWorkRequest("THIRTYMIN${reminderId}")
            }
        } else {
            cancelWorkRequest("ONTIME${reminderId}")
            cancelWorkRequest("FIVEMIN${reminderId}")
            cancelWorkRequest("TENMIN${reminderId}")
            cancelWorkRequest("FIFTEENMIN${reminderId}")
            cancelWorkRequest("THIRTYMIN${reminderId}")
        }
    }

    init {
        val reminderId = savedStateHandle.get<Long>("reminderId")

        reminderId?.let {
            viewModelScope.launch {
                if (reminderId != -1L) {
                    repository.getReminderById(reminderId = reminderId)?.let {
                        this@ReminderViewModel.reminder = it
                        title = it.title
                        description = it.message
                        if (it.reminderTime != null) {
                            if (calendar == null) {
                                calendar = Calendar.getInstance()
                            }
                            calendar!!.time = Date(it.reminderTime)
                        } else {
                            calendar = null
                        }
                        icon = it.icon
                        if (it.locationX != null && it.locationY != null && it.locationX != "null" && it.locationY != "null") {
                            location = LatLng(it.locationX.toDouble(), it.locationY.toDouble())
                        }
                        if (it.imageURL != null) {
                            image = Uri.parse(it.imageURL)
                        }
                        enableNotification = it.notify
                        onTime = it.onTime
                        fiveMins = it.fiveMin
                        tenMins = it.tenMin
                        fifteenMins = it.fifteenMin
                        thirtyMins = it.thirtyMin
                    }
                }
            }
        }
    }

    private fun createWorkRequest(
        message: String, timeDelayInSeconds: Long, uniqueID: String,
        reminderId: Long,
        location: LatLng?,
        state: String
    ) {


        var stateString = when (state) {
            "FIVEMIN" -> "[5 Minutes Left]"
            "TENMIN" -> "[10 Minutes Left]"
            "FIFTEENMIN" -> "[15 Minutes Left]"
            "THIRTYMIN" -> "[30 Minutes Left]"
            "ONTIME" -> "[Now]"
            else -> ""
        }

        val myWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(timeDelayInSeconds, TimeUnit.SECONDS)
            .setInputData(
                workDataOf(
                    "title" to "${stateString} Reminder",
                    "message" to message,
                    "reminderId" to reminderId,
                    "lat" to if (location != null) location.latitude.toString() else null,
                    "lon" to if (location != null) location.longitude.toString() else null
                )
            )
            .build()

        WorkManager.getInstance(application)
            .enqueueUniqueWork(uniqueID, ExistingWorkPolicy.REPLACE, myWorkRequest)

    }

    private fun cancelWorkRequest(uniqueName: String) {
        WorkManager.getInstance(application).cancelUniqueWork(uniqueName);
    }
}