package com.sameeraw.remindbuddy.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sameeraw.remindbuddy.repository.UserRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    sealed class Event {
        object LogoutSuccess : Event()
        object LogoutError : Event()
    }

    private val eventChannel = Channel<HomeViewModel.Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    fun logoutUser() {
        viewModelScope.launch {
            userRepository.logoutUser()
            eventChannel.send(Event.LogoutSuccess)
        }
    }
}

class HomeViewModelFactory(private val userRepository: UserRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")

            return HomeViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}