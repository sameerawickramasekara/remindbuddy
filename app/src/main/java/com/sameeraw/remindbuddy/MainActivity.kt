package com.sameeraw.remindbuddy

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.sameeraw.remindbuddy.ui.RemindBuddyApplication
import com.sameeraw.remindbuddy.ui.theme.RemindbuddyTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @ExperimentalPermissionsApi
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

            val intExtra = intent.getLongExtra("reminderId", -1L)
            Log.d("INTENT",intExtra.toString())



        setContent {
            RemindbuddyTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background, modifier = Modifier.fillMaxSize()) {
                    ProvideWindowInsets(windowInsetsAnimationsEnabled = true){
                        RemindBuddyApplication(reminderId = intExtra)
                    }

                }
            }
        }
    }


}
