package com.sameeraw.remindbuddy.ui.home.reminder


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.insets.systemBarsPadding
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.sameeraw.remindbuddy.GeofenceBroadcastReceiver
import com.sameeraw.remindbuddy.ui.navigation.Screen
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.flow.collect
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@ExperimentalPermissionsApi
@Composable


fun AddEditReminder(
    navController: NavHostController,
    viewModel: ReminderViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val geofenceList = mutableListOf<Geofence?>()

    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        )
    )

    LaunchedEffect(key1 = true) {
        viewModel.eventsFlow.collect { event ->
            when (event) {
                is ReminderViewModel.ReminderEvent.AddSuccess -> {
                    Toast.makeText(context, "Reminder Added", Toast.LENGTH_LONG).show()
                    navController.navigate(Screen.Home.route)
                }
                is ReminderViewModel.ReminderEvent.MarkedDone -> {
                    Toast.makeText(context, "Reminder Marked as Done", Toast.LENGTH_LONG).show()
                    navController.navigate(Screen.Home.route)
                }
                is ReminderViewModel.ReminderEvent.Permission -> {
                    locationPermissionsState.launchMultiplePermissionRequest()
                }
            }
        }

    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) {
        if (it.resultCode != Activity.RESULT_OK) {
            return@rememberLauncherForActivityResult
        }

        val result = it.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

        if (result !== null) {
            val content = result[0] ?: ""
            viewModel.onChangeTitle(content)
        }
    }

    val descLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) {
        if (it.resultCode != Activity.RESULT_OK) {
            return@rememberLauncherForActivityResult
        }

        val result = it.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

        if (result !== null) {
            val content = result[0] ?: ""
            viewModel.onChangeDescription(content)
        }
    }


    val showLocationPicker = rememberSaveable {
        mutableStateOf(false)
    }

    val showNotificationSettings = rememberSaveable {
        mutableStateOf(false)
    }

    val markAsDoneReminder = rememberSaveable {
        mutableStateOf(
            false
        )
    }

    val calendar = remember {
        Calendar.getInstance()
    }

    val year =
        remember { if (viewModel.calendar != null) viewModel.calendar!![Calendar.YEAR] else 0 }
    val month =
        remember { if (viewModel.calendar != null) viewModel.calendar!![Calendar.MONTH] else 0 }
    val day =
        remember { if (viewModel.calendar != null) viewModel.calendar!![Calendar.DAY_OF_MONTH] else 0 }
    val hour = remember { if (viewModel.calendar != null) calendar[Calendar.HOUR_OF_DAY] else 0 }
    val minute = remember { if (viewModel.calendar != null) calendar[Calendar.MINUTE] else 0 }

    val datePickerDialog = DatePickerDialog(
        LocalContext.current,
        { view, year, month, dayOfMonth ->

            val cal2 = viewModel.calendar!!.clone() as Calendar
            cal2[Calendar.DAY_OF_MONTH] = dayOfMonth
            cal2[Calendar.MONTH] = month
            cal2[Calendar.YEAR] = year
            cal2[Calendar.SECOND] = 0
            viewModel.onChangeDateTime(cal2)
        },
        year,
        month,
        day
    )

    val timePickerDialog = TimePickerDialog(
        LocalContext.current,
        { _, hour: Int, minute: Int ->
            val cal2 = viewModel.calendar!!.clone() as Calendar
            cal2[Calendar.HOUR_OF_DAY] = hour
            cal2[Calendar.MINUTE] = minute
            cal2[Calendar.SECOND] = 0
            viewModel.onChangeDateTime(cal2)
        }, hour, minute, true
    )

    val selectImageLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
            viewModel.onChangeImage(uri)
        }

    val expandImageMenu = remember { mutableStateOf(false) }



    Surface() {
        Scaffold(
            modifier = Modifier.padding(bottom = 24.dp),
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        if (viewModel.calendar == null && viewModel.location != null) {
                            if (
                                ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED
                                && ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED
                                && ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.WAKE_LOCK
                                ) == PackageManager.PERMISSION_GRANTED
                                && ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.INTERNET
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {

                                geofenceList.add(viewModel!!.location?.let {
                                    viewModel!!.location?.let { it1 ->
                                        Geofence.Builder()
                                            .setRequestId("sdf")

                                            .setCircularRegion(
                                                it.latitude,
                                                it1.longitude,
                                                200F
                                            )
                                            .setExpirationDuration(900000)
                                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                                            .build()
                                    }
                                })

                                val req = GeofencingRequest.Builder().apply {
                                    setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                                    addGeofences(geofenceList)
                                }.build()

                                val geofencePendingIntent: PendingIntent by lazy {
                                    val intent =
                                        Intent(context, GeofenceBroadcastReceiver::class.java)
                                    intent.putExtra("reminderName", viewModel.title)
                                    intent.putExtra("reminderDesc", viewModel.description)
                                    PendingIntent.getBroadcast(
                                        context,
                                        0,
                                        intent,
                                        PendingIntent.FLAG_UPDATE_CURRENT
                                    )
                                }




                                LocationServices.getGeofencingClient(context).addGeofences(
                                    req, geofencePendingIntent
                                ).run {
                                    addOnSuccessListener {
                                        Log.d("TAG", "GEOFENCE ADDDED")
                                    }
                                    addOnFailureListener {
                                        Log.d("TAG", "GEOFENCE FAILED", it)
                                    }
                                }
                            } else {
                                locationPermissionsState.launchMultiplePermissionRequest()
                            }
                        }
                        viewModel.onSaveReminder()
                    },
                    contentColor = MaterialTheme.colors.primaryVariant,
                    modifier = Modifier.padding(all = 20.dp)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Save reminder")
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .systemBarsPadding()
                    .fillMaxWidth()
            ) {
                //Top App bar
                TopAppBar(
                    title = {
                        Text(
                            text = "Add New Reminder",
                            color = MaterialTheme.colors.primary,
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .heightIn(max = 24.dp)
                        )
                    }, backgroundColor = MaterialTheme.colors.background,
                    actions = {
                        Button(
                            onClick = {
                                markAsDoneReminder.value = true
                            },
                            enabled = when {
                                viewModel.reminder != null && viewModel.reminder!!.id != null -> true
                                else -> false
                            }
                        ) {
                            Text(text = "Mark As Done")
                        }
                    })
                Text(text = "Please fill below details...")

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),

                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = viewModel.title,
                            onValueChange = {
                                viewModel.onChangeTitle(it)
                            },
                            label = {
                                Text(
                                    text = "Title"
                                )
                            },
//                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Speack title",
                            modifier = Modifier
                                .size(30.dp)
                                .clickable {


                                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                                    intent.putExtra(
                                        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                                    )
                                    intent.putExtra(
                                        RecognizerIntent.EXTRA_LANGUAGE,
                                        Locale.getDefault()
                                    )
                                    intent.putExtra(
                                        RecognizerIntent.EXTRA_PROMPT,
                                        "Please speak now"
                                    )

                                    val pendIntent =
                                        PendingIntent.getActivity(context, 0, intent, 0)

                                    launcher.launch(
                                        IntentSenderRequest
                                            .Builder(pendIntent)
                                            .build()
                                    )
                                })
                    }



                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        OutlinedTextField(
                            value = viewModel.description, onValueChange = {
                                viewModel.onChangeDescription(it)
                            },
                            label = {
                                Text(
                                    text = "Message"
                                )

                            }, maxLines = 5
                        )

                        Spacer(modifier = Modifier.width(5.dp))
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Speack desc",
                            modifier = Modifier
                                .size(30.dp)
                                .clickable {


                                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                                    intent.putExtra(
                                        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                                    )
                                    intent.putExtra(
                                        RecognizerIntent.EXTRA_LANGUAGE,
                                        Locale.getDefault()
                                    )
                                    intent.putExtra(
                                        RecognizerIntent.EXTRA_PROMPT,
                                        "Please speak now"
                                    )

                                    val pendIntent =
                                        PendingIntent.getActivity(context, 0, intent, 0)

                                    descLauncher.launch(
                                        IntentSenderRequest
                                            .Builder(pendIntent)
                                            .build()
                                    )
                                })
                    }


                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(text = "Assign an icon")
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    ScrollableTabRow(
                        selectedTabIndex = when {
                            viewModel.icon != "" -> viewModel.reminderIcons.indexOfFirst {
                                it.name == viewModel.icon
                            }
                            else -> 0
                        },
                        edgePadding = 1.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),

                        indicator = emptyTabIndicator,
                        backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.2f)

                    ) {
                        viewModel.reminderIcons.forEachIndexed { _, icon ->
                            Tab(
                                selected = icon.name == viewModel.icon,
                                onClick = {
                                    viewModel.onChangeIcon(icon.name)
                                },

                                ) {
                                IconChip(
                                    icon = icon,
                                    selected = icon.name == viewModel.icon,
                                    modifier = Modifier
                                        .padding(horizontal = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(text = "Reminder details")
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        OutlinedButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                showNotificationSettings.value = !showNotificationSettings.value
                            }) {
                            Text(text = "Notification settings")
                        }


                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        when {
                            viewModel.calendar != null -> Icon(
                                imageVector = Icons.Default.Close,
                                tint = Color.Red,
                                contentDescription = "",
                                modifier = Modifier.clickable {
                                    viewModel.onChangeDateTime(null)
                                }
                            )
                            else -> Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Date range"
                            )
                        }



                        Spacer(modifier = Modifier.width(10.dp))
                        OutlinedButton(
                            modifier = Modifier.fillMaxWidth(),

                            onClick = {
                                if (viewModel.calendar == null) {
                                    viewModel.setNewCalendar()
                                }

                                datePickerDialog.show()

                            }) {
                            Text(
                                text = when {
                                    viewModel.calendar != null -> viewModel.calendar!!.time.formatToDateString()
                                    else -> "Not set"
                                }
                            )
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),

                        ) {
                        Icon(imageVector = Icons.Default.Alarm, contentDescription = "Date range")
                        Spacer(modifier = Modifier.width(10.dp))
                        OutlinedButton(
                            modifier = Modifier.fillMaxWidth(),
                            enabled = viewModel.calendar != null,
                            onClick = {
                                timePickerDialog.show()
                            }) {
                            Text(
                                text = when {
                                    viewModel.calendar != null -> viewModel.calendar!!.time.formatToTimeString()
                                    else -> "Not set"
                                }
                            )
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                    ) {
                        when {
                            viewModel.location != null -> Icon(
                                imageVector = Icons.Default.Close,
                                tint = Color.Red,
                                contentDescription = "",
                                modifier = Modifier.clickable {
                                    viewModel.onChangeLocation(null)
                                }
                            )
                            else -> Icon(
                                imageVector = Icons.Default.AddLocation,
                                contentDescription = "Date range"
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))
                        OutlinedButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                showLocationPicker.value = !showLocationPicker.value
                            }) {
                            when {
                                viewModel.location != null -> viewModel.location?.formatLocationString()
                                else -> "Select Location"
                            }?.let { it1 ->
                                Text(
                                    text = it1
                                )
                            }
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                    ) {
                        when {
                            viewModel.image != null -> Icon(
                                imageVector = Icons.Default.Close,
                                tint = Color.Red,
                                contentDescription = "Date range",
                                modifier = Modifier.clickable {
                                    viewModel.onChangeImage(null)
                                }
                            )
                            else -> Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Date range"
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))
                        OutlinedButton(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = {
                                selectImageLauncher.launch(
                                    "image/*"
                                )
                            },

                            ) {
                            if (viewModel.image != null) {
                                GlideImage(imageModel = viewModel.image)
                            } else {
                                Text(text = "Select Image")
                            }
                        }

                        DropdownMenu(
                            expanded = expandImageMenu.value,
                            onDismissRequest = { /*TODO*/ }) {
                            DropdownMenuItem(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { }
                            ) {
                                Text(
                                    text = "Delete",
                                    color = Color.DarkGray,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showLocationPicker.value) {
            LocationPicker(toggleLocationPicker = {
                showLocationPicker.value = !showLocationPicker.value
            }, confirmLocation = {
                viewModel.onChangeLocation(it)
            }, location = viewModel.location)
        }

        if (showNotificationSettings.value) {
            NotificationSettings(

                toggleNotifySettings = {
                    showNotificationSettings.value = !showNotificationSettings.value
                },
                notificationStatus = viewModel.enableNotification,
                onTime = viewModel.onTime,
                fiveMinsBefore = viewModel.fiveMins,
                tenMinsBefore = viewModel.tenMins,
                fifteenMinsBeofore = viewModel.fifteenMins,
                thirtyMinsBefore = viewModel.thirtyMins,
                onSave = { b: Boolean, b1: Boolean, b2: Boolean, b3: Boolean, b4: Boolean, b5: Boolean ->
                    viewModel.onNotificationChange(
                        notificationStatus = b,
                        onTimeStatus = b1,
                        fiveMinsStatus = b2,
                        tenMinsStatus = b3,
                        fifteenMinsStatus = b4,
                        thirtyMInsStatus = b5
                    )
                    showNotificationSettings.value = !showNotificationSettings.value

                }
            )
        }


        if (markAsDoneReminder.value) {
            AlertDialog(onDismissRequest = { /*TODO*/ },
                title = {
                    Text(text = "Mark Reminder as Done")
                },
                text = {
                    Text(text = "Are you sure you mark this as DONE ?")
                },
                buttons = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp), horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Button(modifier = Modifier.width(100.dp),
                            onClick = {
                                markAsDoneReminder.value = false
                            }) {
                            Text(text = "Cancel")
                        }

                        Button(
                            modifier = Modifier.width(100.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.onError,
                                contentColor = Color.Black
                            ),
                            onClick = {
                                viewModel.markAsDone()
                            }) {
                            Text(text = "Done")
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun IconChip(icon: ImageVector, selected: Boolean, modifier: Modifier) {

    Surface(
        color = Color.Black,
        contentColor = when {
            selected -> MaterialTheme.colors.secondary
            else -> Color.White
        },
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Icon(imageVector = icon, contentDescription = "")
    }
}

@SuppressLint("MissingPermission")
@Composable
private fun LocationPicker(
    toggleLocationPicker: () -> Unit,
    confirmLocation: (value: LatLng) -> Unit,
    location: LatLng?
) {

    var defaultLocation = location ?: LatLng(1.0, 1.0)
    val context = LocalContext.current
    val cam = rememberCameraPositionState()

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
                Text(text = "Pick a reminder Trigger Location", color = Color.Black)
                GoogleMap(

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    cameraPositionState = cam,
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
                Button(
                    modifier = Modifier.width(150.dp),
                    onClick = {

                        val src = CancellationTokenSource()
                        val ct: CancellationToken = src.token
                        val client = LocationServices.getFusedLocationProviderClient(context)
                        client.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, ct)
                            .addOnSuccessListener {
                                cam.position = CameraPosition.fromLatLngZoom(
                                    LatLng(it.latitude, it.longitude),
                                    15f
                                )
                                confirmLocation(LatLng(it.latitude, it.longitude))
                            }

                    }) {
                    Text(text = "Set current Location")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {


                    Button(
                        modifier = Modifier.width(150.dp),
                        enabled = location != null,
                        onClick = { /*TODO*/ }) {
                        Text(text = "Confirm")
                    }

                    Button(
                        modifier = Modifier.width(150.dp),
                        onClick = { /*TODO*/ }) {
                        Text(text = "Cancel")
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationSettings(
    notificationStatus: Boolean,
    onTime: Boolean,
    fiveMinsBefore: Boolean,
    tenMinsBefore: Boolean,
    fifteenMinsBeofore: Boolean,
    thirtyMinsBefore: Boolean,
    toggleNotifySettings: () -> Unit,
    onSave: (
        notificationStatus: Boolean,
        onTime: Boolean,
        fiveMinsBefore: Boolean,
        tenMinsBefore: Boolean,
        fifteenMinsBeofore: Boolean,
        thirtyMinsBefore: Boolean,
    ) -> Unit
) {

    Dialog(onDismissRequest = {
        toggleNotifySettings()
    }) {
        val context = LocalContext.current

        var notificationStatusVal = remember {
            mutableStateOf(notificationStatus)
        }

        var onTimeVal = remember {
            mutableStateOf(onTime)
        }

        var fiveMinVal = remember {
            mutableStateOf(fiveMinsBefore)
        }

        var tenMinVal = remember {
            mutableStateOf(tenMinsBefore)
        }

        var fifteenMinVal = remember {
            mutableStateOf(fifteenMinsBeofore)
        }

        var thirtyMinVal = remember {
            mutableStateOf(thirtyMinsBefore)
        }

        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colors.onSurface
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Modify Notification settings", color = Color.Black)

                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(text = "Notifications : ", color = Color.Black)
                    Switch(
                        checked = notificationStatusVal.value,
                        onCheckedChange = {
                            notificationStatusVal.value = !notificationStatusVal.value
                        }
                    )
                    Text(
                        text = when {
                            notificationStatusVal.value -> "enabled"
                            else -> "disabled"
                        }, color = Color.Black
                    )
                }
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(text = "On Time : ", color = Color.Black)
                    Checkbox(
                        enabled = notificationStatusVal.value,
                        modifier = Modifier.background(Color.Black),
                        checked = onTimeVal.value,
                        onCheckedChange = {
                            onTimeVal.value = !onTimeVal.value
                        }
                    )
                }
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(text = "5 Minutes before :  ", color = Color.Black)
                    Checkbox(
                        enabled = notificationStatusVal.value,
                        modifier = Modifier.background(Color.Black),
                        checked = fiveMinVal.value,
                        onCheckedChange = {

                            fiveMinVal.value = !fiveMinVal.value
                        }
                    )
                }
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(text = "10 Minutes before : ", color = Color.Black)
                    Checkbox(
                        enabled = notificationStatusVal.value,
                        modifier = Modifier.background(Color.Black),
                        checked = tenMinVal.value,
                        onCheckedChange = {
                            tenMinVal.value = !tenMinVal.value
                        }
                    )
                }

                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(text = "15 Minutes before : ", color = Color.Black)
                    Checkbox(
                        enabled = notificationStatusVal.value,
                        modifier = Modifier.background(Color.Black),
                        checked = fifteenMinVal.value,
                        onCheckedChange = {
                            fifteenMinVal.value = !fifteenMinVal.value
                        }
                    )
                }

                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(text = "30 Minutes before : ", color = Color.Black)
                    Checkbox(
                        enabled = notificationStatusVal.value,
                        modifier = Modifier.background(Color.Black),
                        checked = thirtyMinVal.value,
                        onCheckedChange = {
                            thirtyMinVal.value = !thirtyMinVal.value
                        }
                    )
                }

                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    Button(
                        modifier = Modifier.width(150.dp),
                        onClick = {
                            if (notificationStatusVal.value && !onTimeVal.value
                                && !fiveMinVal.value
                                && !tenMinVal.value
                                && !fifteenMinVal.value
                                && !thirtyMinVal.value
                            ) {
                                Toast.makeText(
                                    context,
                                    "At least one Notification Required",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                onSave(
                                    notificationStatusVal.value,
                                    onTimeVal.value,
                                    fiveMinVal.value,
                                    tenMinVal.value,
                                    fifteenMinVal.value,
                                    thirtyMinVal.value
                                )
                            }


                        }) {
                        Text(text = "Confirm")
                    }

                    Button(
                        modifier = Modifier.width(150.dp),
                        onClick = { /*TODO*/ }) {
                        Text(text = "Cancel")
                    }
                }
            }
        }
    }
}

private fun Date.formatToDateString(): String {
    return SimpleDateFormat("MM dd,yyyy", Locale.getDefault()).format(this)
}

private fun Date.formatToTimeString(): String {
    return SimpleDateFormat("kk:mm", Locale.getDefault()).format(this)
}

private fun LatLng.formatLocationString(): String {
    return "Lat:${this.latitude}/Lon:${this.longitude}"
}

private val emptyTabIndicator: @Composable (List<TabPosition>) -> Unit = {}




