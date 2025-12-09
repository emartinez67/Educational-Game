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
fun Level2Game3Screen(
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

    // Stars on the map
    val stars = listOf(
        Pair(3, 3),
        Pair(6, 6),
        Pair(9, 9)
    )
    var collectedStars by remember { mutableStateOf(setOf<Pair<Int, Int>>()) }

    var commandSequence by remember { mutableStateOf(listOf<CommandType>()) }
    var functionA by remember { mutableStateOf(listOf<CommandType>()) }
    var functionB by remember { mutableStateOf(listOf<CommandType>()) }

    var isAnimating by remember { mutableStateOf(false) }
    var gameCompleted by remember { mutableStateOf(false) }
    var attempts by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var errorMessage by remember { mutableStateOf("") }
    var showHint by remember { mutableStateOf(false) }
    var editingFunction by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    val animatedOffsetX by animateFloatAsState(
        targetValue = playerPosition.first.toFloat(),
        animationSpec = tween(durationMillis = 350, easing = LinearEasing),
        label = "playerX"
    )

    val animatedOffsetY by animateFloatAsState(
        targetValue = playerPosition.second.toFloat(),
        animationSpec = tween(durationMillis = 350, easing = LinearEasing),
        label = "playerY"
    )

    fun expandLoopsAndFunctions(commands: List<CommandType>, depth: Int = 0): List<CommandType> {
        if (depth > 10) {
            errorMessage = "❌ Too many nested calls!"
            return emptyList()
        }

        val expanded = mutableListOf<CommandType>()
        var i = 0
        while (i < commands.size) {
            when (commands[i]) {
                CommandType.LOOP_START -> {
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
                            expanded.addAll(expandLoopsAndFunctions(loopBody, depth + 1))
                        }
                        i = endIndex
                    } else {
                        errorMessage = "❌ Loop not properly closed!"
                        return emptyList()
                    }
                }
                CommandType.FUNCTION_A -> {
                    if (functionA.isEmpty()) {
                        errorMessage = "❌ Function F1 is empty! Define it first."
                        return emptyList()
                    }
                    expanded.addAll(expandLoopsAndFunctions(functionA, depth + 1))
                    i++
                }
                CommandType.FUNCTION_B -> {
                    if (functionB.isEmpty()) {
                        errorMessage = "❌ Function F2 is empty! Define it first."
                        return emptyList()
                    }
                    expanded.addAll(expandLoopsAndFunctions(functionB, depth + 1))
                    i++
                }
                CommandType.LOOP_END -> {
                    i++
                }
                else -> {
                    expanded.add(commands[i])
                    i++
                }
            }
        }
        return expanded
    }

    fun executeCommands() {
        if (isAnimating || commandSequence.isEmpty()) return

        errorMessage = ""
        isAnimating = true
        attempts++

        val expandedCommands = expandLoopsAndFunctions(commandSequence)
        if (expandedCommands.isEmpty() && errorMessage.isNotEmpty()) {
            isAnimating = false
            return
        }

        var currentPos = Pair(0, 0)
        val newCollectedStars = collectedStars.toMutableSet()

        coroutineScope.launch {
            for (command in expandedCommands) {
                delay(400)
                currentPos = when (command) {
                    CommandType.MOVE_UP -> Pair(currentPos.first, maxOf(0, currentPos.second - 1))
                    CommandType.MOVE_DOWN -> Pair(currentPos.first, minOf(11, currentPos.second + 1))
                    CommandType.MOVE_RIGHT -> Pair(minOf(11, currentPos.first + 1), currentPos.second)
                    CommandType.MOVE_LEFT -> Pair(maxOf(0, currentPos.first - 1), currentPos.second)
                    else -> currentPos
                }
                playerPosition = currentPos

                if (stars.contains(currentPos) && !newCollectedStars.contains(currentPos)) {
                    newCollectedStars.add(currentPos)
                    collectedStars = newCollectedStars.toSet()
                }

                try {
                    val mediaPlayer = MediaPlayer.create(context, R.raw.move_sound)
                    mediaPlayer?.start()
                    mediaPlayer?.setOnCompletionListener { it.release() }
                } catch (e: Exception) {
                }
            }

            delay(500)

            if (collectedStars.size == stars.size) {
                gameCompleted = true
                val usedFunctions = commandSequence.contains(CommandType.FUNCTION_A) ||
                        commandSequence.contains(CommandType.FUNCTION_B)
                val usedLoops = commandSequence.contains(CommandType.LOOP_START) ||
                        functionA.contains(CommandType.LOOP_START) ||
                        functionB.contains(CommandType.LOOP_START)

                score = when {
                    usedFunctions && usedLoops -> maxOf(100 - (attempts - 1) * 7, 60)
                    usedFunctions -> maxOf(85 - (attempts - 1) * 7, 50)
                    else -> maxOf(70 - (attempts - 1) * 7, 30)
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
                errorMessage = "❌ Collected ${collectedStars.size}/${stars.size} stars. Collect all stars!"
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
        collectedStars = emptySet()
        commandSequence = emptyList()
        gameCompleted = false
        isAnimating = false
        errorMessage = ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Level 2 - Game 3: Function Master") },
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
                            text = "🎓 New Concept: Functions!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0277BD)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Functions let you define reusable sequences! Define F1 and F2 below, then use them in your main sequence.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tip: You can use loops inside functions and call functions multiple times!",
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = Color.Gray
                        )
                    }
                }
            }
            item {
                Text(
                    text = "Collect all 3 stars!",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Stars at: (3,3), (6,6), (9,9)",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .size(385.dp)
                        .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                        .border(2.dp, Color(0xFF4CAF50), RoundedCornerShape(8.dp))
                        .padding(2.dp)
                ) {
                    for (row in 0..11) {
                        for (col in 0..11) {
                            Box(
                                modifier = Modifier
                                    .offset {
                                        IntOffset(
                                            (col * 32).dp.roundToPx(),
                                            (row * 32).dp.roundToPx()
                                        )
                                    }
                                    .size(32.dp)
                                    .border(1.dp, Color.Gray.copy(alpha = 0.2f))
                            )
                        }
                    }
                    stars.forEach { star ->
                        if (!collectedStars.contains(star)) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Star",
                                modifier = Modifier
                                    .offset {
                                        IntOffset(
                                            (star.first * 32 + 6).dp.roundToPx(),
                                            (star.second * 32 + 6).dp.roundToPx()
                                        )
                                    }
                                    .size(20.dp),
                                tint = Color(0xFFFFC107)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    (animatedOffsetX * 32 + 6).dp.roundToPx(),
                                    (animatedOffsetY * 32 + 6).dp.roundToPx()
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
                Text(
                    text = "Stars: ${collectedStars.size}/${stars.size}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
            item {
                Text(
                    text = "Define Your Functions:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE0F7FA)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                "Function F1",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                                    .background(Color.White, RoundedCornerShape(8.dp))
                                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                                    .padding(4.dp)
                            ) {
                                if (functionA.isEmpty()) {
                                    Text(
                                        "Empty",
                                        color = Color.Gray,
                                        fontSize = 12.sp,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                } else {
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        items(functionA) { cmd ->
                                            val command = GameCommands.LEVEL_2_COMMANDS.find { it.type == cmd }
                                            command?.let {
                                                Box(
                                                    modifier = Modifier
                                                        .size(45.dp)
                                                        .background(it.color, RoundedCornerShape(4.dp)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        it.label,
                                                        color = Color.White,
                                                        fontSize = if (it.label.length > 1) 16.sp else 20.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            Button(
                                onClick = { editingFunction = if (editingFunction == "A") null else "A" },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (editingFunction == "A") Color(0xFFFF9800) else Color(0xFF00BCD4)
                                )
                            ) {
                                Text(
                                    if (editingFunction == "A") "Done" else "Edit",
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE0F2F1)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                "Function F2",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                                    .background(Color.White, RoundedCornerShape(8.dp))
                                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                                    .padding(4.dp)
                            ) {
                                if (functionB.isEmpty()) {
                                    Text(
                                        "Empty",
                                        color = Color.Gray,
                                        fontSize = 12.sp,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                } else {
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        items(functionB) { cmd ->
                                            val command = GameCommands.LEVEL_2_COMMANDS.find { it.type == cmd }
                                            command?.let {
                                                Box(
                                                    modifier = Modifier
                                                        .size(45.dp)
                                                        .background(it.color, RoundedCornerShape(4.dp)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        it.label,
                                                        color = Color.White,
                                                        fontSize = if (it.label.length > 1) 16.sp else 20.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            Button(
                                onClick = { editingFunction = if (editingFunction == "B") null else "B" },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (editingFunction == "B") Color(0xFFFF9800) else Color(0xFF009688)
                                )
                            ) {
                                Text(
                                    if (editingFunction == "B") "Done" else "Edit",
                                    fontSize = 12.sp
                                )
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
                    text = if (editingFunction != null) "Editing Function F$editingFunction:" else "Main Command Sequence:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (editingFunction != null) Color(0xFFFF9800) else Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            item {
                val currentSequence = when (editingFunction) {
                    "A" -> functionA
                    "B" -> functionB
                    else -> commandSequence
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(
                            if (editingFunction != null) Color(0xFFFFF3E0) else Color.White,
                            RoundedCornerShape(8.dp)
                        )
                        .border(
                            2.dp,
                            if (editingFunction != null) Color(0xFFFF9800) else Color.Gray,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                ) {
                    if (currentSequence.isEmpty()) {
                        Text(
                            "Drag commands here",
                            color = Color.Gray,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(currentSequence.withIndex().toList()) { (index, command) ->
                                val cmd = GameCommands.LEVEL_2_COMMANDS.find { it.type == command }
                                cmd?.let {
                                    Level2CommandChip(
                                        command = it,
                                        onRemove = {
                                            when (editingFunction) {
                                                "A" -> functionA = functionA.toMutableList().apply { removeAt(index) }
                                                "B" -> functionB = functionB.toMutableList().apply { removeAt(index) }
                                                else -> commandSequence = commandSequence.toMutableList().apply { removeAt(index) }
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
                                        when (editingFunction) {
                                            "A" -> functionA = functionA + command.type
                                            "B" -> functionB = functionB + command.type
                                            else -> commandSequence = commandSequence + command.type
                                        }
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
                                        when (editingFunction) {
                                            "A" -> functionA = functionA + command.type
                                            "B" -> functionB = functionB + command.type
                                            else -> commandSequence = commandSequence + command.type
                                        }
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
                            text = "💡 Hint: Each star is 3 steps right and 3 steps down from the previous position. Create a function for this pattern, then call it 3 times!",
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
                        enabled = !isAnimating && commandSequence.isNotEmpty() && !gameCompleted && editingFunction == null,
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
                        enabled = !isAnimating && editingFunction == null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF9800)
                        )
                    ) {
                        Icon(Icons.Default.Refresh, "Reset")
                        Spacer(Modifier.width(4.dp))
                        Text("Reset")
                    }
                    Button(
                        onClick = {
                            functionA = emptyList()
                            functionB = emptyList()
                        },
                        enabled = !isAnimating && editingFunction == null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF9E9E9E)
                        )
                    ) {
                        Text("Clear Functions", fontSize = 12.sp)
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
                            val usedFunctions = commandSequence.contains(CommandType.FUNCTION_A) ||
                                    commandSequence.contains(CommandType.FUNCTION_B)
                            if (usedFunctions) {
                                Text("🏆 Outstanding! You're a programming master!", color = Color(0xFF4CAF50))
                            } else {
                                Text("✅ Well done! Try using functions for efficiency!", color = Color(0xFF4CAF50))
                            }
                        }
                    }
                }
            }
        }
    }
}