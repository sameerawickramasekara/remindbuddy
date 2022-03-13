package com.sameeraw.remindbuddy.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.sameeraw.remindbuddy.ui.navigation.RemindBuddyNavHost

@ExperimentalPermissionsApi
@ExperimentalComposeUiApi
@Composable
fun RemindBuddyApplication(
    appState: RemindBuddyApplicationState = rememberAppState(),
    reminderId:Long
) {

    RemindBuddyNavHost(navController = appState.navController,reminderId)
}