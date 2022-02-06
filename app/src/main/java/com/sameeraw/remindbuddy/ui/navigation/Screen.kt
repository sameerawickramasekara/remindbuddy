package com.sameeraw.remindbuddy.ui.navigation

sealed class Screen(val route:String) {
    object Login:Screen("login")
    object PINLogin:Screen("pin-login")
    object Home:Screen("home")
    object Splash:Screen("splash")
    object Register:Screen("register")
}