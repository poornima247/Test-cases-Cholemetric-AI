package com.cholemetric.app

import android.content.Context
import android.content.SharedPreferences

object ApiConfig {
    const val DEFAULT_EMULATOR_URL = "http://10.0.2.2/backend/gb_stone_api/"
    const val PREF_KEY_SERVER_URL = "SERVER_API_URL"

    private var _customUrl: String? = null

    var BASE_URL: String
        get() {
            val current = _customUrl ?: DEFAULT_EMULATOR_URL
            val trimmed = current.trim()
            return if (trimmed.endsWith("/")) trimmed else "$trimmed/"
        }
        set(value) {
            _customUrl = value.trim()
        }

    fun initFromPrefs(context: Context) {
        val prefs: SharedPreferences = context.getSharedPreferences("CholemetricPrefs", Context.MODE_PRIVATE)
        val savedUrl = prefs.getString(PREF_KEY_SERVER_URL, null)
        if (!savedUrl.isNullOrBlank()) {
            _customUrl = savedUrl.trim()
        }
    }

    fun saveServerUrl(context: Context, newUrl: String) {
        val trimmed = newUrl.trim()
        val formatted = if (trimmed.endsWith("/")) trimmed else "$trimmed/"
        _customUrl = formatted
        context.getSharedPreferences("CholemetricPrefs", Context.MODE_PRIVATE)
            .edit()
            .putString(PREF_KEY_SERVER_URL, formatted)
            .apply()
    }
}

