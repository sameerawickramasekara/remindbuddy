package com.sameeraw.remindbuddy.ui.home.reminder

import android.net.Uri
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.sameeraw.remindbuddy.data.entity.Reminder
import com.sameeraw.remindbuddy.repository.ReminderRepository
import com.sameeraw.remindbuddy.ui.home.HomeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val repository: ReminderRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    sealed class ReminderEvent {
        object AddSuccess:ReminderEvent()
        object AddError:ReminderEvent()
        object TitleNotGivenError:ReminderEvent()
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
    var calendar by mutableStateOf<Calendar>(Calendar.getInstance())
        private set
    var location by mutableStateOf<LatLng?>(null)
        private set
    var image by mutableStateOf<Uri?>(null)
        private set
    var icon by mutableStateOf<String>(reminderIcons.first().name)
        private set

    fun onChangeTitle(newTitle: String) {
        title = newTitle
    }
    fun onChangeDescription(newDesc: String) {
        description = newDesc
    }
    fun onChangeDateTime(newCalendar: Calendar) {
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

    fun onSaveReminder() {
        viewModelScope.launch {
            repository.insert(
                Reminder(
                    id = reminder?.id,
                    title = title,
                    message = description,
                    locationX = when {
                        location!=null -> location?.latitude.toString()
                        else-> null
                    }
                    ,
                    locationY = when {
                        location!=null -> location?.longitude.toString()
                        else-> null
                    },
                    reminderTime = calendar.time.time,
                    creationTime = Date().time,
                    imageURL = when {
                        image!=null -> image.toString()
                        else-> null
                    },
                    icon = icon,
                    creatorId = 1,
                    recurring = false,
                    reminderSeen = false
                )
            )
            eventChannel.send(ReminderEvent.AddSuccess)
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
                        calendar.time = Date(it.reminderTime)
                        icon = it.icon
                        if (it.locationX != null && it.locationY != null && it.locationX != "null" && it.locationY != "null") {
                            location = LatLng(it.locationX.toDouble(), it.locationY.toDouble())
                        }
                        if(it.imageURL != null ){
                            image = Uri.parse(it.imageURL)
                        }
                    }
                }
            }
        }
    }
}