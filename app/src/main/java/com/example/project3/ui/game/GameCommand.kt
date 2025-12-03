package com.example.project3.game

import androidx.compose.ui.graphics.Color

enum class CommandType {
    MOVE_UP,
    MOVE_DOWN,
    MOVE_LEFT,
    MOVE_RIGHT,
    JUMP,
    TURN_LEFT,
    TURN_RIGHT
}

data class GameCommand(
    val type: CommandType,
    val label: String,
    val color: Color
)

object GameCommands {
    val LEVEL_1_COMMANDS = listOf(
        GameCommand(CommandType.MOVE_UP, "↑", Color(0xFF4CAF50)),
        GameCommand(CommandType.MOVE_DOWN, "↓", Color(0xFF2196F3)),
        GameCommand(CommandType.MOVE_LEFT, "←", Color(0xFFE91E63)),
        GameCommand(CommandType.MOVE_RIGHT, "→", Color(0xFFFFC107))
    )
}