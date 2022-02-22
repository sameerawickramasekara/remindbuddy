package com.sameeraw.remindbuddy.ui.login

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.sameeraw.remindbuddy.repository.UserRepository
import com.sameeraw.remindbuddy.ui.navigation.Screen
import kotlinx.coroutines.flow.collect


@ExperimentalComposeUiApi
@Composable
fun PINLogin(
    viewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(
            UserRepository(LocalContext.current)
        )
    ),
    navController: NavHostController
) {

    val con = LocalContext.current
    val (editValue, setEditValue) = remember { mutableStateOf("") }
    val otpLength = remember { 4 }
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    val checkedState = rememberSaveable { mutableStateOf(true) }


    LaunchedEffect(key1 = true){
        viewModel.eventsFlow.collect { event->
            when(event){
                is LoginViewModel.Event.navigateToHome -> {
                    setEditValue("")
                    navController.navigate(Screen.Home.route)
                }
                is LoginViewModel.Event.loginError -> {
                    Toast.makeText(con, "Invalid PIN Code!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Remindbuddy",
            fontSize = 50.sp,
            color = MaterialTheme.colors.primary,
            fontFamily = FontFamily.Cursive
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        )
        Text(text = "Please enter the 4 digit PIN Code")
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
        )

        TextField(
            value = editValue,
            onValueChange = {
                if (it.length <= otpLength) {
                    setEditValue(it)
                }
                if (it.length == 4) {
                    viewModel.onLoginWithPINClicked(it,checkedState.value)
                }
            },
            modifier = Modifier
                .size(0.dp)
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            )
        )
        Row() {

            PINTextField(modifier = Modifier
                .size(60.dp)
                .clickable {
                    focusRequester.requestFocus()
                    keyboard?.show()
                }
                .border(1.dp, Color.DarkGray),
                value = editValue.getOrNull(0)?.toString() ?: "",
                )

            Spacer(modifier = Modifier.size(8.dp))

            PINTextField(modifier = Modifier
                .size(60.dp)
                .clickable {
                    focusRequester.requestFocus()
                    keyboard?.show()
                }
                .border(1.dp, Color.DarkGray),
                value = editValue.getOrNull(1)?.toString() ?: "",
                )

            Spacer(modifier = Modifier.size(8.dp))

            PINTextField(modifier = Modifier
                .size(60.dp)
                .clickable {
                    focusRequester.requestFocus()
                    keyboard?.show()
                }
                .border(1.dp, Color.DarkGray),
                value = editValue.getOrNull(2)?.toString() ?: "",
                )

            Spacer(modifier = Modifier.size(8.dp))

            PINTextField(modifier = Modifier
                .size(60.dp)
                .clickable {
                    focusRequester.requestFocus()
                    keyboard?.show()
                }
                .border(1.dp, Color.DarkGray),
                value = editValue.getOrNull(3)?.toString() ?: "",
               )

            Spacer(modifier = Modifier.size(8.dp))
        }
        Row(modifier=Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 30.dp)) {
            Checkbox(
                checked = checkedState.value,
                onCheckedChange = { checkedState.value = it }
            )
            Text(text = "Keep me logged in")
        }


    }

    DisposableEffect(Unit) {
        focusRequester.requestFocus()
        onDispose { }
    }
}