package com.example.project3.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable

@Serializable
sealed class Routes {
    @Serializable
    data object Home

    @Serializable
    data object Login

    @Serializable
    data object Registration
}

@Composable
fun EducationalGameApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Home
    ) {
        composable<Routes.Home> {
            HomeScreen(navController)
        }

        composable<Routes.Login> {
            LoginScreen(navController)
        }

        composable<Routes.Registration> {
            RegistrationScreen(navController)
        }
    }
}