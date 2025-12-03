package com.example.project3.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.project3.EducationalGameApplication
import com.example.project3.data.UserRepository
import kotlinx.coroutines.launch

class ChildDashboardViewModel(private val userRepo: UserRepository): ViewModel() {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as EducationalGameApplication)
                ChildDashboardViewModel(application.userRepository)
            }
        }
    }

    var childFirstName by mutableStateOf("")
    var childId by mutableStateOf(0L)
    var completedGames by mutableStateOf(0)
    var totalScore by mutableStateOf(0)
    var averageScore by mutableStateOf(0.0)
    var isLevel2Unlocked by mutableStateOf(false)
    var isLoading by mutableStateOf(false)

    fun loadChildData(email: String) {
        viewModelScope.launch {
            isLoading = true
            val child = userRepo.getChildByEmail(email)
            child?.let {
                childFirstName = it.firstName
                childId = it.id
                loadProgressStats()
            }
            isLoading = false
        }
    }

    private suspend fun loadProgressStats() {
        completedGames = userRepo.getTotalCompletedGames(childId)
        totalScore = userRepo.getTotalScore(childId) ?: 0
        averageScore = userRepo.getAverageScore(childId) ?: 0.0

        val level1Progress = userRepo.getProgressForLevel(childId, 1)
        val level1GamesCompleted = level1Progress.filter { it.completed }.map { it.gameNumber }.distinct().size
        isLevel2Unlocked = level1GamesCompleted >= 3
    }
}