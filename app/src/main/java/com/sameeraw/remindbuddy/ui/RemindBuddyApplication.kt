package com.sameeraw.remindbuddy.ui

import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable

@Composable
fun RemindBuddyApplication (){
 
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "RemindBuddy") })
        }
    ) {
        
    }
}