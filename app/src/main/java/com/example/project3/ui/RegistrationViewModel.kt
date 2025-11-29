package com.example.project3.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.project3.EducationalGameApplication
import com.example.project3.data.Parent
import com.example.project3.data.UserRepository

class RegistrationViewModel(private val userRepo: UserRepository): ViewModel() {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as EducationalGameApplication)
                RegistrationViewModel(application.userRepository)
            }
        }
    }
    var firstNameInput by mutableStateOf("")
    var lastNameInput by mutableStateOf("")
    var emailInput by mutableStateOf("")
    var passwordInput by mutableStateOf("")

    fun registerParent(firstName: String, lastName: String, email: String, password: String) {
        userRepo.registerParent(Parent(
            firstName = firstName,
            lastName = lastName,
            email = email,
            password = password
        ))
    }
}