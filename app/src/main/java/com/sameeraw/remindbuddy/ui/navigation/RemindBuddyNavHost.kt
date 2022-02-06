package com.sameeraw.remindbuddy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sameeraw.remindbuddy.ui.Register
import com.sameeraw.remindbuddy.ui.home.Home
import com.sameeraw.remindbuddy.ui.login.Login
import com.sameeraw.remindbuddy.ui.login.PINLogin
import com.sameeraw.remindbuddy.ui.splash.Splash

@ExperimentalComposeUiApi
@Composable
fun RemindBuddyNavHost(navController: NavHostController) {

    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        composable(route = Screen.Login.route) {
            Login(navController = navController)
        }

        composable(route = Screen.Home.route) {
            Home(navController = navController)
        }

        composable(route = Screen.PINLogin.route) {
            PINLogin(navController = navController)
        }
        composable(route = Screen.Splash.route) {
            Splash(navController = navController)
        }
        composable(route = Screen.Register.route) {
            Register(navController = navController)
        }


    }
}