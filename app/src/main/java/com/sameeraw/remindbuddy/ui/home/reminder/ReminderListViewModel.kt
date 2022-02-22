package com.sameeraw.remindbuddy.ui.home.reminder

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sameeraw.remindbuddy.data.entity.Reminder
import com.sameeraw.remindbuddy.repository.ReminderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ReminderListViewModel @Inject constructor(
    private val repository: ReminderRepository,
)  : ViewModel() {

    val reminders = repository.getAllReminders()

    fun deleteReminder(reminder: Reminder){
        viewModelScope.launch {
            repository.delete(reminder)
        }
    }
}

