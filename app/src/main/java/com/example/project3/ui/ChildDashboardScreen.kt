package com.example.project3.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.material.icons.automirrored.filled.ArrowBack

data class GameLevel(
    val level: Int,
    val gameNumber: Int,
    val title: String,
    val description: String,
    val isLocked: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildDashboardScreen(
    navController: NavController,
    childEmail: String,
    childDashboardViewModel: ChildDashboardViewModel = viewModel(
        factory = ChildDashboardViewModel.Factory
    ),
    onUpClick: () -> Unit = {}
) {
    LaunchedEffect(childEmail) {
        childDashboardViewModel.loadChildData(childEmail)
    }

    // List of all the current game levels
    val gameLevels = remember(childDashboardViewModel.isLevel2Unlocked) {
                listOf(
                    GameLevel(1, 1, "Level 1 - Game 1", "Learn basic movements: Up, Down, Right"),
                    GameLevel(1, 2, "Level 1 - Game 2", "Follow the path"),
                    GameLevel(1, 3, "Level 1 - Game 3", "Escape the maze"),
                    GameLevel(2, 1, "Level 2 - Game 1", "Master loops for efficiency", !childDashboardViewModel.isLevel2Unlocked),
                    GameLevel(2, 2, "Level 2 - Game 2", "Navigate complex patterns", !childDashboardViewModel.isLevel2Unlocked),
                    GameLevel(2, 3, "Level 2 - Game 3", "Use functions like a pro", !childDashboardViewModel.isLevel2Unlocked),
               )
            }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Welcome, ${childDashboardViewModel.childFirstName}!") },
                navigationIcon = {
                    IconButton(onClick = onUpClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE3F2FD)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Your Progress",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Games Completed", fontSize = 12.sp, color = Color.Gray)
                                Text(
                                    "${childDashboardViewModel.completedGames}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                            Column {
                                Text("Total Score", fontSize = 12.sp, color = Color.Gray)
                                Text(
                                    "${childDashboardViewModel.totalScore}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF9800)
                                )
                            }
                            Column {
                                Text("Avg Score", fontSize = 12.sp, color = Color.Gray)
                                Text(
                                    String.format("%.0f", childDashboardViewModel.averageScore),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2196F3)
                                )
                            }
                        }
                    }
                }
            }
            item {
                Text(
                    "Level 1 - Beginner",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            items(gameLevels.filter { it.level == 1 }) { game ->
                GameLevelCard(
                    game = game,
                    onClick = {
                        navController.navigate(Routes.GameLevel(childEmail, game.level, game.gameNumber))
                    }
                )
            }
            item {
                Text(
                    "Level 2 - Advanced",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
                if (!childDashboardViewModel.isLevel2Unlocked) {
                    Text(
                        "Complete all Level 1 games to unlock!",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            items(gameLevels.filter { it.level == 2 }) { game ->
                GameLevelCard(
                    game = game,
                    onClick = {
                        if (!game.isLocked) {
                            navController.navigate(Routes.GameLevel(childEmail, game.level, game.gameNumber))
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun GameLevelCard(
    game: GameLevel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .then(
                if (!game.isLocked) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (game.isLocked) Color(0xFFEEEEEE) else Color(0xFFFFF3E0)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (game.isLocked) Color.Gray
                        else if (game.level == 1) Color(0xFF4CAF50)
                        else Color(0xFFFF9800)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (game.isLocked) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                } else {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Available",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    game.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (game.isLocked) Color.Gray else Color.Black
                )
                Text(
                    game.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (game.isLocked) Color.Gray else Color.DarkGray,
                    fontSize = 14.sp
                )
            }
        }
    }
}