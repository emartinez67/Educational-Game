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
import com.example.project3.data.Child
import com.example.project3.data.UserRepository
import kotlinx.coroutines.launch

class RegisterChildViewModel(private val userRepo: UserRepository): ViewModel() {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as EducationalGameApplication)
                RegisterChildViewModel(application.userRepository)
            }
        }
    }

    var firstNameInput by mutableStateOf("")
    var lastNameInput by mutableStateOf("")
    var emailInput by mutableStateOf("")
    var passwordInput by mutableStateOf("")
    var showErrors by mutableStateOf(false)
    var errorMessage by mutableStateOf("")
    var parentId by mutableStateOf(0L)

    fun loadParentId(parentEmail: String) {
        viewModelScope.launch {
            val parent = userRepo.getParentByEmail(parentEmail)
            parent?.let {
                parentId = it.id
            }
        }
    }

    fun registerChild(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                if (parentId != 0L) {
                    userRepo.registerChild(
                        Child(
                            firstName = firstNameInput,
                            lastName = lastNameInput,
                            email = emailInput,
                            password = passwordInput,
                            parentId = parentId
                        )
                    )
                    onResult(true)
                } else {
                    errorMessage = "Parent not found"
                    onResult(false)
                }
            } catch (e: Exception) {
                errorMessage = "An error occurred during registration"
                onResult(false)
            }
        }
    }
}