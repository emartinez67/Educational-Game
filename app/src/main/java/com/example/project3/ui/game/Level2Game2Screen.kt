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
fun Level2Game2Screen(
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
    val targetPosition = Pair(10, 10)
    var commandSequence by remember { mutableStateOf(listOf<CommandType>()) }
    var isAnimating by remember { mutableStateOf(false) }
    var gameCompleted by remember { mutableStateOf(false) }
    var attempts by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var errorMessage by remember { mutableStateOf("") }
    var showHint by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val validPath = mutableSetOf<Pair<Int, Int>>()
    for (i in 0..10) {
        for (j in 0..i) {
            validPath.add(Pair(j, i))
        }
    }

    val animatedOffsetX by animateFloatAsState(
        targetValue = playerPosition.first.toFloat(),
        animationSpec = tween(durationMillis = 400, easing = LinearEasing),
        label = "playerX"
    )

    val animatedOffsetY by animateFloatAsState(
        targetValue = playerPosition.second.toFloat(),
        animationSpec = tween(durationMillis = 400, easing = LinearEasing),
        label = "playerY"
    )

    fun expandLoops(commands: List<CommandType>): List<CommandType> {
        val expanded = mutableListOf<CommandType>()
        var i = 0
        while (i < commands.size) {
            if (commands[i] == CommandType.LOOP_START) {
                var loopDepth = 1
                var endIndex = i + 1
                while (endIndex < commands.size && loopDepth > 0) {
                    if (commands[endIndex] == CommandType.LOOP_START) loopDepth++
                    if (commands[endIndex] == CommandType.LOOP_END) loopDepth--
                    endIndex++
                }

                if (loopDepth == 0) {
                    val loopBody = commands.subList(i + 1, endIndex - 1)
                    repeat(2) {
                        expanded.addAll(loopBody)
                    }
                    i = endIndex
                } else {
                    errorMessage = "❌ Loop not properly closed!"
                    return emptyList()
                }
            } else if (commands[i] != CommandType.LOOP_END) {
                expanded.add(commands[i])
                i++
            } else {
                i++
            }
        }
        return expanded
    }

    fun executeCommands() {
        if (isAnimating || commandSequence.isEmpty()) return

        errorMessage = ""
        isAnimating = true
        attempts++

        val expandedCommands = expandLoops(commandSequence)
        if (expandedCommands.isEmpty() && errorMessage.isNotEmpty()) {
            isAnimating = false
            return
        }

        var currentPos = Pair(0, 0)
        var pathValid = true

        coroutineScope.launch {
            for (command in expandedCommands) {
                delay(450)
                val nextPos = when (command) {
                    CommandType.MOVE_UP -> Pair(currentPos.first, maxOf(0, currentPos.second - 1))
                    CommandType.MOVE_DOWN -> Pair(currentPos.first, minOf(10, currentPos.second + 1))
                    CommandType.MOVE_RIGHT -> Pair(minOf(10, currentPos.first + 1), currentPos.second)
                    CommandType.MOVE_LEFT -> Pair(maxOf(0, currentPos.first - 1), currentPos.second)
                    else -> currentPos
                }

                if (!validPath.contains(nextPos)) {
                    pathValid = false
                    errorMessage = "❌ Hit a wall! Stay on the staircase path."
                    break
                }

                currentPos = nextPos
                playerPosition = currentPos

                try {
                    val mediaPlayer = MediaPlayer.create(context, R.raw.move_sound)
                    mediaPlayer?.start()
                    mediaPlayer?.setOnCompletionListener { it.release() }
                } catch (e: Exception) {
                }
            }

            delay(500)

            if (!pathValid) {
                try {
                    val mediaPlayer = MediaPlayer.create(context, R.raw.error_sound)
                    mediaPlayer?.start()
                    mediaPlayer?.setOnCompletionListener { it.release() }
                } catch (e: Exception) {
                }
            } else if (currentPos == targetPosition) {
                gameCompleted = true
                val usedLoops = commandSequence.contains(CommandType.LOOP_START)
                score = if (usedLoops) {
                    maxOf(100 - (attempts - 1) * 8, 50)
                } else {
                    maxOf(70 - (attempts - 1) * 8, 30)
                }

                gameViewModel.saveGameProgress(
                    childEmail = childEmail,
                    level = 2,
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
                errorMessage = "❌ Didn't reach the target. Try again!"
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
        errorMessage = ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Level 2 - Game 2: Staircase Challenge") },
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
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE1F5FE)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "🎯 Challenge: Climb the Staircase!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0277BD)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Follow the staircase pattern. Gray areas are walls - don't step there!",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            item {
                Text(
                    text = "Climb to the top efficiently!",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .size(385.dp)
                        .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                        .border(2.dp, Color(0xFF4CAF50), RoundedCornerShape(8.dp))
                        .padding(5.dp)
                ) {
                    for (row in 0..10) {
                        for (col in 0..10) {
                            val isValidCell = validPath.contains(Pair(col, row))
                            Box(
                                modifier = Modifier
                                    .offset {
                                        IntOffset(
                                            (col * 34).dp.roundToPx(),
                                            (row * 34).dp.roundToPx()
                                        )
                                    }
                                    .size(34.dp)
                                    .background(
                                        if (isValidCell) Color.Transparent
                                        else Color.Gray.copy(alpha = 0.7f)
                                    )
                                    .border(
                                        1.dp,
                                        if (isValidCell) Color.Gray.copy(alpha = 0.3f)
                                        else Color.Gray.copy(alpha = 0.8f)
                                    )
                            )
                        }
                    }

                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Target",
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    (targetPosition.first * 34 + 7).dp.roundToPx(),
                                    (targetPosition.second * 34 + 7).dp.roundToPx()
                                )
                            }
                            .size(20.dp),
                        tint = Color(0xFFFFC107)
                    )

                    Box(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    (animatedOffsetX * 34 + 7).dp.roundToPx(),
                                    (animatedOffsetY * 34 + 7).dp.roundToPx()
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
                        .height(100.dp)
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
                                val cmd = GameCommands.LEVEL_2_COMMANDS.find { it.type == command }
                                cmd?.let {
                                    Level2CommandChip(
                                        command = it,
                                        onRemove = {
                                            commandSequence = commandSequence.toMutableList().apply {
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
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        GameCommands.LEVEL_2_COMMANDS.take(4).forEach { command ->
                            Level2DraggableCommand(
                                command = command,
                                onDragEnd = { wasDropped ->
                                    if (wasDropped && !isAnimating) {
                                        commandSequence = commandSequence + command.type
                                    }
                                }
                            )
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        GameCommands.LEVEL_2_COMMANDS.drop(4).forEach { command ->
                            Level2DraggableCommand(
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
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                TextButton(
                    onClick = { showHint = !showHint }
                ) {
                    Text(if (showHint) "Hide Hint 👁️" else "Show Hint 💡")
                }
            }
            if (showHint) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFF9C4)
                        )
                    ) {
                        Text(
                            text = "💡 Hint: Notice the pattern? Each step: move right, then down. You need to do this pattern multiple times with increasing lengths. Use loops!",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
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
                            Text("Score: $score", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                            val usedLoops = commandSequence.contains(CommandType.LOOP_START)
                            if (usedLoops) {
                                Text("🏆 Amazing! You mastered the staircase!", color = Color(0xFF4CAF50))
                            } else {
                                Text("✅ Good job! Try loops for a better score!", color = Color(0xFF4CAF50))
                            }
                        }
                    }
                }
            }
        }
    }
}