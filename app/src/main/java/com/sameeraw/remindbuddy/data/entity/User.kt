package com.sameeraw.remindbuddy.data.entity

data class User(
    val userName: String,
    val password: String,
    val pinCode: String,
    val email: String,
    val keepLoggedIn: Boolean,
    val isLoggedIn: Boolean,
) {
}