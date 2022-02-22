package com.sameeraw.remindbuddy.ui.home.reminder


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.insets.systemBarsPadding
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.sameeraw.remindbuddy.ui.navigation.Screen
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.flow.collect
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun AddEditReminder(
    navController: NavHostController,
    viewModel: ReminderViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.eventsFlow.collect { event ->
            when (event) {
                is ReminderViewModel.ReminderEvent.AddSuccess -> {
                    Toast.makeText(context, "Reminder Added", Toast.LENGTH_LONG).show()
                    navController.navigate(Screen.Home.route)
                }
            }
        }

    }

    val showLocationPicker = rememberSaveable {
        mutableStateOf(false)
    }

    val calendar = remember {
        Calendar.getInstance()
    }

    val datePickerDialog = DatePickerDialog(
        LocalContext.current,
        { view, year, month, dayOfMonth ->
            val cal2 = viewModel.calendar.clone() as Calendar
            cal2[Calendar.DAY_OF_MONTH] = dayOfMonth
            cal2[Calendar.MONTH] = month
            cal2[Calendar.YEAR] = year
            viewModel.onChangeDateTime(cal2)
        },
        viewModel.calendar[Calendar.YEAR],
        viewModel.calendar[Calendar.MONTH],
        viewModel.calendar[Calendar.DAY_OF_MONTH]
    )

    val timePickerDialog = TimePickerDialog(
        LocalContext.current,
        { _, hour: Int, minute: Int ->
            val cal2 = viewModel.calendar.clone() as Calendar
            cal2[Calendar.HOUR_OF_DAY] = hour
            cal2[Calendar.MINUTE] = minute
            viewModel.onChangeDateTime(cal2)
        }, calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE], true
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
                        Button(onClick = { /*TODO*/ }) {
                            Text(text = "Cancel")
                        }
                    })
                Text(text = "Please fill below details...")

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),

                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    OutlinedTextField(value = viewModel.title, onValueChange = {
                        viewModel.onChangeTitle(it)
                    }, label = {
                        Text(
                            text = "Title"
                        )
                    },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = viewModel.description, onValueChange = {
                            viewModel.onChangeDescription(it)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(
                                text = "Message"
                            )

                        }, maxLines = 5
                    )

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
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Date range"
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        OutlinedButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                datePickerDialog.show()

                            }) {
                            Text(text = viewModel.calendar.time.formatToDateString())
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
                            onClick = {
                                timePickerDialog.show()
                            }) {
                            Text(text = viewModel.calendar.time.formatToTimeString())
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

@Composable
private fun LocationPicker(
    toggleLocationPicker: () -> Unit,
    confirmLocation: (value: LatLng) -> Unit,
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
                Text(text = "Pick a reminder Trigger Location", color = Color.Black)
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




