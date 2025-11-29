package com.example.project3.data

class Validation {
    fun validateFirstName(name: String): String {
        if (name.isEmpty()) {
            return "First name cannot be empty\n"
        }

        var errorMessage = ""

        if (name.length < 3) {
            errorMessage += "First name must be at least 3 characters long\n"
        }
        if (name.length > 30) {
            errorMessage += "First name must be less than 30 characters long\n"
        }
        if (!name.all { it.isLetter() }) {
            errorMessage += "First name must only contain letters\n"
        }

        return errorMessage
    }

    fun validateLastName(name: String): String {
        if (name.isEmpty()) {
            return "Last name cannot be empty\n"
        }

        var errorMessage = ""

        if (name.length < 3) {
            errorMessage += "Last name must be at least 3 characters long\n"
        }
        if (name.length > 30) {
            errorMessage += "Last name must be less than 30 characters long\n"
        }
        if (!name.all { it.isLetter() }) {
            errorMessage += "Last name must only contain letters\n"
        }

        return errorMessage
    }

    fun validateEmail(email: String): String {
        if (email.isEmpty()) {
            return "Email cannot be empty\n"
        }

        var errorMessage = ""

        val emailRegex = Regex("^[A-Za-z](.*)([@])(.+)(\\.)(.+)")
        if (!emailRegex.matches(email)) {
            errorMessage += "Email must be in the format example@example.com\n"
        }

        return errorMessage
    }

    fun validatePassword(password: String): String {
        if (password.isEmpty()) {
            return "Password cannot be empty\n"
        }

        var errorMessage = "";

        if (password.length < 8) {
            errorMessage += "Password must be at least 8 characters long\n"
        }
        if (!password.any { it.isUpperCase() }) {
            errorMessage += "Password must contain at least one uppercase letter\n"
        }
        if (!password.any { it.isLowerCase() }) {
            errorMessage += "Password must contain at least one lowercase letter\n"
        }
        if (!password.any { it.isDigit() }) {
            errorMessage += "Password must contain at least one digit\n"
        }
        if (!password.any { it in "@\$!%*?&" }) {
            errorMessage += "Password must contain at least one special character\n"
        }

        return errorMessage
    }

    fun validateRegistration(firstName: String, lastName: String, email: String, password: String): String {
        var errorMessage = ""

        errorMessage += validateFirstName(firstName)
        errorMessage += validateLastName(lastName)
        errorMessage += validateEmail(email)
        errorMessage += validatePassword(password)

        return errorMessage
    }

    fun validateLogin(email: String, password: String): String {
        var errorMessage = ""

        errorMessage += validateEmail(email)
        errorMessage += validatePassword(password)

        return errorMessage
    }
}