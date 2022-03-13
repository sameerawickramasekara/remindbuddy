package com.sameeraw.remindbuddy.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sameeraw.remindbuddy.repository.UserRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


class SplashViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    sealed class Event {
        object LoggedIn : SplashViewModel.Event()
        object LoggedOut : SplashViewModel.Event()
    }

    private val eventChannel = Channel<SplashViewModel.Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    fun checkLoginStatus() {

        viewModelScope.launch {
            userRepository.getLoggedInStatus().collect { loggedin ->

                userRepository.getKeepLoggedIn().collect { rem ->

                    if (loggedin && rem) {
                        eventChannel.send(SplashViewModel.Event.LoggedIn)
                    } else {
                        eventChannel.send(SplashViewModel.Event.LoggedOut)
                    }
                }

            }
        }
    }

}

class SplashViewModelFactory(private val userRepository: UserRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")

            return SplashViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}