package com.example.project3.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
sealed class Routes {
    @Serializable
    data object Home

    @Serializable
    data object Login

    @Serializable
    data object Registration

    @Serializable
    data class ParentDashboard(val parentEmail: String)

    @Serializable
    data class RegisterChild(val parentEmail: String)

    @Serializable
    data class ChildProgress(val childEmail: String)
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
            LoginScreen(
                navController,
                onUpClick = {
                    navController.navigateUp()
                }
            )
        }

        composable<Routes.Registration> {
            RegistrationScreen(
                navController,
                onUpClick = {
                    navController.navigateUp()
                }
            )
        }

        composable<Routes.ParentDashboard> { backStackEntry ->
            val args = backStackEntry.toRoute<Routes.ParentDashboard>()
            ParentDashboardScreen(
                navController = navController,
                parentEmail = args.parentEmail,
                onUpClick = {
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Home) { inclusive = false }
                    }
                }
            )
        }

        composable<Routes.RegisterChild> { backStackEntry ->
            val args = backStackEntry.toRoute<Routes.RegisterChild>()
            RegisterChildScreen(
                navController = navController,
                parentEmail = args.parentEmail,
                onUpClick = {
                    navController.navigateUp()
                }
            )
        }

        composable<Routes.ChildProgress> { backStackEntry ->
            val args = backStackEntry.toRoute<Routes.ChildProgress>()
            ChildProgressScreen(
                navController = navController,
                childEmail = args.childEmail,
                onUpClick = {
                    navController.navigateUp()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationalGameAppBar(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = false,
    onUpClick: () -> Unit = { },
) {
    TopAppBar(
        title = { Text("") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = onUpClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
            }
        }
    )
}