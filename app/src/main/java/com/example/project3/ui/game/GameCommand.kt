package com.example.project3.game

import androidx.compose.ui.graphics.Color

enum class CommandType {
    MOVE_UP,
    MOVE_DOWN,
    MOVE_LEFT,
    MOVE_RIGHT,
    LOOP_START,
    LOOP_END,
    FUNCTION_A,
    FUNCTION_B
}

data class GameCommand(
    val type: CommandType,
    val label: String,
    val color: Color,
    val description: String = ""
)

object GameCommands {
    val LEVEL_1_COMMANDS = listOf(
        GameCommand(CommandType.MOVE_UP, "↑", Color(0xFF4CAF50), "Move Up"),
        GameCommand(CommandType.MOVE_DOWN, "↓", Color(0xFF2196F3), "Move Down"),
        GameCommand(CommandType.MOVE_LEFT, "←", Color(0xFFE91E63), "Move Left"),
        GameCommand(CommandType.MOVE_RIGHT, "→", Color(0xFFFFC107), "Move Right")
    )

    val LEVEL_2_COMMANDS = listOf(
        GameCommand(CommandType.MOVE_UP, "↑", Color(0xFF4CAF50), "Move Up"),
        GameCommand(CommandType.MOVE_DOWN, "↓", Color(0xFF2196F3), "Move Down"),
        GameCommand(CommandType.MOVE_LEFT, "←", Color(0xFFE91E63), "Move Left"),
        GameCommand(CommandType.MOVE_RIGHT, "→", Color(0xFFFFC107), "Move Right"),
        GameCommand(CommandType.LOOP_START, "🔁", Color(0xFF9C27B0), "Loop 2x"),
        GameCommand(CommandType.LOOP_END, "⏹", Color(0xFF673AB7), "End Loop"),
        GameCommand(CommandType.FUNCTION_A, "F1", Color(0xFF00BCD4), "Function 1"),
        GameCommand(CommandType.FUNCTION_B, "F2", Color(0xFF009688), "Function 2")
    )
}