package com.example.project3.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.project3.EducationalGameApplication
import com.example.project3.data.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepo: UserRepository): ViewModel() {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as EducationalGameApplication)
                LoginViewModel(application.userRepository)
            }
        }
    }
    var selectedUserType by mutableStateOf("")
    var emailInput by mutableStateOf("")
    var passwordInput by mutableStateOf("")
    var errorMessage by mutableStateOf("")
    var showErrors by mutableStateOf(false)
    var loginSuccess by mutableStateOf(false)
    var loginError by mutableStateOf("")

    /**
     * Authenticates the parent's email and password put in the Login Screen
     */
    fun authenticateParent(onResult: (Boolean, String) -> Unit) {
        loginError = ""

        viewModelScope.launch {
            try {
                val isAuthenticated = userRepo.authenticateParent(emailInput, passwordInput)

                if (isAuthenticated) {
                    loginSuccess = true
                    onResult(true, "")
                } else {
                    loginError = "Invalid email or password"
                    onResult(false, "Invalid email or password")
                }
            } catch (e: Exception) {
                loginError = "An error occurred during login"
                onResult(false, "An error occurred during login")
            }
        }
    }

    /**
     * Authenticates the child's email and password put in the Login Screen
     */
    fun authenticateChild(onResult: (Boolean, String) -> Unit) {
        loginError = ""

        viewModelScope.launch {
            try {
                val isAuthenticated = userRepo.authenticateChild(emailInput, passwordInput)

                if (isAuthenticated) {
                    loginSuccess = true
                    onResult(true, "")
                } else {
                    loginError = "Invalid email or password"
                    onResult(false, "Invalid email or password")
                }
            } catch (e: Exception) {
                loginError = "An error occurred during login"
                onResult(false, "An error occurred during login")
            }
        }
    }
}