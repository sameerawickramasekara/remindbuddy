package com.sameeraw.remindbuddy.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController


class RemindBuddyApplicationState(val navController: NavHostController) {

    fun navigateBack() {
        navController.popBackStack()
    }


}

@Composable
fun rememberAppState(navController: NavHostController = rememberNavController()) =
    remember(navController) {
        RemindBuddyApplicationState(navController)
    }