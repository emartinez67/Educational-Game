package com.example.project3.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project3.data.Validation
import kotlin.math.log

@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModel.Factory
    ),
    onUpClick: () -> Unit = { }
) {
    var selectedUser by remember{ mutableStateOf("") }
    val radioOptions = listOf("Child", "Parent")

    Scaffold(
        topBar = {
            EducationalGameAppBar(
                canNavigateBack = true,
                onUpClick = onUpClick
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text(
                        text = "Login",
                        style = MaterialTheme.typography.displayLarge,
                        modifier = Modifier.padding(top = 30.dp)
                    )
                }
                item {
                    Spacer(
                        modifier = Modifier.padding(16.dp)
                    )
                }
                item {
                    Row(
                        modifier = Modifier.selectableGroup(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "I am a: ",
                            fontSize = 30.sp
                        )
                        radioOptions.forEach { text ->
                            Column(
                                modifier = Modifier.selectable(
                                    selected = (text == selectedUser),
                                    onClick = {
                                        selectedUser = text
                                        loginViewModel.selectedUserType = selectedUser
                                    },
                                    role = Role.RadioButton
                                ),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                RadioButton(
                                    selected = (text == selectedUser),
                                    onClick = null,
                                    modifier = Modifier.padding(top = 10.dp)
                                )
                                Text(
                                    text = text,
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
                item {
                    LoginEmailField(
                        textInput = loginViewModel.emailInput,
                        labelText = "Email",
                        onValueChange = { loginViewModel.emailInput = it }
                    )
                }
                item {
                    LoginPasswordField(
                        textInput = loginViewModel.passwordInput,
                        labelText = "Password",
                        onValueChange = { loginViewModel.passwordInput = it }
                    )
                }
                item {
                    Spacer(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                item {
                    Button(
                        onClick = {
                            loginViewModel.showErrors = true
                            loginViewModel.errorMessage = Validation().validateLogin(
                                loginViewModel.emailInput,
                                loginViewModel.passwordInput
                            )

                            if (loginViewModel.errorMessage.isEmpty()) {
                                if (selectedUser.isEmpty()) {
                                    loginViewModel.errorMessage =
                                        "Please select user type (Child or Parent)"
                                } else if (selectedUser == "Parent") {
                                    loginViewModel.authenticateParent() { success, error ->
                                        if (success) {

                                        } else {
                                            loginViewModel.errorMessage = error
                                        }
                                    }
                                } else if (selectedUser == "Child") {
                                    loginViewModel.authenticateChild() { success, error ->
                                        if (success) {

                                        } else {
                                            loginViewModel.errorMessage = error
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier.size(width = 200.dp, height = 50.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = Color(0xff62a3d1)
                        )
                    ) {
                        Text(text = "Log In")
                    }
                }
                item {
                    Spacer(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                if (loginViewModel.showErrors && loginViewModel.errorMessage.isNotEmpty()) {
                    item {
                        Text(
                            text = loginViewModel.errorMessage,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoginEmailField(
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
fun LoginPasswordField(
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