package com.sameeraw.remindbuddy.ui.home.reminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sameeraw.remindbuddy.data.entity.Reminder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class ReminderListViewModel : ViewModel() {

    private val _state = MutableStateFlow(ReminderListViewState())

    val state: StateFlow<ReminderListViewState>
        get() = _state

    init {
        val list = mutableListOf<Reminder>()
        for (i in 0..10) {

            list.add(
                Reminder(
                    content = "Go Shopping $i",
                    date = Date(),
                    category = "Shopping"
                )
            )
        }

        _state.value = ReminderListViewState(reminders = list)

    }
}

data class ReminderListViewState(
    val reminders: List<Reminder> = emptyList()
)