package com.cholemetric.app

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object AuthManager {

    private const val PREFS_NAME = "CholemetricPrefs"
    private const val KEY_REGISTERED_USERS = "REGISTERED_USERS_JSON"

    fun initDefaultAccounts(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val usersStr = prefs.getString(KEY_REGISTERED_USERS, null)
        if (usersStr.isNullOrEmpty()) {
            val defaultUsers = JSONArray()
            
            // Seed default primary account for Poornima
            val poornimaObj = JSONObject().apply {
                put("id", 1)
                put("email", "poornimadandu246@gmail.com")
                put("password", "poornima123")
                put("full_name", "Dr. Poornima Dandu")
                put("hospital", "Cholemetric Diagnostics Center")
            }
            defaultUsers.put(poornimaObj)

            prefs.edit().putString(KEY_REGISTERED_USERS, defaultUsers.toString()).apply()
        }
    }

    fun registerUser(context: Context, name: String, hospital: String, email: String, pass: String): Boolean {
        initDefaultAccounts(context)
        val cleanEmail = email.trim().lowercase()
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val usersStr = prefs.getString(KEY_REGISTERED_USERS, "[]") ?: "[]"
        val usersArray = try { JSONArray(usersStr) } catch (e: Exception) { JSONArray() }

        val doctorName = if (name.isNotBlank()) {
            if (name.startsWith("Dr.", ignoreCase = true)) name else "Dr. $name"
        } else {
            val nameFromEmail = cleanEmail.substringBefore("@").replace(".", " ")
            "Dr. $nameFromEmail"
        }

        // Check if user with this email already exists
        for (i in 0 until usersArray.length()) {
            val user = usersArray.getJSONObject(i)
            if (user.optString("email").equals(cleanEmail, ignoreCase = true)) {
                user.put("password", pass)
                user.put("full_name", doctorName)
                if (hospital.isNotBlank()) user.put("hospital", hospital)
                prefs.edit().putString(KEY_REGISTERED_USERS, usersArray.toString()).apply()
                return true
            }
        }

        val newUserObj = JSONObject().apply {
            put("id", usersArray.length() + 1)
            put("email", cleanEmail)
            put("password", pass)
            put("full_name", doctorName)
            put("hospital", hospital)
        }
        usersArray.put(newUserObj)
        prefs.edit().putString(KEY_REGISTERED_USERS, usersArray.toString()).apply()
        return true
    }

    sealed class AuthResult {
        data class Success(val doctorId: Int, val fullName: String, val email: String) : AuthResult()
        object EmailNotRegistered : AuthResult()
        object WrongPassword : AuthResult()
    }

    fun authenticate(context: Context, email: String, pass: String): AuthResult {
        initDefaultAccounts(context)
        val cleanEmail = email.trim().lowercase()
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val usersStr = prefs.getString(KEY_REGISTERED_USERS, "[]") ?: "[]"
        val usersArray = try { JSONArray(usersStr) } catch (e: Exception) { JSONArray() }

        var foundUser: JSONObject? = null
        for (i in 0 until usersArray.length()) {
            val u = usersArray.getJSONObject(i)
            if (u.optString("email").equals(cleanEmail, ignoreCase = true)) {
                foundUser = u
                break
            }
        }

        if (foundUser == null) {
            return AuthResult.EmailNotRegistered
        }

        val storedPass = foundUser.optString("password")
        if (storedPass != pass) {
            return AuthResult.WrongPassword
        }

        return AuthResult.Success(
            doctorId = foundUser.optInt("id", 1),
            fullName = foundUser.optString("full_name", "Dr. Doctor"),
            email = cleanEmail
        )
    }

    fun updatePassword(context: Context, email: String, newPass: String): Boolean {
        initDefaultAccounts(context)
        val cleanEmail = email.trim().lowercase()
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val usersStr = prefs.getString(KEY_REGISTERED_USERS, "[]") ?: "[]"
        val usersArray = try { JSONArray(usersStr) } catch (e: Exception) { JSONArray() }

        for (i in 0 until usersArray.length()) {
            val user = usersArray.getJSONObject(i)
            if (user.optString("email").equals(cleanEmail, ignoreCase = true)) {
                user.put("password", newPass)
                prefs.edit().putString(KEY_REGISTERED_USERS, usersArray.toString()).apply()
                return true
            }
        }
        return false
    }

    fun isEmailRegistered(context: Context, email: String): Boolean {
        initDefaultAccounts(context)
        val cleanEmail = email.trim().lowercase()
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val usersStr = prefs.getString(KEY_REGISTERED_USERS, "[]") ?: "[]"
        val usersArray = try { JSONArray(usersStr) } catch (e: Exception) { JSONArray() }

        for (i in 0 until usersArray.length()) {
            if (usersArray.getJSONObject(i).optString("email").equals(cleanEmail, ignoreCase = true)) {
                return true
            }
        }
        return false
    }
}
