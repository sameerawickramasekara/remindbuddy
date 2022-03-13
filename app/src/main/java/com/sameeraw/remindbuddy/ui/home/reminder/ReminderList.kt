package com.sameeraw.remindbuddy.ui.home.reminder

import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.LatLng
import com.sameeraw.remindbuddy.data.entity.Reminder
import com.sameeraw.remindbuddy.ui.navigation.Screen

@Composable
fun ReminderList(
    viewModel: ReminderListViewModel = hiltViewModel(),
    navController: NavHostController,
    showAll: Boolean = false,
    location: LatLng?
) {
    val reminders = viewModel.reminders.collectAsState(initial = emptyList())


    val deleteReminder = rememberSaveable {
        mutableStateOf<Reminder?>(null)
    }


    if (deleteReminder.value != null) {
        AlertDialog(onDismissRequest = { /*TODO*/ },
            title = {
                Text(text = "Delete Reminder")
            },
            text = {
                deleteReminder.value?.let {
                    Text(text = "Are you sure you want to delete the reminder titled : " + deleteReminder.value!!.title)
                }
            },
            buttons = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(modifier = Modifier.width(100.dp),
                        onClick = {
                            deleteReminder.value = null
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
                            viewModel.deleteReminder(deleteReminder.value!!)
                            deleteReminder.value = null
                        }) {
                        Text(text = "Delete")
                    }
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        LazyColumn(
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            items(reminders.value.filter {
                if (!showAll) {
                    if (location != null) {
                        if (it.locationX != null && it.locationY != null) {
                            val temp = Location(LocationManager.GPS_PROVIDER)
                            temp.latitude = it.locationX.toDouble()
                            temp.longitude = it.locationY.toDouble()

                            val loc = Location(LocationManager.GPS_PROVIDER)
                            loc.latitude = location.latitude
                            loc.longitude = location.longitude

                            Log.d("LOCA", loc.distanceTo(temp).toString())
                            (loc.distanceTo(temp) < 200)
                        } else {
                            false
                        }

                    } else (it.locationX == null && it.locationY == null && it.reminderTime == null) || it.reminderSeen
                } else {
                    true
                }
            }) { item ->
                ReminderListItem(reminder = item, onItemClick = {
                    navController.navigate(Screen.AddEditReminder.route + "?reminderId=${item.id}")
                }, onDeleteClick = {
                    deleteReminder.value = item
                })
            }

        }
    }
}