package com.sameeraw.remindbuddy.ui.login

import androidx.compose.foundation.layout.Box
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier

@ExperimentalComposeUiApi
@Composable
fun PINTextField(
    modifier: Modifier,
    value: String
) {
    Box(
        modifier = modifier
    ) {

        Text(
            text = value,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}