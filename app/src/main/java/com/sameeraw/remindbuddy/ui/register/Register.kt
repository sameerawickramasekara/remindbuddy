package com.sameeraw.remindbuddy.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.insets.statusBarsPadding
import com.sameeraw.remindbuddy.repository.UserRepository
import com.sameeraw.remindbuddy.ui.navigation.Screen
import com.sameeraw.remindbuddy.ui.register.RegisterViewModel
import com.sameeraw.remindbuddy.ui.register.RegisterViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect

@Composable
fun Register(
    viewModel: RegisterViewModel = viewModel(
        factory = RegisterViewModelFactory(
            UserRepository(LocalContext.current)
        )
    ),
    navController: NavHostController,
) {

    Surface() {

        val newUserName = rememberSaveable { mutableStateOf("") }
        val newPassword = rememberSaveable { mutableStateOf("") }
        val confirmPassword = rememberSaveable { mutableStateOf("") }
        val newPinCode = rememberSaveable { mutableStateOf("") }
        val newEmail = rememberSaveable { mutableStateOf("") }


        val context = LocalContext.current

        LaunchedEffect(key1 = true ){
            viewModel.eventsFlow.collect {event ->
                when(event){
                    is RegisterViewModel.Event.RegisterSuccess -> {
                        Toast.makeText(context,"Successfully registered !, Please Login",Toast.LENGTH_LONG).show()
                        delay(500)
                        navController.navigate(Screen.Login.route)
                    }
                    is RegisterViewModel.Event.PasswordMisMatchError -> {
                        Toast.makeText(context,"Please re check the passwords",Toast.LENGTH_SHORT).show()
                    }
                    is RegisterViewModel.Event.PINFormatError -> {
                        Toast.makeText(context,"Please enter a 4 digit PIN",Toast.LENGTH_SHORT).show()
                    }
                    is RegisterViewModel.Event.MissingValuesError -> {
                        Toast.makeText(context,"Please provide all values",Toast.LENGTH_SHORT).show()
                    }
                    is RegisterViewModel.Event.EmailFormatError->{
                        Toast.makeText(context,"Please enter a valid email",Toast.LENGTH_SHORT).show()
                    }
                    is RegisterViewModel.Event.CreateError->{
                        Toast.makeText(context,"Error occurred while registering!",Toast.LENGTH_SHORT).show()
                    }


                    }
            }
        }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .navigationBarsWithImePadding()
                .verticalScroll(rememberScrollState()),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,

            ) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )
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
            Text(
                modifier = Modifier.padding(horizontal = 30.dp, vertical = 10.dp),
                text = "Please fill the details to create an Account",
                style = MaterialTheme.typography.body2,

                )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                value = newUserName.value,
                onValueChange = {
                    newUserName.value = it
                },
                label = { Text(text = "Username") })
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                value = newEmail.value,
                label = { Text(text = "Email") },
                onValueChange = {
                    newEmail.value = it
                }
            )

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                value = newPassword.value,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                label = { Text(text = "Password") },
                onValueChange = {
                    newPassword.value = it
                })
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
                    .navigationBarsWithImePadding()
                    ,
                value = confirmPassword.value,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                label = { Text(text = "Re-type Password") },
                onValueChange = {
                    confirmPassword.value = it
                })
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                value = newPinCode.value,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text(text = "4 Digit Pin Code") },
                onValueChange = {
                    if (it.length <= 4) {
                        newPinCode.value = it
                    }
                })
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )
            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp, vertical = 10.dp)
                    .height(50.dp),
                onClick = { viewModel.onRegisterClicked(
                    userName = newUserName.value,
                    password = newPassword.value,
                    retypePassword = confirmPassword.value,
                    email = newEmail.value,
                    pin = newPinCode.value
                )}) {
                Text(text = "Create Account")
            }
            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp, vertical = 10.dp)
                    .height(50.dp),
                onClick = { navController.navigate(Screen.Login.route)}) {
                Text(text = "Cancel")
            }
        }
    }
}