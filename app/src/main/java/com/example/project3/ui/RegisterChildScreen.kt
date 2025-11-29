package com.example.project3.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project3.data.Validation

@Composable
fun RegisterChildScreen(
    navController: NavController,
    parentEmail: String,
    registerChildViewModel: RegisterChildViewModel = viewModel(
        factory = RegisterChildViewModel.Factory
    ),
    onUpClick: () -> Unit = { }
) {
    LaunchedEffect(parentEmail) {
        registerChildViewModel.loadParentId(parentEmail)
    }

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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text(
                        text = "Register Child",
                        style = MaterialTheme.typography.displayLarge,
                        modifier = Modifier.padding(top = 30.dp)
                    )
                }
                item {
                    Spacer(modifier = Modifier.padding(16.dp))
                }
                item {
                    TextField(
                        value = registerChildViewModel.firstNameInput,
                        onValueChange = { registerChildViewModel.firstNameInput = it },
                        label = { Text("First Name") },
                        modifier = Modifier.padding(25.dp)
                    )
                }
                item {
                    TextField(
                        value = registerChildViewModel.lastNameInput,
                        onValueChange = { registerChildViewModel.lastNameInput = it },
                        label = { Text("Last Name") },
                        modifier = Modifier.padding(25.dp)
                    )
                }
                item {
                    TextField(
                        value = registerChildViewModel.emailInput,
                        onValueChange = { registerChildViewModel.emailInput = it },
                        label = { Text("Email") },
                        modifier = Modifier.padding(25.dp)
                    )
                }
                item {
                    TextField(
                        value = registerChildViewModel.passwordInput,
                        onValueChange = { registerChildViewModel.passwordInput = it },
                        label = { Text("Password") },
                        modifier = Modifier.padding(25.dp)
                    )
                }
                item {
                    Spacer(modifier = Modifier.padding(innerPadding))
                }
                item {
                    Button(
                        onClick = {
                            registerChildViewModel.showErrors = true
                            registerChildViewModel.errorMessage = Validation().validateRegistration(
                                registerChildViewModel.firstNameInput,
                                registerChildViewModel.lastNameInput,
                                registerChildViewModel.emailInput,
                                registerChildViewModel.passwordInput
                            )

                            if (registerChildViewModel.errorMessage.isEmpty()) {
                                registerChildViewModel.registerChild { success ->
                                    if (success) {
                                        navController.popBackStack()
                                    }
                                }
                            }
                        },
                        modifier = Modifier.size(width = 200.dp, height = 50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xff62a3d1)
                        )
                    ) {
                        Text(text = "Register Child")
                    }
                }
                item {
                    Spacer(modifier = Modifier.padding(innerPadding))
                }
                if (registerChildViewModel.showErrors && registerChildViewModel.errorMessage.isNotEmpty()) {
                    item {
                        Text(
                            text = registerChildViewModel.errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}