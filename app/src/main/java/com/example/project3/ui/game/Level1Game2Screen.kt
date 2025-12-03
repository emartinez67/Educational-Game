package com.example.project3.ui.game

import android.media.MediaPlayer
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.project3.R
import com.example.project3.game.CommandType
import com.example.project3.game.GameCommand
import com.example.project3.game.GameCommands
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Level1Game2Screen(
    navController: NavController,
    childEmail: String,
    gameNumber: Int,
    gameViewModel: GameViewModel = viewModel(
        factory = GameViewModel.Factory
    ),
    onUpClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var playerPosition by remember { mutableStateOf(Pair(0, 0)) }
    val targetPosition = Pair(7, 7) // Bottom right corner
    var commandSequence by remember { mutableStateOf(listOf<CommandType>()) }
    var isAnimating by remember { mutableStateOf(false) }
    var gameCompleted by remember { mutableStateOf(false) }
    var attempts by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var errorMessage by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    // Define the valid path - only these positions are accessible
    val validPath = setOf(
        Pair(0, 0), // Start
        Pair(1, 0),
        Pair(2, 0),
        Pair(3, 0),
        Pair(3, 1),
        Pair(3, 2),
        Pair(3, 3),
        Pair(4, 3),
        Pair(5, 3),
        Pair(5, 4),
        Pair(5, 5),
        Pair(6, 5),
        Pair(7, 5),
        Pair(7, 6),
        Pair(7, 7)  // End
    )

    // The correct sequence to reach the target
    val correctSequence = listOf(
        CommandType.MOVE_RIGHT,
        CommandType.MOVE_RIGHT,
        CommandType.MOVE_RIGHT,
        CommandType.MOVE_DOWN,
        CommandType.MOVE_DOWN,
        CommandType.MOVE_DOWN,
        CommandType.MOVE_RIGHT,
        CommandType.MOVE_RIGHT,
        CommandType.MOVE_DOWN,
        CommandType.MOVE_DOWN,
        CommandType.MOVE_RIGHT,
        CommandType.MOVE_RIGHT,
        CommandType.MOVE_DOWN,
        CommandType.MOVE_DOWN
    )

    val animatedOffsetX by animateFloatAsState(
        targetValue = playerPosition.first.toFloat(),
        animationSpec = tween(durationMillis = 500, easing = LinearEasing),
        label = "playerX"
    )

    val animatedOffsetY by animateFloatAsState(
        targetValue = playerPosition.second.toFloat(),
        animationSpec = tween(durationMillis = 500, easing = LinearEasing),
        label = "playerY"
    )

    fun validateAndExecuteCommands() {
        if (isAnimating || commandSequence.isEmpty()) return

        errorMessage = ""

        // Check if the command sequence matches the correct sequence
        if (commandSequence != correctSequence) {
            errorMessage = "❌ Incorrect path! Try again."
            attempts++
            try {
                val mediaPlayer = MediaPlayer.create(context, R.raw.fail_sound)
                mediaPlayer?.start()
                mediaPlayer?.setOnCompletionListener { it.release() }
            } catch (e: Exception) {
            }
            return
        }

        // If correct, execute the commands
        isAnimating = true
        attempts++
        var currentPos = Pair(0, 0)

        coroutineScope.launch {
            for (command in commandSequence) {
                delay(600)
                currentPos = when (command) {
                    CommandType.MOVE_UP -> Pair(currentPos.first, maxOf(0, currentPos.second - 1))
                    CommandType.MOVE_DOWN -> Pair(currentPos.first, minOf(7, currentPos.second + 1))
                    CommandType.MOVE_RIGHT -> Pair(minOf(7, currentPos.first + 1), currentPos.second)
                    else -> currentPos
                }
                playerPosition = currentPos

                try {
                    val mediaPlayer = MediaPlayer.create(context, R.raw.move_sound)
                    mediaPlayer?.start()
                    mediaPlayer?.setOnCompletionListener { it.release() }
                } catch (e: Exception) {
                }
            }

            delay(600)

            if (currentPos == targetPosition) {
                gameCompleted = true
                score = maxOf(100 - (attempts - 1) * 10, 10)

                gameViewModel.saveGameProgress(
                    childEmail = childEmail,
                    level = 1,
                    gameNumber = gameNumber,
                    score = score,
                    attempts = attempts
                )

                try {
                    val mediaPlayer = MediaPlayer.create(context, R.raw.success_sound)
                    mediaPlayer?.start()
                    mediaPlayer?.setOnCompletionListener { it.release() }
                } catch (e: Exception) {
                }
            }

            isAnimating = false
        }
    }

    fun resetGame() {
        playerPosition = Pair(0, 0)
        commandSequence = emptyList()
        gameCompleted = false
        isAnimating = false
        errorMessage = ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Level 1 - Game 2: Maze Path") },
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = "Follow the only valid path to reach the star!",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Gray squares are blocked - find the correct route",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .size(320.dp)
                        .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                        .border(2.dp, Color(0xFF4CAF50), RoundedCornerShape(8.dp))
                ) {
                    // Draw grid cells
                    for (row in 0..7) {
                        for (col in 0..7) {
                            val isValidCell = validPath.contains(Pair(col, row))
                            Box(
                                modifier = Modifier
                                    .offset {
                                        IntOffset(
                                            (col * 40).dp.roundToPx(),
                                            (row * 40).dp.roundToPx()
                                        )
                                    }
                                    .size(40.dp)
                                    .background(
                                        if (isValidCell) Color.Transparent
                                        else Color.Gray.copy(alpha = 0.6f)
                                    )
                                    .border(
                                        1.dp,
                                        if (isValidCell) Color.Gray.copy(alpha = 0.3f)
                                        else Color.Gray.copy(alpha = 0.8f)
                                    )
                            )
                        }
                    }

                    // Draw star at target position
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Target",
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    (targetPosition.first * 40 + 10).dp.roundToPx(),
                                    (targetPosition.second * 40 + 10).dp.roundToPx()
                                )
                            }
                            .size(20.dp)
                            .then(
                                if (gameCompleted) Modifier
                                    .offset(y = (-3).dp)
                                else Modifier
                            ),
                        tint = Color(0xFFFFC107)
                    )

                    // Draw player
                    Box(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    (animatedOffsetX * 40 + 10).dp.roundToPx(),
                                    (animatedOffsetY * 40 + 10).dp.roundToPx()
                                )
                            }
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2196F3)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("😊", fontSize = 14.sp)
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                Text(
                    text = "Command Sequence:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .border(2.dp, Color.Gray, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    if (commandSequence.isEmpty()) {
                        Text(
                            "Drag commands here",
                            color = Color.Gray,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(commandSequence.withIndex().toList()) { (index, command) ->
                                val cmd = GameCommands.LEVEL_1_COMMANDS.find { it.type == command }
                                cmd?.let {
                                    CommandChip(
                                        command = it,
                                        onRemove = {
                                            commandSequence =
                                                commandSequence.toMutableList().apply {
                                                    removeAt(index)
                                                }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                Text(
                    text = "Available Commands:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GameCommands.LEVEL_1_COMMANDS.forEach { command ->
                        DraggableCommand(
                            command = command,
                            onDragEnd = { wasDropped ->
                                if (wasDropped && !isAnimating) {
                                    commandSequence = commandSequence + command.type
                                }
                            }
                        )
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Error message
            if (errorMessage.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFEBEE)
                        )
                    ) {
                        Text(
                            text = errorMessage,
                            color = Color(0xFFD32F2F),
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { validateAndExecuteCommands() },
                        enabled = !isAnimating && commandSequence.isNotEmpty() && !gameCompleted,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        )
                    ) {
                        Icon(Icons.Default.PlayArrow, "Play")
                        Spacer(Modifier.width(4.dp))
                        Text("Run")
                    }

                    Button(
                        onClick = { resetGame() },
                        enabled = !isAnimating,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF9800)
                        )
                    ) {
                        Icon(Icons.Default.Refresh, "Reset")
                        Spacer(Modifier.width(4.dp))
                        Text("Reset")
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Attempts: $attempts")
                        if (gameCompleted) {
                            Text("Score: $score", color = Color(0xFF4CAF50))
                            Text("🎉 Perfect! You found the path!", color = Color(0xFF4CAF50))
                        }
                    }
                }
            }
        }
    }
}