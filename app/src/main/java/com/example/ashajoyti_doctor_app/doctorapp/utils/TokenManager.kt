package com.example.ashajoyti_doctor_app.utils

import android.content.Context
import android.util.Log

object TokenManager {
    private const val TAG = "TokenManager"

    /**
     * Return valid Authorization header string or null.
     * Ensures a single "Bearer " prefix and strips surrounding quotes/whitespace.
     */
    fun getAuthHeader(context: Context): String? {
        val raw = AuthPref.getToken(context) ?: return null
        // strip whitespace and any surrounding double quotes that may have been stored
        var token = raw.trim()
        if (token.length >= 2 && token.startsWith("\"") && token.endsWith("\"")) {
            token = token.substring(1, token.length - 1).trim()
        }
        if (token.isEmpty()) return null

        return if (token.startsWith("Bearer ", ignoreCase = true)) {
            Log.d(TAG, "Using stored token (already has Bearer).")
            token
        } else {
            Log.d(TAG, "Adding Bearer prefix to stored token.")
            "Bearer $token"
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
