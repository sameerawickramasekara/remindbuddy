package com.sameeraw.remindbuddy.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sameeraw.remindbuddy.data.entity.User
import com.sameeraw.remindbuddy.repository.UserRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {

    sealed class Event {
        object RegisterSuccess : Event()
        object CreateError : Event()
        object EmailFormatError : Event()
        object MissingValuesError : Event()
        object PINFormatError : Event()
        object PasswordMisMatchError : Event()
    }

    private val eventChannel = Channel<RegisterViewModel.Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    fun onRegisterClicked(
        userName: String,
        password: String,
        retypePassword: String,
        email: String,
        pin: String
    ) {

        viewModelScope.launch {

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                eventChannel.send(Event.EmailFormatError)
            } else if (pin.length != 4) {
                eventChannel.send(Event.PINFormatError)
            } else if (
                userName == "" || password == "" || email == "" || pin == ""
            ) {
                eventChannel.send(Event.MissingValuesError)
            } else if (password != retypePassword) {
                eventChannel.send(Event.PasswordMisMatchError)
            } else {

                userRepository.insertUser(
                    User(
                        userName = userName,
                        password = password,
                        email = email,
                        pinCode = pin,
                        keepLoggedIn = false,
                        isLoggedIn = false
                    )
                )

                eventChannel.send(Event.RegisterSuccess)
            }


        }


    }

}

class RegisterViewModelFactory(private val userRepository: UserRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")

            return RegisterViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}