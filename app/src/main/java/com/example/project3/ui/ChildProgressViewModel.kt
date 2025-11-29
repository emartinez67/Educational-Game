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

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}