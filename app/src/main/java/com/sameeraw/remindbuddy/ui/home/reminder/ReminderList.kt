package com.sameeraw.remindbuddy.ui.home.reminder

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ReminderList(
    viewModel: ReminderListViewModel = viewModel()
) {
    val viewState by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
        LazyColumn(
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(viewState.reminders) { item ->
                ReminderListItem(reminder = item)
            }

        }
    }


}