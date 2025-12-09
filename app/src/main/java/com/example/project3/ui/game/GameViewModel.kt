package com.example.project3.ui.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.project3.EducationalGameApplication
import com.example.project3.data.GameProgress
import com.example.project3.data.UserRepository
import kotlinx.coroutines.launch

class GameViewModel(private val userRepo: UserRepository) : ViewModel() {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as EducationalGameApplication)
                GameViewModel(application.userRepository)
            }
        }
    }

    var isSavingProgress by mutableStateOf(false)
    var saveError by mutableStateOf<String?>(null)

    /**
     * Saves the game progress to the database
     */
    fun saveGameProgress(
        childEmail: String,
        level: Int,
        gameNumber: Int,
        score: Int,
        attempts: Int,
        onComplete: () -> Unit = {}
    ) {
        viewModelScope.launch {
            isSavingProgress = true
            saveError = null

            try {
                val child = userRepo.getChildByEmail(childEmail)

                if (child != null) {
                    val progress = GameProgress(
                        childId = child.id,
                        level = level,
                        gameNumber = gameNumber,
                        score = score,
                        completed = true,
                        attempts = attempts
                    )

                    userRepo.saveGameProgress(progress)
                    onComplete()
                } else {
                    saveError = "Child not found"
                }
            } catch (e: Exception) {
                saveError = "Error saving progress: ${e.message}"
            } finally {
                isSavingProgress = false
            }
        }
    }
}