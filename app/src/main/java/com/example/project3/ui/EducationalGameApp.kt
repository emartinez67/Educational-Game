package com.example.project3.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.project3.ui.game.Level1Game1Screen
import com.example.project3.ui.game.Level1Game2Screen
import com.example.project3.ui.game.Level1Game3Screen
import com.example.project3.ui.game.Level2Game1Screen
import com.example.project3.ui.game.Level2Game2Screen
import com.example.project3.ui.game.Level2Game3Screen
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

    @Serializable
    data class ChildDashboard(val childEmail: String)

    @Serializable
    data class GameLevel(val childEmail: String, val level: Int, val gameNumber: Int)
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

        composable<Routes.GameLevel> { backStackEntry ->
            val args = backStackEntry.toRoute<Routes.GameLevel>()
            if (args.level == 1) {
                when (args.gameNumber) {
                    1 -> Level1Game1Screen(
                        navController = navController,
                        childEmail = args.childEmail,
                        gameNumber = args.gameNumber,
                        onUpClick = { navController.navigateUp() }
                    )
                    2 -> Level1Game2Screen(
                        navController = navController,
                        childEmail = args.childEmail,
                        gameNumber = args.gameNumber,
                        onUpClick = { navController.navigateUp() }
                    )
                    3 -> Level1Game3Screen(
                        navController = navController,
                        childEmail = args.childEmail,
                        gameNumber = args.gameNumber,
                        onUpClick = { navController.navigateUp() }
                    )
                    else -> Level1Game1Screen(
                        navController = navController,
                        childEmail = args.childEmail,
                        gameNumber = args.gameNumber,
                        onUpClick = { navController.navigateUp() }
                    )
                }
            }
            else if (args.level == 2) {
                when (args.gameNumber) {
                    1 -> Level2Game1Screen(
                        navController = navController,
                        childEmail = args.childEmail,
                        gameNumber = args.gameNumber,
                        onUpClick = { navController.navigateUp() }
                    )
                    2 -> Level2Game2Screen(
                        navController = navController,
                        childEmail = args.childEmail,
                        gameNumber = args.gameNumber,
                        onUpClick = { navController.navigateUp() }
                        )
                    3 -> Level2Game3Screen(
                        navController = navController,
                        childEmail = args.childEmail,
                        gameNumber = args.gameNumber,
                        onUpClick = { navController.navigateUp() }
                        )
                    else -> Level2Game1Screen(
                        navController = navController,
                        childEmail = args.childEmail,
                        gameNumber = args.gameNumber,
                        onUpClick = { navController.navigateUp() }
                    )
                }
            }
        }

        composable<Routes.ChildDashboard> { backStackEntry ->
            val args = backStackEntry.toRoute<Routes.ChildDashboard>()
            ChildDashboardScreen(
                navController = navController,
                childEmail = args.childEmail,
                onUpClick = {
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Home) { inclusive = false }
                    }
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
    title: String = ""
) {
    TopAppBar(
        title = {
            if (title.isNotEmpty()) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF2196F3)
        ),
        modifier = modifier.shadow(4.dp),
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(
                    onClick = onUpClick,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(40.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }
        }
    )
}