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
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Level1Game1Screen(
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
    var targetPosition by remember { mutableStateOf(Pair(4, 4)) }
    var commandSequence by remember { mutableStateOf(listOf<CommandType>()) }
    var isAnimating by remember { mutableStateOf(false) }
    var gameCompleted by remember { mutableStateOf(false) }
    var attempts by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }

    val coroutineScope = rememberCoroutineScope()

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

    fun executeCommands() {
        if (isAnimating || commandSequence.isEmpty()) return

        isAnimating = true
        attempts++
        var currentPos = Pair(0, 0)

        coroutineScope.launch {
            for (command in commandSequence) {
                delay(600)
                currentPos = when (command) {
                    CommandType.MOVE_UP -> Pair(currentPos.first, maxOf(0, currentPos.second - 1))
                    CommandType.MOVE_DOWN -> Pair(currentPos.first, minOf(4, currentPos.second + 1))
                    CommandType.MOVE_RIGHT -> Pair(minOf(4, currentPos.first + 1), currentPos.second)
                    CommandType.MOVE_LEFT -> Pair(maxOf(0, currentPos.first - 1), currentPos.second)
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
            } else {
                try {
                    val mediaPlayer = MediaPlayer.create(context, R.raw.error_sound)
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
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Level 1 - Game $gameNumber") },
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
                    text = "Help the character reach the star!",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                        .border(2.dp, Color(0xFF4CAF50), RoundedCornerShape(8.dp))
                ) {
                    for (row in 0..4) {
                        for (col in 0..4) {
                            Box(
                                modifier = Modifier
                                    .offset {
                                        IntOffset(
                                            (col * 60).dp.roundToPx(),
                                            (row * 60).dp.roundToPx()
                                        )
                                    }
                                    .size(60.dp)
                                    .border(1.dp, Color.Gray.copy(alpha = 0.3f))
                            )
                        }
                    }
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Target",
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    (targetPosition.first * 60 + 15).dp.roundToPx(),
                                    (targetPosition.second * 60 + 15).dp.roundToPx()
                                )
                            }
                            .size(30.dp)
                            .then(
                                if (gameCompleted) Modifier
                                    .offset(y = (-5).dp)
                                else Modifier
                            ),
                        tint = Color(0xFFFFC107)
                    )
                    Box(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    (animatedOffsetX * 60 + 15).dp.roundToPx(),
                                    (animatedOffsetY * 60 + 15).dp.roundToPx()
                                )
                            }
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2196F3)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("😊", fontSize = 20.sp)
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
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { executeCommands() },
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
                            Text("🎉 Success! Great job!", color = Color(0xFF4CAF50))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DraggableCommand(
    command: GameCommand,
    onDragEnd: (Boolean) -> Unit
) {
    var dragOffset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .offset { IntOffset(dragOffset.x.roundToInt(), dragOffset.y.roundToInt()) }
            .size(70.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(command.color)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        onDragEnd(dragOffset.y < -100)
                        dragOffset = Offset.Zero
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragOffset += dragAmount
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            command.label,
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CommandChip(
    command: GameCommand,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(command.color)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = { onRemove() },
                    onDrag = { _, _ -> }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            command.label,
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
    }
}