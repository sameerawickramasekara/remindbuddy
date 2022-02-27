package com.sameeraw.remindbuddy.ui.splash

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.sameeraw.remindbuddy.repository.UserRepository
import com.sameeraw.remindbuddy.ui.navigation.Screen
import kotlinx.coroutines.flow.collect

@Composable
fun Splash(
    viewModel: SplashViewModel = viewModel(
        factory = SplashViewModelFactory(UserRepository(LocalContext.current))
    ),
    navController: NavHostController,
    reminderId:Long
) {

    
    LaunchedEffect(key1 = true){
        viewModel.eventsFlow.collect { event ->
            when(event){
                is SplashViewModel.Event.LoggedIn-> {

                    Log.println(Log.INFO,"REM","LOGGEDIN")
                    if(reminderId != -1L){
                        navController.navigate(Screen.AddEditReminder.route+"?reminderId=${reminderId}")
                    }else {
                        navController.navigate(Screen.Home.route)
                    }

                }
                is SplashViewModel.Event.LoggedOut-> {
                    navController.navigate(Screen.Login.route)
                    Log.println(Log.INFO,"REM","LOGGEDOUT")
                }
            }
        }
    }

    LaunchedEffect(Unit){
        viewModel.checkLoginStatus()
    }

    Surface() {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Remindbuddy",
                fontSize = 50.sp,
                color = MaterialTheme.colors.primary,
                fontFamily = FontFamily.Cursive
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )
        }
    }
}