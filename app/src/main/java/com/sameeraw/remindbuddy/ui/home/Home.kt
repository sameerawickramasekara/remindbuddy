package com.sameeraw.remindbuddy.ui.home

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.accompanist.insets.systemBarsPadding
import com.sameeraw.remindbuddy.repository.UserRepository
import com.sameeraw.remindbuddy.ui.home.reminder.ReminderList
import com.sameeraw.remindbuddy.ui.navigation.Screen
import kotlinx.coroutines.flow.collect

val NEW_REMINDER = -1L

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
    val profileMenuExpanded = rememberSaveable {
        mutableStateOf(false)
    }
    val showAddReminder = rememberSaveable {
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
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Default.Search,
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
                                    Text("Profile")
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
                ReminderList(navController = navController)
            }

        }
    }
}