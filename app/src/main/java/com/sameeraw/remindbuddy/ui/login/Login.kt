package com.sameeraw.remindbuddy.ui.login

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.sameeraw.remindbuddy.R
import com.sameeraw.remindbuddy.repository.UserRepository
import com.sameeraw.remindbuddy.ui.navigation.Screen
import kotlinx.coroutines.flow.collect

@Composable
fun Login(
    viewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(
            UserRepository(LocalContext.current)
        )
    ),
    navController: NavHostController,
) {




    val userName = rememberSaveable {
        mutableStateOf<String>("")
    }
    val password = rememberSaveable {
        mutableStateOf<String>("")
    }

    val checkedState = rememberSaveable { mutableStateOf(true) }


    val con = LocalContext.current

    LaunchedEffect(key1 = true){
        viewModel.eventsFlow.collect { event->
            when(event){
                is LoginViewModel.Event.navigateToHome -> navController.navigate(Screen.Home.route)
                is LoginViewModel.Event.loginError -> {
                    Toast.makeText(con, "Invalid Username Or Password", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Surface() {
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
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Login_img",
                modifier = Modifier.size(100.dp)
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                value = userName.value,
                onValueChange = {
                    userName.value = it
                },
                label = {
                    Text(text = stringResource(R.string.username_string))
                }
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                value = password.value,
                onValueChange = {
                    password.value = it
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),

                label = {
                    Text(text = stringResource(R.string.password_string))
                }
            )
            Row(modifier=Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 30.dp)) {
                Checkbox(
                    checked = checkedState.value,
                    onCheckedChange = { checkedState.value = it }
                )
                Text(text = "Keep me logged in")
            }
            
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
            )
            OutlinedButton(
                onClick = {
                    viewModel.onLoginClicked(userName.value,password.value,checkedState.value)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp, vertical = 10.dp)
                    .height(50.dp),
                shape = MaterialTheme.shapes.small
            ) {
                Text(text = "Login")
            }

            OutlinedButton(
                onClick = { navController.navigate(Screen.PINLogin.route)},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp, vertical = 10.dp)
                    .height(50.dp),
                shape = MaterialTheme.shapes.small
            ) {
                Text(text = "Login with PIN")
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
            )
            Text(text = "Don't have an Account?", textDecoration = TextDecoration.Underline, modifier = Modifier.clickable {
                navController.navigate(Screen.Register.route)
            })
        }

    }


}