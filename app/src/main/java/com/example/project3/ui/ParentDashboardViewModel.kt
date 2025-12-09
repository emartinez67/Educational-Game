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

class ParentDashboardViewModel(private val userRepo: UserRepository): ViewModel() {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as EducationalGameApplication)
                ParentDashboardViewModel(application.userRepository)
            }
        }
    }

    var parentFirstName by mutableStateOf("")
    var parentId by mutableStateOf(0L)
    var children by mutableStateOf<List<Child>>(emptyList())
    var isLoading by mutableStateOf(false)

    /**
     * Gets the parent's data based off the email used to log in
     */
    fun loadParentData(email: String) {
        viewModelScope.launch {
            isLoading = true
            val parent = userRepo.getParentByEmail(email)
            parent?.let {
                parentFirstName = it.firstName
                parentId = it.id
            }
            isLoading = false
        }
    }

    /**
     * Gets a list of all of a parent's children from the database
     */
    fun loadChildren() {
        viewModelScope.launch {
            if (parentId != 0L) {
                children = userRepo.getChildrenByParentId(parentId)
            }
        }
    }

    /**
     * Deletes a child from the database and reloads them on the UI
     */
    fun deleteChild(child: Child) {
        viewModelScope.launch {
            userRepo.deleteChild(child)
            loadChildren()
        }
    }
}