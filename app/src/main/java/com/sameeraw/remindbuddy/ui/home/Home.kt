package com.sameeraw.remindbuddy.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentSender
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.accompanist.insets.systemBarsPadding
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.sameeraw.remindbuddy.repository.UserRepository
import com.sameeraw.remindbuddy.ui.home.reminder.ReminderList
import com.sameeraw.remindbuddy.ui.navigation.Screen
import kotlinx.coroutines.flow.collect


val NEW_REMINDER = -1L



@SuppressLint("MissingPermission")
@ExperimentalPermissionsApi
@Composable
fun Home(
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            UserRepository(LocalContext.current)
        )
    ),
    navController: NavHostController
) {


    val context = LocalContext.current


    val bitmap = remember { mutableStateOf<Bitmap?>(null) }

    val profileMenuExpanded = rememberSaveable {
        mutableStateOf(false)

    }
    val showAddReminder = rememberSaveable {
        mutableStateOf(false)
    }

    val showLocationPicker = rememberSaveable {
        mutableStateOf(false)
    }

    val locationFiler = rememberSaveable {
        mutableStateOf<LatLng?>(null)
    }

    val showAll = rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = true) {

        viewModel.eventsFlow.collect { event ->
            when (event) {
                is HomeViewModel.Event.LogoutSuccess -> {
                    Toast.makeText(context, "Logged out", Toast.LENGTH_LONG).show()
                    navController.navigate(Screen.Login.route)
                }
                is HomeViewModel.Event.NavigateToAddEditReminder -> {
                    event.reminderId.let {
                        navController.navigate(Screen.AddEditReminder.route+"?reminderId=${event.reminderId}")
                    }
                }
            }

        }
    }

    Surface() {
        Scaffold(
            modifier = Modifier.padding(bottom = 24.dp),
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                          viewModel.onAddNewReminder(NEW_REMINDER)
                    },
                    contentColor = MaterialTheme.colors.primaryVariant,
                    modifier = Modifier.padding(all = 20.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "open payments")
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .systemBarsPadding()
                    .fillMaxWidth()
            ) {

                TopAppBar(
                    title = {
                        Text(
                            text = "Remindbuddy",
                            color = MaterialTheme.colors.primary,
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .heightIn(max = 24.dp)
                        )
                    }, backgroundColor = MaterialTheme.colors.background,
                    actions = {
                        Text(text = "Completed",style = MaterialTheme.typography.caption)
                        Switch(checked = showAll.value, onCheckedChange = {
                            showAll.value = !showAll.value
                        })
                        Text(text = "All", style = MaterialTheme.typography.caption)

                        IconButton(onClick = {
                            showLocationPicker.value = !showLocationPicker.value

                        }) {
                            Icon(
                                imageVector = if(locationFiler.value!= null) Icons.Default.LocationOn else Icons.Default.LocationOff,
                                contentDescription = ""
                            )

                        }
                        Box(
                            Modifier
                                .wrapContentSize(Alignment.TopEnd)
                        ) {
                            IconButton(onClick = {
                                profileMenuExpanded.value = true
                            }) {
                                Icon(
                                    Icons.Default.AccountCircle,
                                    contentDescription = ""
                                )
                            }

                            DropdownMenu(
                                expanded = profileMenuExpanded.value,
                                onDismissRequest = { profileMenuExpanded.value = false },
                            ) {
                                DropdownMenuItem(onClick = {
                                    profileMenuExpanded.value = false
                                }) {
                                    Text("Change Profile Picture")
                                }

                                Divider()

                                DropdownMenuItem(onClick = {
                                    profileMenuExpanded.value = false

                                    viewModel.logoutUser()
                                }) {
                                    Text("Logout")
                                }

                            }
                        }

                    })
                Column {
                    Text(text = if(locationFiler.value!= null) "Virtual Location set" else "Virtual Location not set",
                    style = MaterialTheme.typography.subtitle2)
                }
                ReminderList(navController = navController, showAll =showAll.value, location = locationFiler.value )
            }

        }

    }

    if (showLocationPicker.value) {
        LocationPicker(toggleLocationPicker = {
            showLocationPicker.value = !showLocationPicker.value
        }, confirmLocation = {
            locationFiler.value = it
        }, location = locationFiler.value,
        unsetLocation = {
            locationFiler.value = null
        })
    }

}


@Composable
private fun LocationPicker(
    toggleLocationPicker: () -> Unit,
    confirmLocation: (value: LatLng) -> Unit,
    unsetLocation:()->Unit,
    location: LatLng?
) {

    val defaultLocation = location ?: LatLng(1.0, 1.0)
    Dialog(onDismissRequest = {
        toggleLocationPicker()
    }) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colors.onSurface,
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Select Location", color = Color.Black)
                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    googleMapOptionsFactory = {
                        GoogleMapOptions().camera(
                            CameraPosition.fromLatLngZoom(
                                defaultLocation,
                                10f
                            )
                        )
                    },
                    onMapClick = {
                        confirmLocation(it)
                    }

                ) {
                    location?.let { it1 -> Marker(position = it1) }
                }
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    Button(
                        modifier = Modifier.width(150.dp),
                        enabled = location != null,
                        onClick = { /*TODO*/ }) {
                        Text(text = "Set")
                    }

                    Button(
                        modifier = Modifier.width(150.dp),
                        onClick = {

                            unsetLocation()
                        }) {
                        Text(text = "Clear")
                    }
                }
            }
        }
    }
}


fun checkLocationSetting(
    context: Context,
    onDisabled: (IntentSenderRequest) -> Unit,
    onEnabled: () -> Unit
) {

    val locationRequest = LocationRequest.create().apply {
        interval = 1000
        fastestInterval = 1000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    val client: SettingsClient = LocationServices.getSettingsClient(context)
    val builder: LocationSettingsRequest.Builder = LocationSettingsRequest
        .Builder()
        .addLocationRequest(locationRequest)

    val gpsSettingTask: Task<LocationSettingsResponse> =
        client.checkLocationSettings(builder.build())

    gpsSettingTask.addOnSuccessListener { onEnabled() }
    gpsSettingTask.addOnFailureListener { exception ->
        if (exception is ResolvableApiException) {
            try {
                val intentSenderRequest = IntentSenderRequest
                    .Builder(exception.resolution)
                    .build()
                onDisabled(intentSenderRequest)
            } catch (sendEx: IntentSender.SendIntentException) {
                // ignore here
            }
        }
    }

}

