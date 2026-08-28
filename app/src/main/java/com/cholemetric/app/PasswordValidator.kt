package com.cholemetric.app

object PasswordValidator {

    fun hasMinLength(pass: String): Boolean = pass.length >= 6

    fun hasUppercase(pass: String): Boolean = pass.any { it.isUpperCase() }

    fun hasSpecialChar(pass: String): Boolean = pass.any { !it.isLetterOrDigit() }

    fun isValid(pass: String): Boolean =
        hasMinLength(pass) && hasUppercase(pass) && hasSpecialChar(pass)

    fun getValidationErrorMessage(pass: String): String {
        val missing = mutableListOf<String>()
        if (!hasMinLength(pass)) missing.add("at least 6 characters")
        if (!hasUppercase(pass)) missing.add("at least 1 uppercase letter")
        if (!hasSpecialChar(pass)) missing.add("at least 1 special character")

        return if (missing.isEmpty()) {
            ""
        } else {
            "Password must contain " + missing.joinToString(", ") + "."
        }
    }
}
