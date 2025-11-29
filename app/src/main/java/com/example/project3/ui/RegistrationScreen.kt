package com.example.project3.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project3.data.Validation

@Composable
fun RegistrationScreen(
    navController: NavController,
    registrationViewModel: RegistrationViewModel = viewModel(
        factory = RegistrationViewModel.Factory
    )
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = "Register",
                    style = MaterialTheme.typography.displayLarge
                )
            }
            item {
                Spacer(
                    modifier = Modifier.padding(16.dp)
                )
            }
            item {
                FirstNameField(
                    labelText = "First Name",
                    textInput = registrationViewModel.firstNameInput,
                    onValueChange = { registrationViewModel.firstNameInput = it }
                )
            }
            item {
                LastNameField(
                    labelText = "Last Name",
                    textInput = registrationViewModel.lastNameInput,
                    onValueChange = { registrationViewModel.lastNameInput = it }
                )
            }
            item {
                RegistrationEmailField(
                    labelText = "Email",
                    textInput = registrationViewModel.emailInput,
                    onValueChange = { registrationViewModel.emailInput = it }
                )
            }
            item {
                RegistrationPasswordField(
                    labelText = "Password",
                    textInput = registrationViewModel.passwordInput,
                    onValueChange = { registrationViewModel.passwordInput = it }
                )
            }
            item {
                Spacer(
                    modifier = Modifier.padding(16.dp)
                )
            }
            item {
                Button(
                    onClick = {
                        registrationViewModel.showErrors = true
                        registrationViewModel.errorMessage = Validation().validateRegistration(
                            registrationViewModel.firstNameInput,
                            registrationViewModel.lastNameInput,
                            registrationViewModel.emailInput,
                            registrationViewModel.passwordInput
                        )

                        if (registrationViewModel.errorMessage.isEmpty()) {
                            navController.navigate(Routes.Home)
                            registrationViewModel.registerParent(
                                registrationViewModel.firstNameInput,
                                registrationViewModel.lastNameInput,
                                registrationViewModel.emailInput,
                                registrationViewModel.passwordInput
                            )
                        }
                    },
                    modifier = Modifier.size(width = 200.dp, height = 50.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color(0xff62a3d1)
                    )
                ) {
                    Text(text = "Register")
                }
            }
            item {
                Spacer(
                    modifier = Modifier.padding(16.dp)
                )
            }
            if (registrationViewModel.showErrors && registrationViewModel.errorMessage.isNotEmpty()) {
                item {
                    Text(
                        text = registrationViewModel.errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun FirstNameField(
    labelText: String,
    textInput: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = textInput,
        onValueChange = onValueChange,
        label = { Text(labelText) },
        modifier = modifier.padding(25.dp)
    )
}

@Composable
fun LastNameField(
    labelText: String,
    textInput: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = textInput,
        onValueChange = onValueChange,
        label = { Text(labelText) },
        modifier = modifier.padding(25.dp)
    )
}

@Composable
fun RegistrationEmailField(
    labelText: String,
    textInput: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = textInput,
        onValueChange = onValueChange,
        label = { Text(labelText) },
        modifier = modifier.padding(25.dp)
    )
}

@Composable
fun RegistrationPasswordField(
    labelText: String,
    textInput: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = textInput,
        onValueChange = onValueChange,
        label = { Text(labelText) },
        modifier = modifier.padding(25.dp)
    )
}