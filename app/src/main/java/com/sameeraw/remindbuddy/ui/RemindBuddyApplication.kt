package com.sameeraw.remindbuddy.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import com.sameeraw.remindbuddy.ui.navigation.RemindBuddyNavHost

@ExperimentalComposeUiApi
@Composable
fun RemindBuddyApplication(
    appState: RemindBuddyApplicationState = rememberAppState()
) {

    RemindBuddyNavHost(navController = appState.navController)
}