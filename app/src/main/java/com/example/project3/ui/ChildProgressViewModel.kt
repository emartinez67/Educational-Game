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
import com.example.project3.data.GameProgress
import com.example.project3.data.UserRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ChildProgressViewModel(private val userRepo: UserRepository): ViewModel() {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as EducationalGameApplication)
                ChildProgressViewModel(application.userRepository)
            }
        }
    }

    var childFirstName by mutableStateOf("")
    var childLastName by mutableStateOf("")
    var childEmail by mutableStateOf("")
    var accountCreated by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    var totalGamesCompleted by mutableStateOf(0)
    var totalScore by mutableStateOf(0)
    var averageScore by mutableStateOf(0.0)
    var progressData by mutableStateOf<List<GameProgress>>(emptyList())

    /**
     * Gets child's data from the database using the email logged in with
     */
    fun loadChildData(email: String) {
        viewModelScope.launch {
            isLoading = true
            val child = userRepo.getChildByEmail(email)
            child?.let {
                childFirstName = it.firstName
                childLastName = it.lastName
                childEmail = it.email
                accountCreated = formatDate(it.creationTime)
            }
            isLoading = false
        }
    }

    /**
     * Get's the child's progress data from the database
     */
    fun loadProgressData(email: String) {
        viewModelScope.launch {
            val child = userRepo.getChildByEmail(email)
            child?.let {
                progressData = userRepo.getChildProgress(it.id)
                totalGamesCompleted = userRepo.getTotalCompletedGames(it.id)
                totalScore = userRepo.getTotalScore(it.id) ?: 0
                averageScore = userRepo.getAverageScore(it.id) ?: 0.0
            }
        }
    }

    /**
     * Formats date
     */
    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}