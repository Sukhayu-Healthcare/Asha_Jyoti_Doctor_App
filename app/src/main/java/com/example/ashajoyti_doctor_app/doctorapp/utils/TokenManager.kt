package com.example.ashajoyti_doctor_app.utils

import android.content.Context
import android.util.Log

object TokenManager {
    private const val TAG = "TokenManager"

    /**
     * Return valid Authorization header string or null.
     * Ensures a single "Bearer " prefix.
     */
    fun getAuthHeader(context: Context): String? {
        val raw = AuthPref.getToken(context) ?: return null
        val trimmed = raw.trim()
        return if (trimmed.startsWith("Bearer ", ignoreCase = true)) {
            Log.d(TAG, "Using stored token (already has Bearer).")
            trimmed
        } else {
            Log.d(TAG, "Adding Bearer prefix to stored token.")
            "Bearer $trimmed"
        }
    }

    // Store token (raw or with Bearer — keep as-is)
    fun saveTokenRaw(context: Context, token: String) {
        AuthPref.saveToken(context, token)
    }

    fun clear(context: Context) {
        AuthPref.clear(context)
    }
}
