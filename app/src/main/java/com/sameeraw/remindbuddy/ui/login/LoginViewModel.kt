package com.sameeraw.remindbuddy.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sameeraw.remindbuddy.repository.UserRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    sealed class Event {
        object navigateToHome : Event()
        object loginError : Event()
    }

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()


    fun onLoginClicked(userName: String, password: String, keepLoggedIn: Boolean) {
        viewModelScope.launch {
            userRepository.getUserByUserName().collect { user ->
                if (userName == user.first && password == user.second) {

                    userRepository.logInUser(keepLoggedIn = keepLoggedIn)
                    eventChannel.send(Event.navigateToHome)
                } else {
                    eventChannel.send(Event.loginError)
                }
            }
        }

    }

    fun onLoginWithPINClicked(editValue: String, checkedState: Boolean) {

        val s = 1234

        viewModelScope.launch {
            userRepository.getUserPIN().collect { pin ->
                if (editValue == pin) {

                    userRepository.logInUser(checkedState)
                    eventChannel.send(Event.navigateToHome)
                } else {
                    eventChannel.send(Event.loginError)
                }
            }
        }

    }
}

class LoginViewModelFactory(private val userRepository: UserRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")

            return LoginViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}